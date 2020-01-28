import java.lang.Exception
import kotlin.random.Random

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
 * Class that reprsents permutation and subpermutation matrices
 */
abstract class AbstractPermutationMatrix : Iterable<Position2D<Int>> {

    /**
     * The type of query for get queries such as matrix[i,type]
     */
    enum class GetType {
        ROW,
        COLUMN
    }

    /**
     * variable that state that noPoint at some position in a row
     */
    val NOPOINT = -1

    /**
     * height of permutation matrix
     */
    abstract fun height(): Int

    /**
     * width of permutation matrix
     */
    abstract fun width(): Int

    /**
     * Returns the value in position [row,col] in matrix
     */
    abstract operator fun get(row: Int, col: Int): Boolean

    /**
     * Sets the value in position [row,col] in matrix
     */
    abstract operator fun set(row: Int, col: Int, value: Boolean)

    /**
     * Returns the  position of non zero elemenet in a row(col) given positon in a col(row)
     * @param getType deterimines the type of query. For example matrix[col_i,,ColTYpe]
     * @return NOPOINT if no point in a row(col) or position in a row(col)
     */
    abstract operator fun get(pos: Int, getType: GetType): Int

    /**
     * Resets nonzero element in a row
     */
    abstract fun resetInRow(row: Int)

    /**
     * Resets nonzero element in a col
     */
    abstract fun resetInColumn(column: Int)

    /**
     * Create zero matrix with NOPOINT at each position in created matrix
     */
    abstract fun createZeroMatrix(height: Int, width: Int): AbstractPermutationMatrix

    abstract fun print()

    companion object {

        fun generatePermutationMatrix(
            height: Int,
            width: Int,
            nonZerosCount: Int,
            seed: Int
        ): AbstractPermutationMatrix {
            if (nonZerosCount > kotlin.math.min(height, width)) throw Exception("")
            val randomizer = Random(seed)
            var positions2D = mutableListOf<Position2D<Int>>()
            var remainingCount = nonZerosCount
            while (remainingCount > 0) {
                val randI = Math.abs(randomizer.nextInt()) % height
                val randJ = Math.abs(randomizer.nextInt()) % width
                if (positions2D.none { it.i == randI || it.j == randJ }) {
                    positions2D.add(Position2D(randI, randJ))
                    remainingCount--;
                }
            }
            return PermutationMatrixTwoLists(positions2D, height, width)
        }
    }


}

/**
 * Class for dominance sum counting queries with O(1) for permutation and subpermutation matrices.
 * Given the sum in position i,j each function returns sum in adjacent position for differnet type of dominance sum.
 * The prefix (SUM......) determines type of dominance sum whereas prefix (...Move)  determines adjacent posititon
 */
class CountingQuery {
    /**
     * see class definition
     */
    inline fun dominanceSumTopLeftLeftMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        var jCap = j
        if (jCap == 0) return sum
        jCap--

        var iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap >= i) 1 else 0
    }

    /**
     * see class definition
     */
    inline fun dominanceSumTopLeftDownMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        var iCap = i
        if (iCap >= permMatrix.height()) {
            return 0
        }
        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap >= j) -1 else 0
    }


    /**
     * see class definition
     */
    inline fun dominanceSumTopLeftUpMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        var iCap = i
        if (iCap == 0) {
            return sum
        }

        iCap -= 1

        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap >= j) 1 else 0
    }

    /**
     * see class definition
     */
    inline fun dominanceSumTopLeftRightMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {

        val jCap = j
        if (jCap >= permMatrix.width()) return 0

        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) 0 else -1
    }

    /**
     * see class definition
     */
    inline fun dominanceSumBottomRightLeftMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        var jCap = j
        if (jCap == 0) return sum
        jCap--

        var iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) -1 else 0
    }

    /**
     * see class definition
     */
    inline fun dominanceSumBottomRightDownMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        var iCap = i
        if (iCap >= permMatrix.height()) {
            return 0
        }
        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap < j) 1 else 0
    }

    /**
     * see class definition
     */
    inline fun dominanceSumBottomRightUpMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        var iCap = i

        if (iCap == 0) {
            return sum
        }
        iCap -= 1

        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum

        return sum + if (jCap >= j) 0 else -1
    }

    /**
     * see class definition
     */
    inline fun dominanceSumBottomRightRightMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        val jCap = j
        if (jCap >= permMatrix.width()) return 0

        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) 1 else 0
    }

    companion object {
        val topRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i >= row && pos.j < col }

        val topLeftSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i >= row && pos.j >= col } // below-right

        val bottomRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i < row && pos.j < col } //above-left


        fun dominanceMatrix(matrix: AbstractPermutationMatrix, func: (Position2D<Int>, Int, Int) -> Boolean)
                : Array<Array<Int>> {
            //half sizes for permutation matrix
            // and for croos is integer
            val dominanceMatrix = Array(matrix.height() + 1) { Array(matrix.width() + 1) { 0 } }
            for (row in dominanceMatrix.indices) {
                for (col in dominanceMatrix[0].indices) {
                    for (pos in matrix) {
                        if (func(pos, row, col)) dominanceMatrix[row][col]++
                    }
                }
            }
            return dominanceMatrix
        }

    }


}

