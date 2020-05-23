package duplicateDetection

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import beyondsemilocality.BoundedLengthSmithWatermanAlignment
import beyondsemilocality.IFragmentSubstringProvider
import beyondsemilocality.ImplicitFragmentSubstringProvider
import longestCommonSubsequence.ReducingKernelEvaluation
import sequenceAlignment.ISemiLocalProvider
import sequenceAlignment.ImplicitSemiLocalProvider
import utils.IScoringScheme
import utils.Interval
import utils.dummyPermutationMatrixTwoLists
import kotlin.math.min

/**
 *
 */
fun percentToScore(patternLength: Int, scheme: IScoringScheme, percent: Double): Double {
    val badPercent = (1 - percent) * 3 / 4
    val approximatePercent = (1 - percent) * 1 / 4
    return patternLength * (scheme.getMatchScore().toDouble() * (percent - approximatePercent)
            + (scheme.getMismatchScore() + scheme.getGapScore()).toDouble() * badPercent)

}


/**
 * Function that measure similarity between given fragments
 * @return Pair of score  with interval in b where this happen and percent of similarity
 */
interface IMeasureFunction<T> {
    fun computeSimilarity(a: List<T>, b: List<T>, thresholdPercent: Double): Pair<Interval, Boolean>
}


/**
 * Bounded-length Smith-Waterman Measure Function. reffer to local alignment between two strings
 */
class BoundedLengthSWMeasureFunction<T>(var scheme: IScoringScheme, var w: Int) : IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>, thresholdPercent: Double): Pair<Interval, Boolean> {
        val provider: IFragmentSubstringProvider<T> = ImplicitFragmentSubstringProvider(a, b, scheme)
        val bslw = BoundedLengthSmithWatermanAlignment(provider).solve(a, b, scheme, w)
        val cloneInALen = bslw.first.endExclusive - bslw.first.startInclusive
        val cloneInBLen = bslw.second.endExclusive - bslw.second.startInclusive
        val len = min(cloneInALen,cloneInBLen)
        val thresholdValue = percentToScore(len, scheme, thresholdPercent)
        return Pair(
            Interval(bslw.second.startInclusive, bslw.second.endExclusive, bslw.second.score),
            bslw.second.score >= thresholdValue
        )
    }

//    return Pair(
//    Interval(aMatch!!.second.first, aMatch.first, aMatch.second.second),
//    aMatch.second.second > thresholdValue
}



/**
 *
 */
class StringSubstringMeasureFunction<T>(var scheme: IScoringScheme) :
    IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>, thresholdPercent: Double): Pair<Interval, Boolean> {
        val patternSize = a.size
        val provider = ImplicitSemiLocalProvider(ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists},scheme))
        val thresholdValue = percentToScore(patternSize, scheme, thresholdPercent)
        val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(provider.buildSolution(a, b, scheme))
            .solve().mapIndexed { index: Int, pair: Pair<Int, Double> -> Pair(index, pair) }.maxBy { it.second.second }
        return Pair(
            Interval(aMatch!!.second.first, aMatch.first, aMatch.second.second),
            aMatch.second.second >= thresholdValue
        )
    }
}



///**
// *TODO add backtrace for the second one
// */
//class GlobalAlignmentMeasureFunction<T>(var scheme: IScoringScheme) : IMeasureFunction<T> {
//    override fun computeSimilarity(a: List<T>, b: List<T>): Interval {
//        val scoreMatrix = Array(a.size + 1) { Array(b.size + 1) { 0.0 } }
//        val match = scheme.getMatchScore().toDouble()
//        val mismatch = scheme.getMismatchScore().toDouble()
//        val gap = scheme.getGapScore().toDouble()
//
//        for (i in 1 until scoreMatrix.size) {
//            for (j in 1 until scoreMatrix[0].size) {
//                scoreMatrix[i][j] = java.lang.Double.max(
//                    scoreMatrix[i - 1][j - 1] + (if (a[i - 1] == b[j - 1]) match
//                    else mismatch),
//                    java.lang.Double.max(
//                        scoreMatrix[i - 1][j] + gap,
//                        scoreMatrix[i][j - 1] + gap
//                    )
//                )
//            }
//        }
//
//
//        return Pair(scoreMatrix[a.size][b.size], TODO())
//    }
//}


///**
// *
// */
//class BoundedLengthSWAccumulatedFunction<T>() : IMeasureFunction<T> {
//    override fun computeSimilarity(a: List<T>, b: List<T>, thresholdPercent: Double): Pair<Interval, Boolean> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}
//
//
///**
// *
// */
//class NormalizedBoundedLengthSWMeasureFunction<T>() : IMeasureFunction<T> {
//    override fun computeSimilarity(a: List<T>, b: List<T>, thresholdPercent: Double): Pair<Interval, Boolean> {
//        TODO()
//    }
//}
//
//
///**
// *
// */
//class NormalizedBoundedLengthSWAccumulatedMeasureFunction<T>() : IMeasureFunction<T> {
//    override fun computeSimilarity(a: List<T>, b: List<T>, thresholdPercent: Double): Pair<Interval, Boolean> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
///**
// *
// */
//class StringSubstringAccumulatedMeasureFunction<T>(var provider: ISemiLocalProvider, var scheme: IScoringScheme, var threshold: Double) : IMeasureFunction<T> {
//    override fun computeSimilarity(a: List<T>, b: List<T>): Interval {
//        val clones =
//            ThresholdAMathViaSemiLocal(CompleteAMatchViaSemiLocalTotallyMonotone(provider.buildSolution(a, b, scheme)))
//                .solve(threshold)
//        val len = clones.size
//        return Pair(clones.sumByDouble { it.score } / len,
//            clones.flatMap { b.subList(it.startInclusive, it.endExclusive) })
//    }
//}
