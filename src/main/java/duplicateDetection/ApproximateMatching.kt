package duplicateDetection

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.ThresholdAMathViaSemiLocal
import sequenceAlignment.ISemiLocalProvider
import com.brein.time.timeintervals.collections.ListIntervalCollection
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder.IntervalType
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder
import com.brein.time.timeintervals.intervals.IntegerInterval
import longestCommonSubsequence.AbstractMongeMatrix
import longestCommonSubsequence.ReducingKernelEvaluation
import sequenceAlignment.ImplicitSemiLocalSA
import utils.*
import java.util.stream.Collectors
import kotlin.math.max


/**
 *
 */
interface IApproximateMatching<T> {

    /**
     * For a given threshold in [0,1] according to scoring scheme find
     */
    fun find(p: List<T>, text: List<T>): MutableList<Interval>
}


/**
 * Approximate matching via thresholdAMatch
 */
class ApproximateMatchingViaThresholdAMatch<T>(
        var provider: ISemiLocalProvider,
        var scheme: IScoringScheme,
        thresholdPercent: Double
) : IApproximateMatching<T> {

    private val badPercent = (1 - thresholdPercent) * 3 / 4
    private val approximatePercent = (1 - thresholdPercent) * 1 / 4
    private val goodPercent = thresholdPercent

    override fun find(p: List<T>, text: List<T>): MutableList<Interval> {
        val solution = provider.buildSolution(p, text, scheme)

        //exactMatchOverall*percent
        val realThreshold = p.size * (scheme.getMatchScore().toDouble() * (goodPercent - approximatePercent)
                + (scheme.getMismatchScore() + scheme.getGapScore()).toDouble() * badPercent)

        val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(solution)
        val clones = ThresholdAMathViaSemiLocal(aMatch).solve(realThreshold)
        return clones.toMutableList()
    }
}

/**
 * Approximate matching via thresholdAMatch
 */
class ApproximateMatchingViaCut<T>(
        var provider: ISemiLocalProvider,
        var scheme: IScoringScheme,
        thresholdPercent: Double
) : IApproximateMatching<T> {

    private val badPercent = (1 - thresholdPercent) * 3 / 4
    private val approximatePercent = (1 - thresholdPercent) * 1 / 4
    private val goodPercent = thresholdPercent

    override fun find(p: List<T>, text: List<T>): MutableList<Interval> {
        val solution = provider.buildSolution(p, text, scheme)

        //exactMatchOverall*percent
        val realThreshold = p.size * (scheme.getMatchScore().toDouble() * (goodPercent - approximatePercent)
                + (scheme.getMismatchScore() + scheme.getGapScore()).toDouble() * badPercent)

        val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(solution)

        mutableListOf<Interval>()
        val result2 = mutableListOf<Interval>()
        val candidates =
                aMatch.solve().mapIndexed { index, pair -> Interval(pair.first, index, pair.second) }
                        .filter { it.score >= realThreshold }
                        .filter { it.endExclusive > it.startInclusive }
                        .sortedByDescending { it.score }


        val intervalTree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.LONG)
                .collectIntervals { interval -> ListIntervalCollection() }
                .build()

        for (candidate in candidates) {
            val interval = IntegerInterval(candidate.startInclusive, candidate.endExclusive, false, true)
            if (intervalTree.overlapStream(interval).limit(1).collect(Collectors.toList()).isEmpty()) {
                intervalTree.insert(interval)
                result2.add(candidate)
            }
        }

        return result2
    }
}

class InteractiveDuplicateSearch<T>(val k: Double) : IApproximateMatching<T> {

    fun <T> editDistance(a: List<T>, b: List<T>): Double {
        val row = DoubleArray(b.size + 1) { 0.0 }

        val match = 1.0//scoringScheme.getMatchScore().toDouble()
        val mismatch = 0.0//scoringScheme.getMismatchScore().toDouble()
        val gap = 0.0//scoringScheme.getGapScore().toDouble()
        var left = 0.0
        var newLeft = 0.0
        for (i in 1 until a.size + 1) {
            left = max(if (a[i - 1] == b[0]) match else mismatch, max(gap + row[0], row[1] + gap))

            for (j in 2 until b.size) {
                newLeft = max(left + gap, max(row[j - 1] + if (a[i - 1] == b[j - 1]) match else mismatch, row[j] + gap))
                row[j - 1] = left
                left = newLeft
            }
            //j==b.size
            row[b.size] = max(
                    row[b.size - 1] + if (a[i - 1] == b[b.size - 1]) match else mismatch,
                    max(left + gap, row[b.size] + gap)
            )
            row[b.size - 1] = left
        }
        return row.last() * (0 - 2 * (-1)) + (a.size + b.size) * (-1)
    }

