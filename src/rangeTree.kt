import kotlin.math.ceil


//TODO implicit MONGE

// TODO row minima

//TODO exlicit monge


data class SortedArray<T : Comparable<T>>(val sortedPointsByY: List<T>) {
    fun countElementsBetween(interval: IntervalQuery<T>): Int {
        val lowerBoundPos = lowerBoundPosition(0, sortedPointsByY.size, interval.leftInclusive) // O(logn)
        if (lowerBoundPos == sortedPointsByY.size) return 0
        val upperBoundPos = upperBoundPosition(-1, sortedPointsByY.size - 1, interval.rightInclusive) //  O(logn)
        if (upperBoundPos == -1) return 0
        if (lowerBoundPos > upperBoundPos) return 0
        return upperBoundPos - lowerBoundPos + 1

    }

    /**
     * Returns an index pointing to the first element in the range
     * [first,last) which does not compare less than y
     * aka first element that >= y
     * @return If all the element in the range compare less than val, the function returns last
     */
    fun lowerBoundPosition(start: Int, last: Int, y: T): Int {
        var count = last - start
        var step = 0
        var position = 0
        var first = start

        while (count > 0) {
            step = count / 2
            position = step + first
            if (sortedPointsByY[position] < y) {
                first = position + 1
                count -= step + 1
            } else {
                count = step
            }
        }
        return first
    }

    /**
     * Returns an index pointing to the first element in the range
     * (first,last] which does not compare greater than y
     * aka first element that <= y
     * @return If all the element in the range compare great than val, the function returns first
     */
    fun upperBoundPosition(start: Int, end: Int, y: T): Int {
        var count = end - start
        var step = 0
        var position = 0
        var last = end

        while (count > 0) {
            step = count / 2
            position = last - step

            if (sortedPointsByY[position] > y) {
                // go left
                last = position - 1
                count -= step + 1
            } else {
                // go right
                count = step
            }
        }
        return last
    }
}


data class Position2D<T : Comparable<T>>(var i: T, var j: T) {
    inline fun isInside(intervalX: IntervalQuery<T>, intervalY: IntervalQuery<T>): Boolean =
        (i >= intervalX.leftInclusive && i <= intervalX.rightInclusive)
                && (j >= intervalY.leftInclusive && j <= intervalY.rightInclusive)
}

data class IntervalQuery<T : Comparable<T>>(val leftInclusive: T, val rightInclusive: T)

/**
 * For for unique points
 * TODO do we need ununique points,
 */
class RangeTree2D<T : Comparable<T>>(points: List<Position2D<T>>) {
    private var rangeTree: RangeTreeNode<T>? = null

    private data class RangeTreeNode<T : Comparable<T>>(
        var value: Position2D<T>, var size: Int,
        internal var leftSubtree: RangeTreeNode<T>?,
        internal var rightSubtree: RangeTreeNode<T>?,
        internal var rangeTree1D: SortedArray<T>
    ) {
        internal fun isLeaf() = leftSubtree == null && rightSubtree == null
    }

    init {
        val xPointsSorted = points.sortedBy { it.i } // O(nlogn)
        val yPointsSorted = points.sortedBy { it.j } // O(nlogn)
        rangeTree = build2DTree(xPointsSorted, yPointsSorted)
    }

    private fun build2DTree(xPoints: List<Position2D<T>>, yPoints: List<Position2D<T>>): RangeTreeNode<T>? {
        val size = xPoints.size
        if (xPoints.isEmpty()) {
            return null
        }
        if (size == 1)
            return RangeTreeNode(xPoints[0], 1, null, null, SortedArray(listOf(yPoints[0].j)))

        val median = ceil(xPoints.size.toDouble() / 2).toInt() - 1

        val medianPoint = xPoints[median]
        val leftSubtree =
            build2DTree(xPoints.slice(IntRange(0, median)), yPoints.filter { it.i <= medianPoint.i })
        val rightSubtree =
            build2DTree(xPoints.slice(IntRange(median + 1, size - 1)), yPoints.filter { it.i > medianPoint.i })
        return RangeTreeNode(medianPoint, size, leftSubtree, rightSubtree, SortedArray(yPoints.map { it.j }))
    }

    fun ortoghonalQuery(intervalX: IntervalQuery<T>, intervalY: IntervalQuery<T>): Int {
        var lcs: RangeTreeNode<T>? = this.rangeTree
        if (lcs == null) return 0
        if (lcs.isLeaf()) return if (lcs.value.isInside(intervalX, intervalY)) 1 else 0

        // find common node aka lcs
        loop@ while (true) {
            when {
                lcs == null -> return 0
                intervalX.leftInclusive <= lcs.value.i && intervalX.rightInclusive <= lcs.value.i ->
                    lcs = lcs.leftSubtree
                intervalX.leftInclusive > lcs.value.i && intervalX.rightInclusive > lcs.value.i ->
                    lcs = lcs.rightSubtree
                else -> break@loop //
            }
        }

        var result = 0
        var left = lcs?.leftSubtree
        var right = lcs?.rightSubtree

        if (lcs?.isLeaf()!! && lcs.value.isInside(intervalX, intervalY)) return lcs.rangeTree1D.countElementsBetween(
            intervalY
        )

        while (left != null) {
            if (left.isLeaf()) {
                result += if (left.value.isInside(intervalX, intervalY)) 1 else 0
                left = null
            } else if (intervalX.leftInclusive <= left.value.i) {
                val queryOnY = if (left.rightSubtree != null)
                    left.rightSubtree!!.rangeTree1D.countElementsBetween(intervalY) else 0
                result += queryOnY
                left = left.leftSubtree
            } else {
                left = left.rightSubtree
            }
        }

        while (right != null) {
            if (right.isLeaf()) {
                result += if (right.value.isInside(intervalX, intervalY)) 1 else 0
                right = null
            } else if (intervalX.rightInclusive > right.value.i) {
                val queryOnY = if (right.leftSubtree != null)
                    right.leftSubtree!!.rangeTree1D.countElementsBetween(intervalY) else 0

                result += queryOnY
                right = right.rightSubtree
            } else {
                right = right.leftSubtree
            }
        }
        return result
    }

}
