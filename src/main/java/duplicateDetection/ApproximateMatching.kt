package duplicateDetection

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.ThresholdAMathViaSemiLocal
import sequenceAlignment.ISemiLocalProvider
import utils.IScoringScheme
import utils.Interval
import utils.TextInterval


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

    private fun Interval.notIntersectedWith(other: Interval): Boolean =
        // [this][other] or [other][this]
        other.startInclusive >= this.endExclusive || other.endExclusive <= this.startInclusive

    private val badPercent = (1 - thresholdPercent) * 3 / 4
    private val approximatePercent = (1 - thresholdPercent) * 1 / 4
    private val goodPercent = thresholdPercent

    override fun find(p: List<T>, text: List<T>): MutableList<Interval> {
        val solution = provider.buildSolution(p, text, scheme)

        //exactMatchOverall*percent
        val realThreshold = p.size * (scheme.getMatchScore().toDouble() * (goodPercent - approximatePercent)
                + (scheme.getMismatchScore() + scheme.getGapScore()).toDouble() * badPercent)

        val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(solution)

        val result = mutableListOf<Interval>()
        val candidates =
            aMatch.solve().mapIndexed { index, pair -> Interval(pair.first, index, pair.second) }
                .filter { it.score >= realThreshold }
                .sortedByDescending { it.score }

        println(candidates)

        for (candidate in candidates) {
            if (result.all { clone -> clone.notIntersectedWith(candidate) }) result.add(candidate)
        }
        return result
    }
}

/**
 * TODO do we have other options?
 * Our approavch when analyze dist matrix
 */
class ApproximateMatchingViaRangeQuery