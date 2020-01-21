

/**
 *Symbol type refer to Symbol
 */
enum class SymbolType {
    AlphabetSymbol,
    WildCardSymbol,// '?' - symbol not presented in alphabet
    //...
}

/**
 * Symbol is extended alphabet for semiLocalLCS
 */
data class Symbol<T>(val symbol: T, val type: SymbolType) where T : Comparable<T>


/**
 * Interface for semiLocal LCS problem for the two given lists of comparable elements.
 * The definition of semiLocal LCS problem see book "The algebra of string comparison: Computing with sticky braids",
 * page 51
 */
interface ISemiLocalLCS {
    /**
     *For a given A and B asks for lcs score for A and B[i:j]
     */
    fun stringSubstringLCS(i: Int, j: Int): Int

    /**
     *For a given A and B asks for lcs score for A[k:A.size] and B[0:j]
     */
    fun prefixSuffixLCS(k: Int, j: Int): Int

    /**
     *For a given A and B asks for lcs score for A[0:l] and B[i:B.size]
     */
    fun suffixPrefixLCS(l: Int, i: Int): Int

    /**
     *For a given A and B asks for lcs score for A[k:l] and B
     */
    fun substringStringLCS(k: Int, l: Int): Int
}


/////**
//// *  Semi Local problems
//// */
////
////
/////**
//// *
//// */
////abstract class SemiLocalLCS<E> {
////    abstract var fragmentA: List<E>
////    abstract var fragmentB: List<E>
////    // TODO ideompotence operation
////    abstract fun solve()
////
////    // TODO waht args
////    abstract fun query(i: Int, j: Int)
////}
////
/////**
//// *
//// */
////class SemiLocalLCSByMonge<E, T : MatrixElem, M : MongeMatrix<T>>(
////    private var solver: SemiLocalLCSSolveStrategy<T, M>,
////    override var fragmentA: List<E>,
////    override var fragmentB: List<E>) : SemiLocalLCS<E>() {
////    private lateinit var matrix: M
////    override fun solve() {
////        //should be ideompotence
////        matrix = solver.solve(fragmentA, fragmentB)
////    }
////
////    fun getMatrix(): M = matrix
////
////    fun setSolver(newSolver: SemiLocalLCSSolveStrategy<T, M>) {
////        solver = newSolver
////    }
////
////    override fun query(i: Int, j: Int) {
////        //matrix.get()
////        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
////    }
////
////}
////
////
/////**TODO K constraint comparable and hash
//// * or move him to class declaration
//// *
//// */
////abstract class SemiLocalLCSSolveStrategy<T : MatrixElem, M : MongeMatrix<T>>() {
////    abstract fun <K> solve(a: List<K>, b: List<K>): M
////}
////
/////**
//// *
//// */
////class SemiLocalLCSSolveStrategyRecursive<T : MatrixElem, M : MongeMatrix<T>>() : SemiLocalLCSSolveStrategy<T, M>() {
////    override fun <K> solve(a: List<K>, b: List<K>): M {
////        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
////    }
////}
////
/////**
//// *
//// */
////class SemiLocalLCSSolveStrategyIterative<T : MatrixElem, M : MongeMatrix<T>>() : SemiLocalLCSSolveStrategy<T, M>() {
////    override fun <K> solve(a: List<K>, b: List<K>): M {
////        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
////    }
////}
//
//
///**
// * Also sub permutation
// * // 0 in lists mean that no in row element
// * Format
// * X - rows in form  X -> Y , if X -> NOPOINT => no points at all in row x
// * Y - cols in form Y -> X , if Y ->  NOPOINT => no point at all in col y
// */
//data class PermutationMatrix(var X: MutableList<Int>, var Y: MutableList<Int>) {
//
//    val NO_POINT = -1
//
//    fun height() = X.size
//    fun width() = Y.size
//
//    /**
//     * Make all elements in column zero (at most 1) due to sub permutation property
//     * @param col indexed starting with zero!
//     */
//    inline fun resetInCol(col: Int) {
//        val x = Y[col]
//        Y[col] = NO_POINT
//        if (x != NO_POINT) X[x] = NO_POINT
//    }
//
//    /**
//     * Make all elements in column zero (at most 1) due to sub permutation property
//     * @param row indexed starting with zero
//     */
//    inline fun resetInRow(row: Int) {
//        val y = X[row]
//        X[row] = NO_POINT
//        if (y != NO_POINT) Y[y] = NO_POINT
//    }
//
//    /**
//     * @param row indexed starting with zero
//     * @param col indexed starting with zero
//     * @param value Boolean
//     */
//    inline operator fun set(row: Int, col: Int, value: Boolean) {
//        if (value) {
//            X[row] = col
//            Y[col] = row
//        } else {
//            X[row] = NO_POINT
//            Y[col] = NO_POINT
//        }
//    }
//
//    /**
//     * Split by colExclusive current matrix to two matrix i.e A[n, m] ->  A[n, colExclusive], A[n, m - colExclusive]
//     * @return  Pair of matricres with hasnmap for mapping back x coordinates
//     */
//    fun splitOnByColumn(colExclusive: Int): Pair<Pair<PermutationMatrix, MutableMap<Int, Int>>, Pair<PermutationMatrix, MutableMap<Int, Int>>> {
//        if (colExclusive >= width() || colExclusive<=0)
//            throw java.lang.IllegalArgumentException("colExclusive is $colExclusive whereas width is ${width()}")
//
//        fun getFirst(): Pair<PermutationMatrix, MutableMap<Int, Int>> {
//            val newToOldX = mutableMapOf<Int, Int>()
//            val oldToNew = mutableMapOf<Int, Int>()
//            val newXPoints = mutableListOf<Int>()
//
//            for (x in 0 until X.size) {
//                val y = X[x]
//
//                // mapping with removing of zero rows
//                //TODO checking of NO_Point ?
//                if (y  < colExclusive && y != NO_POINT) {
//                    newToOldX[newXPoints.size] = x
//                    oldToNew[x] = newXPoints.size
//                    newXPoints.add(y)
//                }
//            }
//
//            val nextYPoints = mutableListOf<Int>()
//            for (y in 0 until Y.size) {
//                val oldX = Y[y]
//                // TODO checking of NO_Point ?
//                if (y < colExclusive) {
//                    nextYPoints.add(oldToNew.getOrDefault(oldX, NO_POINT) )
//                } else break
//            }
//
//            return Pair(PermutationMatrix(newXPoints, nextYPoints), newToOldX)
//        }
//
//        fun getSecond(): Pair<PermutationMatrix, MutableMap<Int, Int>> {
//            val newToOldX = mutableMapOf<Int, Int>()
//            val oldToNew = mutableMapOf<Int, Int>()
//            val nextXPoints = mutableListOf<Int>()
//
//            for (x in 0 until X.size) {
//                val y = X[x]
//                if (y  >= colExclusive) {
//                    // y== 0 also removed
//                    newToOldX[nextXPoints.size] = x
//                    oldToNew[x] = nextXPoints.size
//                    val shifted = if(y == NO_POINT) NO_POINT else  y - colExclusive
//                    nextXPoints.add(shifted)
//                }
//            }
//
//            val nextYPoints = mutableListOf<Int>()
//            for (y in 0 until Y.size) {
//                val oldX = Y[y]
//                if (y >= colExclusive)
//                    nextYPoints.add(oldToNew.getOrDefault(oldX, NO_POINT) )
//            }
//
//            return Pair(PermutationMatrix(nextXPoints, nextYPoints), newToOldX)
//
//
//        }
//
//        return Pair(getFirst(), getSecond())
//    }
//
//    fun splitOnByRow(rowExclusive: Int): Pair<Pair<PermutationMatrix, MutableMap<Int, Int>>, Pair<PermutationMatrix, MutableMap<Int, Int>>> {
//        if (rowExclusive >= height() || rowExclusive<=0)
//            throw java.lang.IllegalArgumentException("rowExclusive is $rowExclusive whereas height is ${height()}")
//
//        fun getFirst(): Pair<PermutationMatrix, MutableMap<Int, Int>> {
//            val newToOldY = mutableMapOf<Int, Int>()
//            val oldToNewY = mutableMapOf<Int, Int>()
//            val nextYPoints = mutableListOf<Int>()
//
//            for (y in 0 until Y.size) {
//                val x = Y[y]
//                //TODO SAME QS
//                if (x  < rowExclusive && x != NO_POINT) {
//                    newToOldY[nextYPoints.size] = y
//                    oldToNewY[y] = nextYPoints.size
//                    nextYPoints.add(x)
//                }
//            }
//
//            val nextXPoints = mutableListOf<Int>()
//            for (x in 0 until X.size) {
//                val oldY = X[x]
//                if (x < rowExclusive) {
//                    nextXPoints.add(oldToNewY.getOrDefault(oldY, NO_POINT))
//                }
//            }
//
//            return Pair(PermutationMatrix(nextXPoints, nextYPoints), newToOldY)
//
//        }
//
//        fun getSecond(): Pair<PermutationMatrix, MutableMap<Int, Int>> {
//            val newToOldY = mutableMapOf<Int, Int>()
//            val oldToNewY = mutableMapOf<Int, Int>()
//            val nextYPoints = mutableListOf<Int>()
//
//            for (y in 0 until Y.size) {
//                val x = Y[y]
//                if (x >= rowExclusive) {
//                    newToOldY[nextYPoints.size] = y
//                    oldToNewY[y] = nextYPoints.size
//                    val offset = if (x!=NO_POINT) x - rowExclusive else NO_POINT
//                    nextYPoints.add(offset)
//                }
//            }
//
//            val nextXPoint = mutableListOf<Int>()
//            for (x in 0 until X.size) {
//                val oldY = X[x]
//                if (x >= rowExclusive) nextXPoint.add(oldToNewY.getOrDefault(oldY, NO_POINT))
//            }
//
//            return Pair(PermutationMatrix(nextXPoint, nextYPoints), newToOldY)
//
//
//        }
//        return Pair(getFirst(), getSecond())
//
//    }
//
//    fun restoreMatrixByMapping(
//        newToOldX: MutableMap<Int, Int>,
//        newToOldY: MutableMap<Int, Int>,
//        width: Int,
//        height: Int
//    ):
//            PermutationMatrix {
//        val newX = MutableList(height) { -1 }
//        val newY = MutableList(width) { -1 }
//        this.X.forEachIndexed { xNew, y ->
//            val yOld = y - 1
//            newX[newToOldX[xNew]!!] = newToOldY.getOrDefault(yOld, -1) + 1
//        }
//        this.Y.forEachIndexed { yNew, x ->
//            val xOLd = x - 1
//            newY[newToOldY[yNew]!!] = newToOldX.getOrDefault(xOLd, -1) + 1
//        }
//
//        return PermutationMatrix(newX, newY)
//
//    }
//
//}
//
//// a1  * a2
//// a1 of n1Xm
//// a2 mXn2
//// c result = n1Xn2
//fun steadyAnt(P: PermutationMatrix, Q: PermutationMatrix): PermutationMatrix {
//    if (P.width() == 1) return Q
//    val widthP1 = P.width() / 2
//
//    val (firstP, secondP) = P.splitOnByColumn(widthP1)
//    val (firstQ, secondQ) = Q.splitOnByRow(widthP1)
//
//    val R1 = steadyAnt(firstP.first, firstQ.first).restoreMatrixByMapping(
//        firstP.second,
//        firstQ.second,
//        P.width(),
//        P.height()
//    )
//
//    val R2 = steadyAnt(secondP.first, secondQ.first).restoreMatrixByMapping(
//        secondP.second,
//        secondQ.second,
//        P.width(),
//        P.height()
//    )
//
//    // we draw border to n+1 x  m + 1 grid , not kernel
//    val leftBorder = Point2D(P.height(), 0)
//    val rightBorder = Point2D(P.height(), 0)
//    //draw left
//    var currentHi = 0
//    var currentLo = 0
//
//
//    val leftBorderPath = hashMapOf<Point2D<Int>, Boolean>()
//    while (leftBorder != Point2D(0, P.width())) {
//        // choouse step
//        if (leftBorder.x == 0) break
//        val topCellHi = 1
//        val rightCellHi = 1
//        val topRightCellHi = 1
//        val topCellLo = 1
//        val rightCellLo = 1
//        val topRightCellLo = 1
//        val delta = topRightCellHi - topRightCellLo
//        leftBorderPath[Point2D(leftBorder.x, leftBorder.y)] = true
//        if (delta == 0) {
//            leftBorder.x -= 1
//            currentHi = topCellHi
//            currentLo = topCellLo
//        } else if (delta < 0) {
//            leftBorder.y += 1
//            currentHi = rightCellHi
//            currentLo = rightCellLo
//        } else {
//            throw NotImplementedError("THIS IS IMPOSSIBLE")
//        }
//    }
//    val rightBorderPath = hashMapOf<Point2D<Int>, Boolean>()
//    val goodPoints = mutableListOf<Point2D<Int>>()
//
//    // TODO стартуем с -1?
//    while (rightBorder != Point2D(0, P.width())) {
//        val topCellHi = 1
//        val rightCellHi = 1
//        val topRightCellHi = 1
//        val topCellLo = 1
//        val rightCellLo = 1
//        val topRightCellLo = 1
//        val delta = topRightCellHi - topRightCellLo
//        // move to if and else if
//        if (rightBorder in leftBorderPath) {
//            goodPoints.add(Point2D(rightBorder.x - 1, +rightBorder.y + 1))
//        }
//        if (delta == 0) {
//            rightBorder.y += 1
//            currentHi = rightCellHi
//            currentLo = rightCellLo
//        } else if (delta > 0) {
//            rightBorder.x -= 1
//            currentHi = topCellHi
//            currentLo = topCellLo
//        } else {
//            throw NotImplementedError("THIS IS IMPOSSIBLE")
//        }
//    }
//
//    // filter R1 and R2
//    goodPoints.forEach { p ->
//        R1.resetInCol(p.y)
//        R1.resetInRow(p.x)
//        R2.resetInCol(p.y)
//        R2.resetInRow(p.x)
//    }
//
//
//    //add R2 points to R1
//    R2.X.forEachIndexed { x, y ->
//        R1[x, y] = true
//    }
//    //add good points to R1
//    goodPoints.forEach {
//        R1[it.x, it.y] = true
//    }
//
//    return R1
//}
//
//
//fun semiLocalLcs(a: String, b: String, n: Int, m: Int): List<Point2D<Int>> = when {
//    n == 1 && m == 1 -> {
//        // a[n-1] == b[m-1]
//        if (a == b) listOf(Point2D(0, 0), Point2D(1, 1))
//        else listOf(Point2D(1, 0), Point2D(0, 1))
//    }
//
//    m == 1 && n > 1 -> {
//        val n1 = n / 2
//        val n2 = n - n / 2
//        val a1 = a.substring(0, n1)
//        val a2 = a.substring(n1, n)
//        steadyAnt(semiLocalLcs(a1, b, n1, m), semiLocalLcs(a2, b, n2, m), n1, n2, m)
//    }
//
//    n == 1 && m > 1 -> {
//        TODO()
//    }
//    n > 1 && m > 1 -> {
//        TODO()
//
//    }
//
//    else -> throw IllegalArgumentException("SemiLocalLcs:n=$n,m=$m")
//
//
//}
