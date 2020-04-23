package duplicateDetection

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.ThresholdAMathViaSemiLocal
import beyondsemilocality.BoundedLengthSmithWatermanAlignment
import beyondsemilocality.IFragmentSubstringProvider
import sequenceAlignment.ISemiLocalProvider
import utils.IScoringScheme


/**
 * Function that measure similarity between given fragments
 * @return Pair of score  with interval in b where this happen
 */
interface IMeasureFunction<T> {
    fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>>
}


/**
 * Bounded-length Smith-Waterman Measure Function. reffer to local alignment between two strings
 */
class BoundedLengthSWMeasureFunction<T>(
    var provider: IFragmentSubstringProvider<T>, var scheme: IScoringScheme, var w: Int) : IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val bslw = BoundedLengthSmithWatermanAlignment(provider).solve(a, b, scheme, w)
        return Pair(bslw.first.score, b.subList(bslw.second.startInclusive, bslw.second.endExclusive))
    }
}

/**
 *
 */
class BoundedLengthSWAccumulatedFunction<T>():IMeasureFunction<T>{
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        // find all non intersected
        TODO()
    }
}


/**
 *
 */
class NormalizedBoundedLengthSWMeasureFunction<T>():IMeasureFunction<T>{
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        TODO("Should be implemented as sum of non intersected pair of clones")
    }
}


/**
 *
 */
class NormalizedBoundedLengthSWAccumulatedMeasureFunction<T>():IMeasureFunction<T>{
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        TODO("Should be implemented from article")
    }
}


/**
 *
 */
class StringSubstringMeasureFunction<T>(var provider: ISemiLocalProvider, var scheme: IScoringScheme) :
    IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(provider.buildSolution(a, b, scheme))
            .solve().mapIndexed { index: Int, pair: Pair<Int, Double> -> Pair(index, pair) }.maxBy { it.second.second }
        return Pair(aMatch!!.second.second, b.subList(aMatch.second.first, aMatch.first))
    }
}


/**
 *
 */
class StringSubstringAccumulatedMeasureFunction<T>(var provider: ISemiLocalProvider, var scheme: IScoringScheme, var threshold: Double) : IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val clones =
            ThresholdAMathViaSemiLocal(CompleteAMatchViaSemiLocalTotallyMonotone(provider.buildSolution(a, b, scheme)))
                .solve(threshold)
        val len = clones.size
        return Pair(clones.sumByDouble { it.score } / len,
            clones.flatMap { b.subList(it.startInclusive, it.endExclusive) })
    }
}

/**
 *TODO add backtrace
 */
class GlobalAlignmentMeasureFunction<T>(var scheme: IScoringScheme) : IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val scoreMatrix = Array(a.size + 1) { Array(b.size + 1) { 0.0 } }
        val match = scheme.getMatchScore().toDouble()
        val mismatch = scheme.getMismatchScore().toDouble()
        val gap = scheme.getGapScore().toDouble()

        for (i in 1 until scoreMatrix.size) {
            for (j in 1 until scoreMatrix[0].size) {
                scoreMatrix[i][j] = java.lang.Double.max(
                    scoreMatrix[i - 1][j - 1] + (if (a[i - 1] == b[j - 1]) match
                    else mismatch),
                    java.lang.Double.max(
                        scoreMatrix[i - 1][j] + gap,
                        scoreMatrix[i][j - 1] + gap
                    )
                )
            }
        }
        //TODO add backtrace

        return Pair(scoreMatrix[a.size][b.size], TODO())
    }
}