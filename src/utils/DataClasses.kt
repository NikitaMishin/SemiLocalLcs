package utils


data class Interval(val startInclusive: Int, val endExclusive: Int, val score: Double)

/**
 *
 */
data class SimilarityResult(val intervals: Sequence<Pair<Interval, Interval>>, val similarityScore: Double)



data class IntervalQuery<T : Comparable<T>>(val leftInclusive: T, val rightInclusive: T)

data class Position2D<T : Comparable<T>>(var i: T, var j: T, val value: Int = 1) {
    fun isInside(intervalX: IntervalQuery<T>, intervalY: IntervalQuery<T>): Boolean =
        (i >= intervalX.leftInclusive && i <= intervalX.rightInclusive)
                && (j >= intervalY.leftInclusive && j <= intervalY.rightInclusive)
}
typealias Position1D<T> = Pair<T, Int>
