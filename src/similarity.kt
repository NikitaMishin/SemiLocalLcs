/**
 *
 */


/**
 *
 */
data class SimilarityResult(val intervals: Sequence<Pair<Interval, Interval>>, val similarityScore: Double)

/**
 *
 */
interface SimilarityStrategy {
    //text1: List<T>, text2: List<T>
    /**
     *
     */
    fun execute(): Sequence<SimilarityResult>
}



