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
    fun find(p: Fragment<T>, fragments: List<Fragment<T>>): MutableList<Pair<Fragment<T>, MutableList<TextInterval<T>>>>
}


/**
 * Approximate matching via thresholdAMatch
 */
class ApproximateMatchingViaThresholdAMatch<T>(
    var provider: ISemiLocalProvider,
    var scheme: IScoringScheme
) : IApproximateMatching<T> {

    private var thresholdPercent: Double
    init {
        //TODO()
        //TODO(add scheme translation to therssold)
        thresholdPercent = 5.0

    }

    override fun find(
        p: Fragment<T>,
        fragments: List<Fragment<T>>): MutableList<Pair<Fragment<T>, MutableList<TextInterval<T>>>> {

        return fragments.map { fragment ->
            val solution = provider.buildSolution(
                p.text.subList(p.startInclusive, p.endExclusive),
                fragment.text.subList(fragment.startInclusive, fragment.endExclusive),
                scheme
            )

            val realThreshold = 4.0//TODO(use percent of somethign)

            val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(solution)
            val clones = ThresholdAMathViaSemiLocal(aMatch).solve(realThreshold)

            Pair(
                fragment,
                clones.map {
                    TextInterval(
                        it.startInclusive + fragment.startInclusive,
                        it.endExclusive + fragment.startInclusive,
                        fragment.text,
                        it.score
                    )
                }.toMutableList()
            )

        }.toMutableList()
    }
}

/**
 * TODO do we have other options?
 * Our approavch when analyze dist matrix
 */
class ApproximateMatchingViaRangeQuery