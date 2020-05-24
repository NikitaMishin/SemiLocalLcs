package duplicateDetection

import SpeedRunner.lightPrefixAlignment
import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.ThresholdAMathViaSemiLocal
import beyondsemilocality.ImplicitFragmentSubstringProvider
import beyondsemilocality.WindowSubstringSA
import beyondsemilocality.WindowSubstringSANaiveImplicit
import sequenceAlignment.ISemiLocalProvider
import com.brein.time.timeintervals.collections.ListIntervalCollection
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder.IntervalType
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder
import com.brein.time.timeintervals.intervals.IntegerInterval
import longestCommonSubsequence.ReducingKernelEvaluation
import utils.*
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.system.measureTimeMillis


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

    fun <T> lightPrefixAlignment(a: List<T>, b: List<T>, scoringScheme: IScoringScheme): Double {
        val row = DoubleArray(b.size + 1) { 0.0 }

        val match = scoringScheme.getMatchScore().toDouble()
        val mismatch = scoringScheme.getMismatchScore().toDouble()
        val gap = scoringScheme.getGapScore().toDouble()
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
        return row.last()
    }

    //    note inverse operands for alignment score
    private fun compare(w1: Interval, w2: Interval) = when {
        w1.score > w2.score -> true
        w1.score < w2.score -> false
        else -> w1.endExclusive - w1.startInclusive > w2.endExclusive - w2.startInclusive
    }

    override fun find(p: List<T>, text: List<T>): MutableList<Interval> {
        val scoringScheme =
                FixedScoringScheme(Fraction(0, 1), Fraction(-1, 1), Fraction(-1, 1))
        val w = (p.size / k).toInt()

        val kdi = p.size * (1 / k + 1) * (1 - k * k)

//        phase one
        val setW1 = hashSetOf<Interval>()
        for (end in w..text.size) {
            val dist = lightPrefixAlignment(p, text.subList(end - w, end), scoringScheme)
            if (dist >= -kdi) setW1.add(Interval(end - w, end, dist))
        }
        val setW2 = hashSetOf<Interval>()

//        phase 2

        for (clone in setW1) {
            var w_stroke = clone
            // iterated over all sizes
            for (l in (p.size * k).toInt()..w) {
                for (end in l + clone.startInclusive..clone.endExclusive) {
                    val w2 =
                            Interval(end - l, end, lightPrefixAlignment(p, text.subList(end - l, end), scoringScheme))
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
        return resullt
    }
}


class InteractiveDuplicateSearchViaSemiLocal<T>(val k: Double, val withSmartWindowSubstringCalc: Boolean = false) : IApproximateMatching<T> {


    override fun find(p: List<T>, text: List<T>): MutableList<Interval> {
        val scoringScheme =
                FixedScoringScheme(
                        Fraction(0, 1),
                        Fraction(-1, 1),
                        Fraction(-1, 1))
        val w = (p.size / k).toInt()
        val kdi = p.size * (1 / k + 1) * (1 - k * k)

        val setW2 = hashSetOf<Interval>()

        val windowSubs = if (withSmartWindowSubstringCalc)
            WindowSubstringSA(ImplicitFragmentSubstringProvider(p, text, scoringScheme)) else
            WindowSubstringSANaiveImplicit<T>(ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists }, scoringScheme))

        val windowSubstring = windowSubs.solve(p, text, w, scoringScheme)

        for (window in windowSubstring.getSolutions()) {
            //findmax its O(nlogn) for implicit keys and O(n) for explicit
            val maximums = CompleteAMatchViaSemiLocalTotallyMonotone(window).solve()
            val maxScore = maximums.maxBy { it.second }!!.second
            if (maxScore >= -kdi) continue

            val curMax = Interval(0, 0, 0.0)

//            O(n)
            for ((column, pair) in maximums.withIndex()) {
                if (pair.second == maxScore && column - pair.first >= curMax.endExclusive - curMax.startInclusive) {
                    curMax.startInclusive = pair.first
                    curMax.endExclusive = column
                }
            }
            setW2.add(curMax)
        }
        val result = mutableListOf<Interval>()
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
                result.add(possibleClone)
            }
        }

        return result
    }
}

/**
 * TODO do we have other options?
 * Our approavch when analyze dist matrix
 */
class ApproximateMatchingViaRangeQuery