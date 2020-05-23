package utils


data class SortedArray<T : Comparable<T>>(val sortedPointsByY: List<T>) {
    fun countSumBetween(interval: IntervalQuery<T>): Int {
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