/**
 * Also sub permutation matrices
 * Format:
 * ROWS in form  row -> col , if ROW -> NOPOINT => no points at all in row.
 * COLS in form col -> row , if COL ->  NOPOINT => no point at all in col.
 * Standart indexation from 0 to n - 1
 */
class PermutationMatrixTwoLists(positions: List<Position2D<Int>>, height: Int, width: Int) :
    AbstractPermutationMatrix() {

    private var rows: MutableList<Int> = MutableList(height) { NOPOINT }
    private var cols: MutableList<Int> = MutableList(width) { NOPOINT }

    init {
        for (p in positions) {
            rows[p.i] = p.j
            cols[p.j] = p.i
        }
    }

    override fun height() = rows.size

    override fun width() = cols.size

    override fun set(row: Int, col: Int, value: Boolean) = when (value) {
        true -> {
            rows[row] = col
            cols[col] = row
        }
        false ->
            if (this[row, col]) {
                rows[row] = NOPOINT
                cols[col] = NOPOINT
            } else {
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

    override fun createZeroMatrix(height: Int, width: Int): AbstractPermutationMatrix =
        PermutationMatrixTwoLists(mutableListOf(), height, width)


    /**
     * iterator over non zero elements in permutation matrix
     */
    override fun iterator(): Iterator<Position2D<Int>> {
        val height = height()
        val width = width()
        val cols = cols
        val rows = rows


        return object : Iterator<Position2D<Int>> {

            private val nonZeroPositions =
                if (height > width) cols.mapIndexed { col, row -> Position2D(row, col) }
                    .filter { it.i != NOPOINT }.toList()
                else rows.mapIndexed { row, col -> Position2D(row, col) }
                    .filter { it.j != NOPOINT }.toList()

            private var cur = 0

            override fun hasNext(): Boolean = cur < nonZeroPositions.size

            override fun next(): Position2D<Int> {
                val elem = nonZeroPositions[cur]
                cur++
                return elem

            }
        }
    }

    override fun print() {
        val dominanceMatrix = Array(height()) { Array(width()) { 0 } }
        for (pos in this) dominanceMatrix[pos.i][pos.j] = 1


        for (i in dominanceMatrix.indices) {
            for (j in dominanceMatrix[0].indices) {
                print("${dominanceMatrix[i][j]} ")
            }
            println()
        }
    }

}


/**
 * Step for function steady and for path restoring
 */
internal enum class Step {
    UP,
    RIGHT
}

/**
 * Fast multiplication for permutation and subpermutation matrices each with at most n nonzeros.
 * The product is obtained in O(nlogn) time.
 * The details see on page 30.
 */
fun steadyAnt(P: AbstractPermutationMatrix, Q: AbstractPermutationMatrix): AbstractPermutationMatrix {

    /**
     *
     */
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
        if (newRowPoints.size == 0) return Pair(true, null)

        val nextColPoints = mutableListOf<Int>()
        for (col in 0 until colExclusive) {
            val oldRow = P[col, AbstractPermutationMatrix.GetType.COLUMN]
            nextColPoints.add(oldToNewRows.getOrDefault(oldRow, P.NOPOINT))
        }

        val matrix = P.createZeroMatrix(height = newRowPoints.size, width = nextColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            newRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            nextColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldRows))
    }

    /**
     *
     */
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
        if (newRowPoints.size == 0) return Pair(true, null)

        val nextColPoints = mutableListOf<Int>()

        for (col in colExclusive until P.width()) {
            val oldRow = P[col, AbstractPermutationMatrix.GetType.COLUMN]
            nextColPoints.add(oldToNewRows.getOrDefault(oldRow, P.NOPOINT))
        }

        val matrix = P.createZeroMatrix(height = newRowPoints.size, width = nextColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            newRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            nextColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldRows))
    }

    /**
     *
     */
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
        if (newColPoints.size == 0) return Pair(true, null)

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

        return Pair(false, Pair(matrix, newToOldCol))

    }

    /**
     *
     */
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

        if (newColPoints.size == 0) return Pair(true, null)

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

        return Pair(false, Pair(matrix, newToOldCol))
    }

    /**
     *
     */
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

    //base case
    if (P.width() == 1) {
        val m = P.createZeroMatrix(P.height(), Q.width())
        val row = P[0, AbstractPermutationMatrix.GetType.COLUMN]
        val col = Q[0, AbstractPermutationMatrix.GetType.ROW]
        m[row, col] = row != P.NOPOINT && col != Q.NOPOINT
        return m
    }


    val widthP1 = P.width() / 2
    //val widthP2 = P.width() - widthP1
    val (P1IsZero, P1) = getP1(widthP1)
    val (P2IsZero, P2) = getP2(widthP1)
    val (Q1IsZero, Q1) = getQ1(widthP1)
    val (Q2IsZero, Q2) = getQ2(widthP1)
    val R1: AbstractPermutationMatrix?
    var R2: AbstractPermutationMatrix?

    //CASE WHEN P1 OR Q1 IS ZERO
    when {
        (P1IsZero || Q1IsZero) && (P2IsZero || Q2IsZero) -> return P.createZeroMatrix(P.height(), Q.width())
        (P1IsZero || Q1IsZero) -> {
            //then R1 is zero
            //TODO can we return R2???
            R1 = P.createZeroMatrix(P.height(), Q.width())
            R2 = inverseMapping(P2!!.second, Q2!!.second, P.height(), P.width(), steadyAnt(P2.first, Q2.first))
        }
        (P2IsZero || Q2IsZero) -> {
            R1 = inverseMapping(P1!!.second, Q1!!.second, P.height(), Q.width(), steadyAnt(P1.first, Q1.first))
            R2 = P.createZeroMatrix(P.height(), Q.width())
        }
        // all non zero products
        else -> {
            R1 = inverseMapping(P1!!.second, Q1!!.second, P.height(), Q.width(), steadyAnt(P1.first, Q1.first))
            R2 = inverseMapping(P2!!.second, Q2!!.second, P.height(), P.width(), steadyAnt(P2.first, Q2.first))
        }
    }

    // R1 and R2 of size mXn
    // indexation on dominance on bigger in each dimension
    // also for grid over dominance on one bigger so +2 overall


