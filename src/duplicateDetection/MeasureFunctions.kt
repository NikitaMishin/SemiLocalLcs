package duplicateDetection

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.ThresholdAMathViaSemiLocal
import beyondsemilocality.BoundedLengthSmithWatermanAlignment
import beyondsemilocality.IFragmentSubstringProvider
import sequenceAlignment.ISemiLocalProvider
import utils.IScoringScheme

interface IMeasureFunction<T> {
    fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>>
}



//local
class BoundedLengthSWMeasureFunction<T>(
    var provider: IFragmentSubstringProvider<T>,
    var scheme: IScoringScheme,
    var w: Int
) : IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val bslw = BoundedLengthSmithWatermanAlignment(provider).solve(a, b, scheme, w)
        return Pair(bslw.first.score, b.subList(bslw.second.startInclusive, bslw.second.endExclusive))
    }
}
//TODO BoundendLengthSWAccumulated


//TODO normalized blsw measure in article

//TODO semi-local also prefix-suffix and reverse

//string- substring
class StringSubstringMeasureFunction<T>(var provider: ISemiLocalProvider, var scheme: IScoringScheme) :
    IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(provider.buildSolution(a, b, scheme))
            .solve().mapIndexed { index: Int, pair: Pair<Int, Double> -> Pair(index, pair) }.maxBy { it.second.second }
        return Pair(aMatch!!.second.second, b.subList(aMatch.second.first, aMatch.first))
    }


}

//TODO what is this ?
class StringSubstringMeasureFunctionAccumulated<T>(
    var provider: ISemiLocalProvider,
    var scheme: IScoringScheme,
    var threshold: Double
) : IMeasureFunction<T> {
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