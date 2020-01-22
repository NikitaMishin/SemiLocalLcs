///**
// *  Semi Local problems
// */
//
//
///**
// *
// */
//abstract class SemiLocalLCS<E> {
//    abstract var fragmentA: List<E>
//    abstract var fragmentB: List<E>
//    // TODO ideompotence operation
//    abstract fun solve()
//
//    // TODO waht args
//    abstract fun query(i: Int, j: Int)
//}
//
///**
// *
// */
//class SemiLocalLCSByMonge<E, T : MatrixElem, M : MongeMatrix<T>>(
//    private var solver: SemiLocalLCSSolveStrategy<T, M>,
//    override var fragmentA: List<E>,
//    override var fragmentB: List<E>) : SemiLocalLCS<E>() {
//    private lateinit var matrix: M
//    override fun solve() {
//        //should be ideompotence
//        matrix = solver.solve(fragmentA, fragmentB)
//    }
//
//    fun getMatrix(): M = matrix
//
//    fun setSolver(newSolver: SemiLocalLCSSolveStrategy<T, M>) {
//        solver = newSolver
//    }
//
//    override fun query(i: Int, j: Int) {
//        //matrix.get()
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}
//
//
///**TODO K constraint comparable and hash
// * or move him to class declaration
// *
// */
//abstract class SemiLocalLCSSolveStrategy<T : MatrixElem, M : MongeMatrix<T>>() {
//    abstract fun <K> solve(a: List<K>, b: List<K>): M
//}
//
///**
// *
// */
//class SemiLocalLCSSolveStrategyRecursive<T : MatrixElem, M : MongeMatrix<T>>() : SemiLocalLCSSolveStrategy<T, M>() {
//    override fun <K> solve(a: List<K>, b: List<K>): M {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
//
///**
// *
// */
//class SemiLocalLCSSolveStrategyIterative<T : MatrixElem, M : MongeMatrix<T>>() : SemiLocalLCSSolveStrategy<T, M>() {
//    override fun <K> solve(a: List<K>, b: List<K>): M {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}


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

/**
 *
 */
abstract class AbstractPermutationMatrix {
    /**
     *
     */
    enum class GetType {
        ROW,
        COLUMN
    }

    /**
     *
     */
    val NOPOINT = -1

    /**
     *
     */
    abstract fun height(): Int

    /**
     *
     */
    abstract fun width(): Int

    /**
     *
     */
    abstract operator fun get(row: Int, col: Int): Boolean

    /**
     *
     */
    abstract operator fun set(row: Int, col: Int, value: Boolean)

    /**
     *
     */
    abstract operator fun get(pos: Int, getType: GetType): Int

    /**
     *
     */
    abstract fun resetInRow(row: Int)

    /**
     *
     */
    abstract fun resetInColumn(column: Int)

    /**
     *
     */
    abstract fun createZeroMatrix(height: Int, width: Int): AbstractPermutationMatrix
}

/**
 *
 */
class CountingQuery() {

    /**
     *
     */
    inline fun dominanceSumTopLeftUpMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        val iCap = i - 1
        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum - if (jCap < j) -1 else 0
    }

    /**
     *
     */
    inline fun dominanceSumTopLeftRightMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        val jCap = j + 1
        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) 1 else 0
    }

    /**
     *
     */
    inline fun dominanceSumBottomRightUpMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        val iCap = i - 1
        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap < j) 0 else 1
    }

    /**
     *
     */
    inline fun dominanceSumBottomRightRightMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        val jCap = j + 1
        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum - if (iCap < i) 0 else 1
    }


}

/**
 * Also sub permutation matrices
 * Format:
 * ROWS in form  row -> col , if ROW -> NOPOINT => no points at all in row.
 * COLS in form col -> row , if COL ->  NOPOINT => no point at all in col.
 * Standart indexation from 0 to n - 1
 */
data class PermutationMatrixTwoLists(private var rows: MutableList<Int>, private var cols: MutableList<Int>) :
    AbstractPermutationMatrix() {

    override fun height() = rows.size

    override fun width() = cols.size

    override fun set(row: Int, col: Int, value: Boolean) = when (value) {
        true -> {
            rows[row] = col
            cols[col] = row
        }
        false -> {
            rows[row] = NOPOINT
            cols[col] = NOPOINT
        }
    }


    override fun get(pos: Int, getType: GetType): Int = when (getType) {
        GetType.ROW -> rows[pos]
        GetType.COLUMN -> cols[pos]
    }

    override fun get(row: Int, col: Int): Boolean = rows[row] == col

    override fun resetInRow(row: Int) {
        val column = rows[row]
        rows[row] = NOPOINT
        if (column != NOPOINT) cols[column] = NOPOINT
    }

    override fun resetInColumn(column: Int) {
        val row = cols[column]
        cols[column] = NOPOINT
        if (row != NOPOINT) rows[row] = NOPOINT
    }

    override fun createZeroMatrix(height: Int, width: Int): AbstractPermutationMatrix = PermutationMatrixTwoLists(
        (0 until height).map { NOPOINT }.toMutableList(), (0 until width).map { NOPOINT }.toMutableList()
    )


}