// size of grid bigger in each dimension on one
    //start from <n^+,0^-> to  <0^-,n^+>
    val endPos = Position2D(0, R1.width() + 1)

    val currentPos = Position2D(R1.height(), -1)
    var RHi = 0 // now at point <n^+,0^->
    var RLo = 0 // now at point <n^+,0^->

    // queries goes to extende matrix i.e (m+1)x(n+1)
    val countingQuery = CountingQuery()
    val goodPoints = mutableListOf<Position2D<Int>>()
    println("hi")
    R2.print()
    println()
    println("lo")
    R1.print()
    println()

    val hi = CountingQuery.dominanceMatrix(R2, CountingQuery.bottomRightSummator)
    val lo = CountingQuery.dominanceMatrix(R1, CountingQuery.topLeftSummator)


    println()

    for (i in 0 until hi.size) {
        for (j in hi[0].indices) {
            hi[i][j] -= lo[i][j]
            print(" ${hi[i][j]}")
        }
        println()
    }

    var step = Step.UP
    while (currentPos != endPos) {
        println(currentPos)
        if (currentPos.i == 0) {
            // could terminate
            step = Step.RIGHT
            currentPos.j += 1
            continue
        }
        // go
        val posDominanceMatrix = Position2D(currentPos.i - 1, currentPos.j + 1)

        if (step == Step.RIGHT) {

            RHi =
                countingQuery.dominanceSumBottomRightRightMove(posDominanceMatrix.i, posDominanceMatrix.j - 1, RHi, R2)
            RLo = countingQuery.dominanceSumTopLeftRightMove(posDominanceMatrix.i, posDominanceMatrix.j - 1, RLo, R1)
            posDominanceMatrix.j--;
        } else {
            RHi = countingQuery.dominanceSumBottomRightUpMove(posDominanceMatrix.i + 1, posDominanceMatrix.j, RHi, R2)
            RLo = countingQuery.dominanceSumTopLeftUpMove(posDominanceMatrix.i + 1, posDominanceMatrix.j, RLo, R1)
            posDominanceMatrix.i++
        }

        when {
            RHi - RLo < 0 -> {
                // go right
                step = Step.RIGHT
                currentPos.j++
            }
            RHi - RLo == 0 -> {
                step = Step.UP
                currentPos.i--;
            }
            else -> {
                R2.print()
                println()
                val hi = CountingQuery.dominanceMatrix(R2, CountingQuery.bottomRightSummator)
                val lo = CountingQuery.dominanceMatrix(R1, CountingQuery.topLeftSummator)


                println()

                for (i in 0 until hi.size) {
                    for (j in hi[0].indices) {
                        hi[i][j] -= lo[i][j]
                        print(" ${hi[i][j]}")
                    }
                    println()
                }
                println()
                R2.print()
                println()
                naiveMultiplicationBraids(P, Q).print()

                throw Exception("Impossible case:${R1.width()} ${R1.height()}")
            }
        }


//TODO add edge cases when out of matrix
        //check if points is a good one
        // in cur point Rhi and Rlo
        /// check errorrs
        val deltaAboveLeft =
            countingQuery.dominanceSumBottomRightLeftMove(posDominanceMatrix.i, posDominanceMatrix.j, RHi, R2)
        -countingQuery.dominanceSumTopLeftLeftMove(posDominanceMatrix.i, posDominanceMatrix.j, RLo, R1)
        val deltaBelowRight =
            countingQuery.dominanceSumBottomRightDownMove(posDominanceMatrix.i, posDominanceMatrix.j, RHi, R2)
        -countingQuery.dominanceSumTopLeftDownMove(posDominanceMatrix.i, posDominanceMatrix.j, RLo, R1)

        // -1 ????
        if (deltaAboveLeft < 0 && deltaBelowRight > 0)
            goodPoints.add(Position2D(posDominanceMatrix.i - 1, posDominanceMatrix.j - 1))

        //check is good point
        // delta[\bar{i}^{-},\bar{j}^{-}] < 0 and delta[\bar{i}^{+},\bar{j}^{+}] > 0


    }

    //filter in R1 and R2
    goodPoints.forEach { p ->
        R1.resetInColumn(p.j)
        R1.resetInRow(p.i)
        R2.resetInColumn(p.j)
        R2.resetInRow(p.i)
    }

    // make better
    for (nonzeroPointsPosR1 in R2) {
        R1[nonzeroPointsPosR1.i, nonzeroPointsPosR1.j] = true
    }
    goodPoints.forEach {
        R1[it.i, it.j] = true
    }
    R1.print()
    return R1
}

fun naiveMultiplicationBraids(a: AbstractPermutationMatrix, b: AbstractPermutationMatrix)
        : AbstractPermutationMatrix {
    val aDominance = CountingQuery.dominanceMatrix(a, CountingQuery.topRightSummator)
    val bDominance = CountingQuery.dominanceMatrix(b, CountingQuery.topRightSummator)
    val cDominance: Array<Array<Int>> = Array(a.height() + 1) { Array(b.width() + 1) { 0 } }
    for (i in 0 until a.height() + 1) {
        for (k in 0 until b.width() + 1) {
            var tmp = Int.MAX_VALUE
            for (j in 0 until a.width() + 1) {
                tmp = Integer.min(aDominance[i][j] + bDominance[j][k], tmp)

            }
            cDominance[i][k] = tmp
        }
    }

    val c = a.createZeroMatrix(a.height(), b.width())

    for (i in 0 until a.height()) {
        for (j in 0 until b.width()) {
            c[i, j] =
                (cDominance[i][j + 1] + cDominance[i + 1][j] - cDominance[i][j] - cDominance[i + 1][j + 1]) == 1
        }
    }

    return c
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