    //    note inverse operands for alignment score
    private fun compare(w1: Interval, w2: Interval) = when {
        w1.score > w2.score -> true
        w1.score < w2.score -> false
        else -> w1.endExclusive - w1.startInclusive > w2.endExclusive - w2.startInclusive
    }

    override fun find(p: List<T>, text: List<T>): MutableList<Interval> {
        val w = (p.size / k).toInt()

        val kdi = p.size * (1 / k + 1) * (1 - k * k)
//        phase one
        val setW1 = hashSetOf<Interval>()
        for (end in w..text.size) {
            val dist = editDistance(text.subList(end - w, end), p)
            if (dist >= -kdi) setW1.add(Interval(end - w, end, dist))
        }
        val setW2 = hashSetOf<Interval>()

//        phase 2
//3743

        for (clone in setW1) {
            var w_stroke = clone
            // iterated over all sizes
            for (l in (p.size * k).toInt()..w) {
                for (end in l + clone.startInclusive..clone.endExclusive) {
                    val w2 =
                            Interval(end - l, end, editDistance(p, text.subList(end - l, end)))
                    if (compare(w2, w_stroke)) {
                        w_stroke = w2
                    }
//                    here in place filtering of same interaals
//                    i think there error in luciv article: this should not be here
//                    setW2.add(w_stroke)
                }
            }
//          should be here
//            unique function is used here by using hashmap
            setW2.add(w_stroke)
        }


        val resullt = mutableListOf<Interval>()
//        phase3
        val intervalTree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.LONG)
                .collectIntervals { interval -> ListIntervalCollection() }
                .build()

        for (possibleClone in setW2) {
            val interval =
                    IntegerInterval(possibleClone.startInclusive, possibleClone.endExclusive, false, true)
            if (intervalTree.overlapStream(interval).limit(1).collect(Collectors.toList()).isEmpty()) {
                //not intersected
                intervalTree.insert(interval)
                resullt.add(possibleClone)
            }
        }

        println(resullt.size)
        return resullt
    }
}


class InteractiveDuplicateSearchViaSemiLocal<T>(val k: Double, val toExplicitKernel: Boolean = false) : IApproximateMatching<T> {

    val scoringScheme =
//            LCSScoringScheme()
            FixedScoringScheme(Fraction(0, 1), Fraction(-2, 1), Fraction(-1, 1))