internal enum class Step {
    UP,
    RIGHT
}

/**
 * Fast multiplication for permutation and subpermutation matrices each with at most n nonzeros.
 * The product is obtained in O(nlogn) time.
 * The details see on page 30.
 */
fun steadyAnt(
    P: AbstractPermutationMatrix,
    Q: AbstractPermutationMatrix
):
        AbstractPermutationMatrix {

    fun getP1(colExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
        val newToOldRows = mutableMapOf<Int, Int>()
        val oldToNewRows = mutableMapOf<Int, Int>()
        val newRowPoints = mutableListOf<Int>()

        for (row in 0 until P.height()) {
            val col = P[row, AbstractPermutationMatrix.GetType.ROW]
            if (col < colExclusive && col != P.NOPOINT) {
                newToOldRows[newRowPoints.size] = row
                oldToNewRows[row] = newRowPoints.size
                newRowPoints.add(col)
            }
        }

        //zero matrix get
        if (newRowPoints.size == 0) return Pair(false, null)

        val nextColPoints = mutableListOf<Int>()
        for (col in 0 until colExclusive) {
            val oldRow = P[col, AbstractPermutationMatrix.GetType.COLUMN]
            nextColPoints.add(oldToNewRows.getOrDefault(oldRow, P.NOPOINT))
        }
        //TODO  is really needed?
        //if (newRowPoints.size == 0) return Pair(false,null)

        val matrix = P.createZeroMatrix(height = newRowPoints.size, width = nextColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            newRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            nextColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(true, Pair(matrix, newToOldRows))
    }

    fun getP2(colExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
        val newToOldRows = mutableMapOf<Int, Int>()
        val oldToNewRows = mutableMapOf<Int, Int>()
        val newRowPoints = mutableListOf<Int>()

        for (row in 0 until P.height()) {
            val col = P[row, AbstractPermutationMatrix.GetType.ROW]
            if (col >= colExclusive && col != P.NOPOINT) {
                newToOldRows[newRowPoints.size] = row
                oldToNewRows[row] = newRowPoints.size
                newRowPoints.add(col - colExclusive)
            }
        }

        //zero matrix get
        if (newRowPoints.size == 0) return Pair(false, null)

        val nextColPoints = mutableListOf<Int>()

        for (col in colExclusive until P.width()) {
            val oldRow = P[col, AbstractPermutationMatrix.GetType.COLUMN]
            nextColPoints.add(oldToNewRows.getOrDefault(oldRow, P.NOPOINT))
        }

        //TODO  is really needed?
        //if (newRowPoints.size == 0) return Pair(false,null)

        val matrix = P.createZeroMatrix(height = newRowPoints.size, width = nextColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            newRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            nextColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(true, Pair(matrix, newToOldRows))
    }

    fun getQ1(rowExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
        val newToOldCol = mutableMapOf<Int, Int>()
        val oldToNewCol = mutableMapOf<Int, Int>()
        val newColPoints = mutableListOf<Int>()

        for (col in 0 until Q.width()) {
            val row = Q[col, AbstractPermutationMatrix.GetType.COLUMN]

            if (row != Q.NOPOINT && row < rowExclusive) {
                newToOldCol[newColPoints.size] = col
                oldToNewCol[col] = newColPoints.size
                newColPoints.add(row)
            }
        }
        if (newColPoints.size == 0) return Pair(false, null)

        val nexRowPoints = mutableListOf<Int>()
        for (row in 0 until rowExclusive) {
            val oldCol = Q[row, AbstractPermutationMatrix.GetType.ROW]
            nexRowPoints.add(oldToNewCol.getOrDefault(oldCol, Q.NOPOINT))
        }

        val matrix = P.createZeroMatrix(height = nexRowPoints.size, width = newColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            nexRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            newColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(true, Pair(matrix, newToOldCol))

    }

    fun getQ2(rowExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
        val newToOldCol = mutableMapOf<Int, Int>()
        val oldToNewCol = mutableMapOf<Int, Int>()
        val newColPoints = mutableListOf<Int>()

        for (col in 0 until Q.width()) {
            val row = Q[col, AbstractPermutationMatrix.GetType.COLUMN]

            if (row != Q.NOPOINT && row >= rowExclusive) {
                newToOldCol[newColPoints.size] = col
                oldToNewCol[col] = newColPoints.size
                newColPoints.add(row - rowExclusive)
            }
        }

        if (newColPoints.size == 0) return Pair(false, null)

        val nexRowPoints = mutableListOf<Int>()

        for (row in rowExclusive until Q.height()) {
            val oldCol = Q[row, AbstractPermutationMatrix.GetType.ROW]
            nexRowPoints.add(oldToNewCol.getOrDefault(oldCol, Q.NOPOINT))
        }

        val matrix = P.createZeroMatrix(height = nexRowPoints.size, width = newColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            nexRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            newColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(true, Pair(matrix, newToOldCol))
    }

    fun inverseMapping(
        newToOldX: MutableMap<Int, Int>, newToOldY: MutableMap<Int, Int>, height: Int, width: Int,
        shrinkMatrix: AbstractPermutationMatrix
    ): AbstractPermutationMatrix {

        val matrix = P.createZeroMatrix(height, width)

        for (row in 0 until shrinkMatrix.height()) {
            val col = shrinkMatrix[row, AbstractPermutationMatrix.GetType.ROW]
            if (col != shrinkMatrix.NOPOINT) matrix[newToOldX[row]!!, newToOldY[col]!!] = true
        }
        return matrix
    }


//    if (P.width() == 1) return Q base case
    val widthP1 = P.width() / 2
    val widthP2 = P.width() - widthP1
    val (P1IsZero, P1) = getP1(widthP1)
    val (P2IsZero, P2) = getP2(widthP1)
    val (Q1IsZero, Q1) = getQ1(widthP1)
    val (Q2IsZero, Q2) = getQ2(widthP1)
    //CASE WHEN P1 OR Q1 IS ZERO


    // R1 same dimension as R2
    val R1 = inverseMapping(P1!!.second, Q1!!.second, P.height(), Q.width(), steadyAnt(P1.first, Q1.first))
    val R2 = inverseMapping(P2!!.second, Q2!!.second, P.height(), P.width(), steadyAnt(P2.first, Q2.first))


// size of grid bigger in each dimension on one
    //start from <n^+,0^-> to  <0^-,n^+>
    val endPos = Position2D(-1, R1.width() + 1) // или +2?
    val currentPos = Position2D(R1.height() + 1, -1)//?? or 0
    var delta = 0
    var RHi = 0 // now at point <n^+,0^->
    var RLo = 0 // now at point <n^+,0^->
    var RHiNext = 0
    var RLoNext = 0

    var step = Step.UP
    while (currentPos != endPos) {
        if (currentPos.i == 0) {
            // could terminate
            step = Step.RIGHT
            currentPos.j += 1
            continue
        }
        // go
        val posExtendedMatrix = Position2D(currentPos.i - 1, currentPos.j + 1)

        //obtain Rhi a rLo for current point
        if (step == Step.RIGHT) {

//            RHiNext = RHi - if () 0 else 1

        } else {
            //step up
        }

        // check conditions for step


        //check is good point
        // delta[\bar{i}^{-},\bar{j}^{-}] < 0 and delta[\bar{i}^{+},\bar{j}^{+}] > 0
//        val deltaMinusMinus =


//            delta =
//                RHiNext = RHi
//        delta =


    }

    // filter R1 and R2
//    goodPoints.forEach { p ->
//        R1.resetInCol(p.j)
//        R1.resetInRow(p.i)
//        R2.resetInCol(p.j)
//        R2.resetInRow(p.i)
//    }
//
//
//    //add R2 points to R1
//    R2.X.forEachIndexed { x, y ->
//        R1[x, y] = true
//    }
//    //add good points to R1
//    goodPoints.forEach {
//        R1[it.i, it.j] = true
//    }
//
//    return R1
    TODO()
}


//fun semiLocalLcs(a: String, b: String, n: Int, m: Int): List<Position2D<Int>> = when {
//    n == 1 && m == 1 -> {
//        // a[n-1] == b[m-1]
//        if (a == b) listOf(Position2D(0, 0), Position2D(1, 1))
//        else listOf(Position2D(1, 0), Position2D(0, 1))
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
