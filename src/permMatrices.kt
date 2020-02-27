import utils.Position2D
import kotlin.math.abs
import kotlin.random.Random


typealias Matrix = AbstractPermutationMatrix
/**
 * Class that represents permutation and subpermutation matrices
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
 * see definiton
 */
class  SubBistochasticMatrix(private val width: Int,private val height: Int, private val v:Int){

    /**
     * Returns the value in position [row,col] in matrix
     */
    operator fun get(row: Int, col: Int): Double {
        TODO()
    }

    /**
     * Sets the value in position [row,col] in matrix
     */
    operator fun set(row: Int, col: Int, value: Double){
        TODO()
    }



}

//
//typealias PosValue = Pair<Pair<Int,Int>, Double>
//abstract class AbstractSubBistochasticMatrix:Iterable<PosValue>{
//    /**
//     * The type of query for get queries such as matrix[i,type]
//     */
//    enum class GetType {
//        ROW,
//        COLUMN
//    }
//
//    /**
//     * height of subbistochastic matrix
//     */
//    abstract fun height(): Int
//
//    /**
//     * width of subbistochastic matrix
//     */
//    abstract fun width(): Int
//
//    /**
//     * Returns the value in position [row,col] in matrix
//     */
//    abstract operator fun get(row: Int, col: Int): Boolean
//
//    /**
//     * Sets the value in position [row,col] in matrix
//     */
//    abstract operator fun set(row: Int, col: Int, value: Boolean)
//
//    /**
//     * Returns the  positions with values of non zero element in a row(col) given position in a col(row)
//     * @param getType determines the type of query. For example matrix[col_i,,ColTYpe]
//     * @return null if no point in a row(col) or position in a row(col)
//     */
//    abstract operator fun get(pos: Int, getType: GetType): List<PosValue>?
//
//    /**
//     * Resets nonzero element in a row
//     */
//    abstract fun resetInRow(row: Int)
//
//    /**
//     * Resets nonzero element in a col
//     */
//    abstract fun resetInColumn(column: Int)
//
//    /**
//     * weather matrix is stochastic or not
//     */
//    abstract fun isStochastic(): Boolean
//
//    /**
//     * Create zero matrix with NOPOINT at each position in created matrix
//     */
//    abstract fun createZeroMatrix(height: Int, width: Int): AbstractPermutationMatrix
//
//    abstract fun print()
//
//
//}
