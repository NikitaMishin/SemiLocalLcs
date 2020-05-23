package utils





internal class RangeTree1D<T : Comparable<T>>(points: List<Position1D<T>>) {
    private var rangeTreeNode = build(points)

    internal data class RangeTreeNode<T : Comparable<T>>(
        var left: T,
        var right: T,
        var sum: Int,
        internal var leftSubtree: RangeTreeNode<T>?,
        internal var rightSubtree: RangeTreeNode<T>?
    ) {
        fun countSumBetween(interval: IntervalQuery<T>): Int {
            val node: RangeTreeNode<T>? = this
            val l = interval.leftInclusive
            val r = interval.rightInclusive


            if (node == null) return 0

            return when {
                l <= node.left && node.right <= r -> node.sum
                node.right < l || r < node.left -> 0
                else -> {
                    val sum = if (node.leftSubtree != null) node.leftSubtree!!.countSumBetween(interval) else 0
                    if (node.rightSubtree != null) sum + node.rightSubtree!!.countSumBetween(interval) else sum
                }
            }


        }
    }


    private fun build(points: List<Position1D<T>>): RangeTreeNode<T>? {

        if (points.isEmpty()) return null

        if (points.size == 1 || points.last().first == points.first().first)
            return RangeTreeNode(
                points[0].first,
                points[0].first,
                points.fold(0, { acc, v -> acc + v.second }),
                null,
                null
            )

        val dist = mutableListOf(points[0].first)

        var prev = points[0].first
        for (i in 1 until points.size) {
            val cur = points[i].first
            if (cur != prev) dist.add(cur)
            prev = cur
        }

        val medianPoint = dist[dist.size / 2 - 1]

        val lTree = build(points.filter { it.first <= medianPoint })
        val rTree = build(points.filter { it.first > medianPoint })

        var sum = 0
        if (lTree != null) sum += lTree.sum
        if (rTree != null) sum += rTree.sum

        return RangeTreeNode(points.first().first, points.last().first, sum, lTree, rTree)
    }

    fun countSumBetween(interval: IntervalQuery<T>): Int {
        val node: RangeTreeNode<T>? = this.rangeTreeNode ?: return 0
        return node!!.countSumBetween(interval)
    }

}


/**
 * For for unique points
 */
class RangeTree2D<T : Comparable<T>>(points: List<Position2D<T>>) {
    private var rangeTree: RangeTreeNode<T>? = build(points.sortedBy { it.i })// O(nlogn)

    private data class RangeTreeNode<T : Comparable<T>>(
        var coordinate: T,
        var leftSubtree: RangeTreeNode<T>?,
        var rightSubtree: RangeTreeNode<T>?,
        var rangeTree1D: RangeTree1D<T>
    ) {
        internal fun isLeaf() = leftSubtree == null && rightSubtree == null
    }


    private fun build(points: List<Position2D<T>>): RangeTreeNode<T>? {
        val size = points.size
        if (points.isEmpty()) return null
        if (size == 1 || points.last().i == points.first().i)
            return RangeTreeNode(
                points[0].i, null, null,
                RangeTree1D(points.sortedBy { it.j }.map { Pair(it.j, it.value) })
            )

        val dist = mutableListOf(points[0].i)
        var prev = points[0].i
        for (i in 1 until points.size) {
            val cur = points[i].i
            if (cur != prev) dist.add(cur)
            prev = cur
        }

        val medianPoint = dist[dist.size / 2 - 1]
        val l = points.filter { it.i <= medianPoint }
        val r = points.filter { it.i > medianPoint }


        val lTree = build(l)
        val rTree = build(r)

        return RangeTreeNode(
            medianPoint, lTree, rTree,
            RangeTree1D(points.sortedBy { it.j }.map { Pair(it.j, it.value) })
        )
    }


