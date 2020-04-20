package utils

import longestCommonSubsequence.AbstractMongeMatrix
import longestCommonSubsequence.ReducingKernelEvaluation
import java.util.*
import kotlin.math.abs
import kotlin.random.Random



interface IStochasticMatrix{
    /**
     * all elements in matrices is multiple by factor v
     */
    val v:Int

    /**
     * height of matrix
     */
    fun height(): Int

    /**
     * width of matrix
     */
    fun width(): Int
    /**
     * Get all points in specified row
     * @param row
     */
    fun getAllInRow(row: Int): List<Position2D<Int>>
    /**
     * Get all points in specified col
     * @param col
     */
    fun getAllInCol(col: Int): List<Position2D<Int>>

    companion object{
        fun generateStochasticMatrix(height: Int, width: Int, v:Int, seed: Int): IStochasticMatrix {
            val randomizer = Random(seed)
            val positions2D = mutableListOf<Position2D<Int>>()
            val used = IntArray(width){0}
            for (row in 0 until height) {
                    for (trie in 0 until v) {
                        val randJ = abs(randomizer.nextInt()) % width
                        val value = abs(randomizer.nextInt()) % 100
                        if(used[randJ] < v){
                            used[randJ]++
                            positions2D.add(Position2D(row,randJ,value))
                    }
                }
            }
            return VSubBistochasticMatrix(positions2D, height, width, v)
        }
    }



}



typealias Matrix = AbstractPermutationMatrix
/**
 * Class that represents permutation and subpermutation matrices
 */
abstract class AbstractPermutationMatrix : Iterable<Position2D<Int>>, IStochasticMatrix {

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
     * Returns the value in position [row,col] in matrix
     */
    abstract operator fun get(row: Int, col: Int): Boolean

    /**
     * Sets the value in position [row,col] in matrix
     */
    abstract operator fun set(row: Int, col: Int, value: Boolean)

    /**
     * Returns the  position of non zero element in a row(col) given position in a col(row)
     * @param getType determines the type of query. For example matrix[col_i,,ColTYpe]
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
     * wheather matrix is stochastic or not
     */
    abstract fun isStochastic(): Boolean

    /**
     * Create zero matrix with NOPOINT at each position in created matrix
     */
    abstract fun createZeroMatrix(height: Int, width: Int): AbstractPermutationMatrix

    abstract fun print()

    companion object {
        fun generatePermutationMatrix(height: Int, width: Int, nonZerosCount: Int, seed: Int): Matrix {
            if (nonZerosCount > kotlin.math.min(height, width)) throw Exception("")
            val randomizer = Random(seed)
            val positions2D = mutableListOf<Position2D<Int>>()
            var remainingCount = nonZerosCount
            while (remainingCount > 0) {
                val randI = abs(randomizer.nextInt()) % height
                val randJ = abs(randomizer.nextInt()) % width
                if (positions2D.none { it.i == randI || it.j == randJ }) {
                    positions2D.add(Position2D(randI, randJ))
                    remainingCount--;
                }
            }
            return PermutationMatrixTwoLists(positions2D, height, width)
        }

        fun generateSquarePermutationMatrix(a: Int, b: Int,seed: Int,limit:Int =100): Matrix {
            val randomizer = Random(seed)
            val kernel = ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists}).evaluate(
                (0 until a).map { randomizer.nextInt(0,limit) },
                (0 until b).map { randomizer.nextInt(0,limit) }
            )

            return kernel
        }
    }

}

fun AbstractPermutationMatrix.isEquals(b: AbstractPermutationMatrix): Boolean {
    if (b.height() != this.height() || this.width() != b.width()) return false
    for (i in 0 until this.height()) {
        for (j in 0 until this.width()) {
            if (this[i, j] != b[i, j]) return false
        }
    }
    return true
}


/**
 * Also sub permutation matrices
 * Format:
 * ROWS in form  row -> col , if ROW -> NOPOINT => no points at all in row.
 * COLS in form col -> row , if COL ->  NOPOINT => no point at all in col.
 * Standart indexation from 0 to n - 1
 */
class PermutationMatrixTwoLists(positions: List<Position2D<Int>>, height: Int, width: Int) : Matrix() {
    override val v: Int
        get() = 1

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

    override fun set(row: Int, col: Int, value: Boolean) {
        if (value) {
            rows[row] = col
            cols[col] = row
        } else if (this[row, col]) {
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

    override fun isStochastic(): Boolean {
        return if (width() > height()) !cols.any { it == NOPOINT }
        else !rows.any { it == NOPOINT }
    }

    override fun createZeroMatrix(height: Int, width: Int): Matrix =
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

    override fun getAllInRow(row: Int): List<Position2D<Int>> {
        val col = this[row, GetType.ROW]
        return if (col != NOPOINT) listOf(Position2D(row,col)) else listOf()
    }

    override fun getAllInCol(col: Int): List<Position2D<Int>> {
        val row = this[col, GetType.COLUMN]
        return  if (row!=NOPOINT) listOf(Position2D(row,col)) else listOf()
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



class VSubBistochasticMatrix(points: List<Position2D<Int>>, height: Int, width: Int, override val v: Int):
    IStochasticMatrix {

    private val arrColToRow = Array(width) { LinkedList<Position2D<Int>>() }
    private val arrRowToCol = Array(height) { LinkedList<Position2D<Int>>() }

    override fun height() = arrRowToCol.size

    override fun width() = arrColToRow.size

    /**
     * The type of query for get queries such as matrix[i,type]
     */
    enum class GetType {
        ROW,
        COLUMN
    }

    init {
        for (pos in points) {
            arrRowToCol[pos.i].add(Position2D(pos.i, pos.j, pos.value))
            arrColToRow[pos.j].add(Position2D(pos.i, pos.j, pos.value))
        }
    }

    override fun getAllInCol(col: Int): List<Position2D<Int>> = this[col, GetType.COLUMN]

    override fun getAllInRow(row: Int): List<Position2D<Int>> = this[row, GetType.ROW]

    operator fun get(pos: Int, type: GetType) = when (type) {
        GetType.ROW -> arrRowToCol[pos]
        GetType.COLUMN -> arrColToRow[pos]
    }

}