    private fun getStringSubstringMatrix(monge: AbstractMongeMatrix, n: Int, m: Int): Array<DoubleArray> {
        val matrix = Array<DoubleArray>(n + 1) { DoubleArray(n + 1) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                matrix[i][j] = monge[i + m, j]
            }
        }
        return matrix
    }


    //    time /usr/lib/jvm/jdk-12.0.2/bin/java -jar target/GeneralSemiLocalSubsequenceProblem-1.0-SNAPSHOT-jar-with-dependencies.jar "1,2" "1/1,-1/1,-1/1" "1" "1" "Started shortly for assured hearing expense sdjfhsjd hjfsd sdhfj hsdjf sdhf hdsf jdsj fdhs jfhdsj fsdj hfsdj fhsdjhf dsjf dshf hjsdf hjdsfh jsdf dsjf hdjs hfjsdj fhsdj fhds fdsj fhjsd hfjsd fhhd fsdj fhjsd fhsdj fhsd hfdsj hfdsj fhdjs hfdsfj hdsj hfsdjh fsjdh fshdjf hdsjf sjd fhhjsd hfjshdf sj fhjsd fhdsjf hsdj hfsdjfhdsj hfdsjhfsdjhfsdfj hsdhjsdhj fsdh fjshd jfds hjsdf hjdsh fdshjfh sdjfhjsdhf dsjhdsfjh sdj hfsd jfhjdsf hsdjf hjdsfh sdjf hsdj fhsdjf hsdj fhsdfj hjsdjf hsd fh sda sadh shajd hasjd sajd jasd hjsa jdsja dhjsaj hdsajd sa hjdsa das hjsda hjdsa dhash dhsja dhsaj hash jjasds hjas jshjsa hjsa hj ashj ashj sahj  shj shj saahj sas h sadjh sadhj sdahj sadhj sa shj sdahjs ahj ash as jhs ahjds ah shsjh sdjh s hjs ajh hj saj  sadhj sadjhsa dhjs adhj asdhj asdhj asjh sajh  sad hj hj" "/home/nikita/IdeaProjects/GeneralSemiLocalSubsequenceProblem/src/main/java/application/exampleTextForPatternMatching.txt" "0.8" 5 "df.sog"
    override fun find(p: List<T>, text: List<T>): MutableList<Interval> {

        val w = (p.size / k).toInt()
        val kdi = p.size * (1 / k + 1) * (1 - k * k)

//        O(tp)
        val windowSubstringSA = ImplicitSemiLocalSA(p, text, scoringScheme,
                ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists }, scoringScheme))

        val setW2 = if (toExplicitKernel) {
//            n+1 n+1 size
            val w1Set = hashSetOf<Interval>()
            val matrix = getStringSubstringMatrix(windowSubstringSA.getMatrix(), text.size, p.size)
//            O(text.size-w) *O(w) ~ O(tp)
            for (end in w..text.size) {
                val offset = end - w
                if (windowSubstringSA.stringSubstring(offset, offset + w + 1) < -kdi) continue
                val smawk = rowMinima({ i, j -> -matrix[i + offset][j + offset] }, w, w)
                val possible = smawk.mapIndexed { col: Int, row: Int ->
                    Interval(row + offset, col + offset,
                            matrix[row + offset][col + offset])
                }.filter { it.score >= -kdi }
                if (possible.isEmpty()) continue
                val maximum = possible.maxBy { it.score }!!.score
                val curMax = Interval(0, 0, 0.0)
                for (clone in possible) {
                    if (clone.score == maximum && clone.endExclusive - clone.startInclusive >= curMax.endExclusive - curMax.startInclusive) {
                        curMax.startInclusive = clone.startInclusive
                        curMax.endExclusive = clone.endExclusive
                    }
                }
                w1Set.add(curMax)
            }
            w1Set
        } else {

//            2385
//            2023

//            val possible = rowMinima({ i, j -> -windowSubstringSA.stringSubstring(j + 0, i ) }, text.size + 1, text.size + 1)
//                    .mapIndexed{ col: Int, row: Int ->  Interval(row,col,
//                            windowSubstringSA.stringSubstring(row+0,col+0))}
//                    .filter { it.score>=-kdi }.forEach{println(it)}
            var ja = 0
            val w1Set = hashSetOf<Interval>()


            for (end in w..text.size) {

                val offset = end - w
                if (windowSubstringSA.stringSubstring(offset, offset + w) < -kdi) {
                    continue
                }

                val smawk = rowMinima({ i, j -> -windowSubstringSA.stringSubstring(j + offset, i + offset) }, w + 1, w + 1)
                        .mapIndexed { col: Int, row: Int -> Interval(row + offset, col + offset, windowSubstringSA.stringSubstring(row + offset, col + offset)) }
                val possible = smawk.filter { it.score >= -kdi }
                if (possible.isEmpty()) continue

                val maximum = possible.maxBy { it.score }!!.score
                val curMax = Interval(0, 0, 0.0)
                for (clone in possible) {
                    if (clone.score == maximum && clone.endExclusive - clone.startInclusive >= curMax.endExclusive - curMax.startInclusive) {
                        curMax.startInclusive = clone.startInclusive
                        curMax.endExclusive = clone.endExclusive
                    }
                }
                w1Set.add(curMax)
            }
            w1Set
        }

        val result = mutableListOf<Interval>()
        val intervalTree = IntervalTreeBuilder.newBuilder()
                .usePredefinedType(IntervalType.LONG)
                .collectIntervals { interval -> ListIntervalCollection() }
                .build()
//3634
        for (possibleClone in setW2) {
            val interval =
                    IntegerInterval(possibleClone.startInclusive, possibleClone.endExclusive, false, true)
            if (intervalTree.overlapStream(interval).limit(1).collect(Collectors.toList()).isEmpty()) {
                //not intersected
                intervalTree.insert(interval)
                result.add(possibleClone)
            }
        }


        println(result.size)
        return result
    }
}

/**
 * TODO do we have other options?
 * Our approavch when analyze dist matrix
 */
class ApproximateMatchingViaRangeQuery