    fun ortoghonalQuery(intervalX: IntervalQuery<T>, intervalY: IntervalQuery<T>): Int {
        var lcs: RangeTreeNode<T>? = this.rangeTree

        if (lcs == null) return 0


        if (lcs.isLeaf()) return if (lcs.coordinate.isInside(intervalX)) lcs.rangeTree1D.countSumBetween(intervalY) else 0

        // find common node aka lcs
        loop@ while (true) {
            when {
                lcs == null -> return 0
                intervalX.leftInclusive <= lcs.coordinate && intervalX.rightInclusive <= lcs.coordinate ->
                    lcs = lcs.leftSubtree
                intervalX.leftInclusive > lcs.coordinate && intervalX.rightInclusive > lcs.coordinate ->
                    lcs = lcs.rightSubtree
                else -> break@loop //
            }
        }

        var result = 0
        var left = lcs?.leftSubtree
        var right = lcs?.rightSubtree

        if (lcs?.isLeaf()!! && lcs.coordinate.isInside(intervalX)) return lcs.rangeTree1D.countSumBetween(intervalY)

        while (left != null) {
            when {
                left.isLeaf() -> {
                    result += if (left.coordinate.isInside(intervalX)) left.rangeTree1D.countSumBetween(intervalY) else 0
                    left = null
                }
                intervalX.leftInclusive <= left.coordinate -> {
                    val queryOnY = if (left.rightSubtree != null)
                        left.rightSubtree!!.rangeTree1D.countSumBetween(intervalY) else 0
                    result += queryOnY
                    left = left.leftSubtree
                }
                else -> {
                    left = left.rightSubtree
                }
            }
        }

        while (right != null) {
            when {
                right.isLeaf() -> {
                    result += if (right.coordinate.isInside(intervalX)) right.rangeTree1D.countSumBetween(intervalY) else 0
                    right = null
                }
                intervalX.rightInclusive > right.coordinate -> {
                    val queryOnY = if (right.leftSubtree != null)
                        right.leftSubtree!!.rangeTree1D.countSumBetween(intervalY) else 0

                    result += queryOnY
                    right = right.rightSubtree
                }
                else -> {
                    right = right.leftSubtree
                }
            }
        }
        return result
    }

}



//TODO
internal data class  MatrixPoint<T:Comparable<T>>(val x:Int, val y :Int, val value:T)

internal class RangeTreeD1Max<T : Comparable<T>>(points: List<MatrixPoint<T>>,val operation:(T,T)->T,val default: T) {
    private var rangeTreeNode = build(points)

    internal data class RangeTreeNode<T : Comparable<T>>(
        var left: Int,
        var right: Int,
        var value: T,
        internal var leftSubtree: RangeTreeNode<T>?,
        internal var rightSubtree: RangeTreeNode<T>?) {

        fun max(interval: IntervalQuery<Int>, default:T,operation:(T,T)->T): T {
            val node: RangeTreeNode<T>? = this
            val l = interval.leftInclusive
            val r = interval.rightInclusive


            if (node == null) return default

            return when {
                l <= node.left && node.right <= r -> node.value
                node.right < l || r < node.left -> default
                else -> {
                    when{
                        node.leftSubtree != null && node.rightSubtree != null ->
                            operation(
                                node.leftSubtree!!.max(interval,default, operation),
                                node.rightSubtree!!.max(interval,default, operation)
                            )
                        node.leftSubtree != null -> node.leftSubtree!!.max(interval,default,operation)
                        node.rightSubtree != null -> node.rightSubtree!!.max(interval,default,operation)
                        else -> default
                    }
                }
            }


        }
    }


    private fun build(points: List<MatrixPoint<T>>): RangeTreeNode<T>? {

        if (points.isEmpty()) return null

        if (points.size == 1 || points.last().x == points.first().x)
            return RangeTreeNode(
                points[0].x,
                points[0].x,
                points.fold(points[0].value, { acc, v -> operation(acc,v.value)}),
                null,
                null
            )

        val dist = mutableListOf(points[0].x)

        var prev = points[0].x
        for (i in 1 until points.size) {
            val cur = points[i].x
            if (cur != prev) dist.add(cur)
            prev = cur
        }

        val medianPoint = dist[dist.size / 2 - 1]

        val lTree = build(points.filter { it.x <= medianPoint })
        val rTree = build(points.filter { it.x > medianPoint })

//        either one of exist
        return RangeTreeNode(points.first().x,points.last().x,
            when{
                lTree != null && rTree != null -> operation(lTree.value,rTree.value)
                lTree != null -> lTree.value
                rTree != null -> rTree.value
                else ->  {
                    throw NotImplementedError("Impossible case")
                }
            },lTree,rTree)

    }

    fun max(interval: IntervalQuery<Int>): T {
        val node: RangeTreeNode<T>? = this.rangeTreeNode ?: return default
        return node!!.max(interval, default, operation)
    }

}



/**
 * For for unique points
 */
class RangeTree2DMax<T : Comparable<T>>(points: List<Position2D<T>>) {
    private var rangeTree: RangeTreeNode<T>? = build(points.sortedBy { it.i })// O(nlogn)

    private data class RangeTreeNode<T : Comparable<T>>(
        var coordinate: T,
        var leftSubtree: RangeTreeNode<T>?,
        var rightSubtree: RangeTreeNode<T>?,
        var rangeTree1D: RangeTree1D<T>
    ) {
        internal fun isLeaf() = leftSubtree == null && rightSubtree == null
    }


    private fun build(points: List<Position2D<T>>): RangeTreeNode<T>? {
        val size = points.size
        if (points.isEmpty()) return null
        if (size == 1 || points.last().i == points.first().i)
            return RangeTreeNode(
                points[0].i, null, null,
                RangeTree1D(points.sortedBy { it.j }.map { Pair(it.j, it.value) })
            )

        val dist = mutableListOf(points[0].i)
        var prev = points[0].i
        for (i in 1 until points.size) {
            val cur = points[i].i
            if (cur != prev) dist.add(cur)
            prev = cur
        }

        val medianPoint = dist[dist.size / 2 - 1]
        val l = points.filter { it.i <= medianPoint }
        val r = points.filter { it.i > medianPoint }


        val lTree = build(l)
        val rTree = build(r)

        return RangeTreeNode(
            medianPoint, lTree, rTree,
            RangeTree1D(points.sortedBy { it.j }.map { Pair(it.j, it.value) })
        )
    }


    fun ortoghonalQuery(intervalX: IntervalQuery<T>, intervalY: IntervalQuery<T>): Int {
        var lcs: RangeTreeNode<T>? = this.rangeTree

        if (lcs == null) return 0


        if (lcs.isLeaf()) return if (lcs.coordinate.isInside(intervalX)) lcs.rangeTree1D.countSumBetween(intervalY) else 0

        // find common node aka lcs
        loop@ while (true) {
            when {
                lcs == null -> return 0
                intervalX.leftInclusive <= lcs.coordinate && intervalX.rightInclusive <= lcs.coordinate ->
                    lcs = lcs.leftSubtree
                intervalX.leftInclusive > lcs.coordinate && intervalX.rightInclusive > lcs.coordinate ->
                    lcs = lcs.rightSubtree
                else -> break@loop //
            }
        }

        var result = 0
        var left = lcs?.leftSubtree
        var right = lcs?.rightSubtree

        if (lcs?.isLeaf()!! && lcs.coordinate.isInside(intervalX)) return lcs.rangeTree1D.countSumBetween(intervalY)

        while (left != null) {
            when {
                left.isLeaf() -> {
                    result += if (left.coordinate.isInside(intervalX)) left.rangeTree1D.countSumBetween(intervalY) else 0
                    left = null
                }
                intervalX.leftInclusive <= left.coordinate -> {
                    val queryOnY = if (left.rightSubtree != null)
                        left.rightSubtree!!.rangeTree1D.countSumBetween(intervalY) else 0
                    result += queryOnY
                    left = left.leftSubtree
                }
                else -> {
                    left = left.rightSubtree
                }
            }
        }

        while (right != null) {
            when {
                right.isLeaf() -> {
                    result += if (right.coordinate.isInside(intervalX)) right.rangeTree1D.countSumBetween(intervalY) else 0
                    right = null
                }
                intervalX.rightInclusive > right.coordinate -> {
                    val queryOnY = if (right.leftSubtree != null)
                        right.leftSubtree!!.rangeTree1D.countSumBetween(intervalY) else 0

                    result += queryOnY
                    right = right.rightSubtree
                }
                else -> {
                    right = right.leftSubtree
                }
            }
        }
        return result
    }

}
