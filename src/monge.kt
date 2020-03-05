import java.util.*
import kotlin.collections.HashMap

/**
* TODO Irrelevant file?
 * Useful - smawk algorithm row minima
**/

/**
 * Wrapper for numerical values of utils.Matrix elements()
 * */
abstract class ElemWrapper : Cloneable {

    abstract val positiveInfinity: ElemWrapper

    abstract val negativeInfinity: ElemWrapper

    abstract val neutralElement: ElemWrapper

    /**
     * String presentation of wrapped element
     */
    abstract fun printable(): String

    abstract operator fun plus(mongeElement: ElemWrapper): ElemWrapper

    abstract operator fun times(mongeElement: ElemWrapper): ElemWrapper

    abstract operator fun plusAssign(mongeElement: ElemWrapper)

    abstract operator fun timesAssign(mongeElement: ElemWrapper)

    public abstract override fun clone(): ElemWrapper

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    abstract operator fun compareTo(elemWrapper: ElemWrapper): Int


    fun min(other: ElemWrapper): ElemWrapper = if (this <= other) this else other

    fun max(other: ElemWrapper): ElemWrapper = if (this >= other) this else other

}

/**
 * Wrapper class for Int elements
 */
class IntWrapper(var number: Int) : ElemWrapper() {
    override val positiveInfinity: IntWrapper
        get() = IntWrapper(Int.MAX_VALUE)
    override val negativeInfinity: IntWrapper
        get() = IntWrapper(Int.MIN_VALUE)
    override val neutralElement: IntWrapper
        get() = IntWrapper(0)


    override fun printable(): String = number.toString()

    override fun plus(mongeElement: ElemWrapper): IntWrapper =
        IntWrapper((mongeElement as IntWrapper).number + number)

    override fun times(mongeElement: ElemWrapper): IntWrapper =
        IntWrapper((mongeElement as IntWrapper).number * number)

    override fun plusAssign(mongeElement: ElemWrapper) {
        number += (mongeElement as IntWrapper).number
    }

    override fun timesAssign(mongeElement: ElemWrapper) {
        number *= (mongeElement as IntWrapper).number
    }

    override fun equals(other: Any?): Boolean {
        if (other is IntWrapper) return other.number == number
        return false
    }

    override fun hashCode(): Int = number.hashCode()

    override fun compareTo(elemWrapper: ElemWrapper): Int {
        val other = elemWrapper as IntWrapper
        return number.compareTo(other.number)
    }

    override fun clone(): ElemWrapper {
        return IntWrapper(number)
    }
}


///////////////////////////////////////////////////////////////////////////////
/**
 * Class of Matrices that satisifed monge property (aka totally monotone)
 */
abstract class MongeMatrix<T : ElemWrapper> : Cloneable {
    /**
     * Slice of row or column by specified position
     */
    abstract operator fun get(position: Int, isRow: Boolean): List<T>

    abstract operator fun times(mongeMatrix: MongeMatrix<T>): MongeMatrix<T>

    abstract operator fun plus(mongeMatrix: MongeMatrix<T>): MongeMatrix<T>

    abstract operator fun get(i: Int, j: Int): T

    abstract operator fun set(i: Int, j: Int, elem: T)

    abstract fun height(): Int

    abstract fun width(): Int

    abstract override fun clone(): MongeMatrix<T>

    fun printMatrix() {
        for (rowNum in 0 until height()) {
            for (colNum in 0 until width()) {
                print("${(this[rowNum, colNum]).printable()} ")
            }
            println()
        }
    }

    /**
     * Checking that monge property is satisfied.
     * An m-by-n matrix is said to be a Monge array if, for all  i, j ,k , l such that 1<=i<k<=m and 1<=j<l<=n
     * A[i,j] + A[k,l] <= A[i,l] + A[k,j]
     */
    fun isMongePropertySatisified(): Boolean {
        for (rowNum1 in 0 until height()) {
            for (rowNum2 in rowNum1 + 1 until height()) {
                for (colNum1 in 0 until width()) {
                    for (colNum2 in colNum1 + 1 until width()) {
                        val diagL = this[rowNum1, colNum1]
                        val diagR = this[rowNum2, colNum2]
                        val antiDiagL = this[rowNum2, colNum1]
                        val antiDiagR = this[rowNum1, colNum2]
                        if (antiDiagL + antiDiagR < diagL + diagR) return false
                    }
                }
            }
        }
        return true
    }

    /**
     * smawk algorithm implementation. O(n*queryTimeForMatrixElementAccess)
     * @return Indexes of position in a row for each row in matrix
     */
    fun rowMinima(): List<Int> {
        val result = MutableList(this.height()) { -1 }

        fun smawk(rows: List<Int>, cols: List<Int>) {
            if (rows.isEmpty()) return

            val stack = Stack<Int>()
            for (col in cols) {
                while (true) {
                    if (stack.size == 0) break
                    val row = rows[stack.size - 1]
                    if (get(row, col) >= get(row, stack.peek())) break
                    stack.pop()
                }

                if (stack.size < rows.size) stack.push(col)
            }


            val oddRows = rows.filterIndexed { i, _ -> i % 2 == 1 }
            smawk(oddRows, stack)

            val colToIndex = HashMap<Int, Int>()
            stack.forEachIndexed { index, value -> colToIndex[value] = index }

            var begin = 0
            val optimizedAccess = stack.toIntArray()
            for (i in 0 until rows.size step 2) {
                val row = rows[i]
                var stop = optimizedAccess.size - 1
                if (i < rows.size - 1) stop = colToIndex[result[rows[i + 1]]]!!
                var argmin = optimizedAccess[begin]
                var min = get(row, argmin)
                for (c in begin + 1..stop) {
                    val value = get(row, optimizedAccess[c])
                    if (c == begin || value < min) {
                        argmin = optimizedAccess[c]
                        min = value
                    }
                }

                result[row] = argmin
                begin = stop
            }
        }

        smawk((0 until height()).toList(), (0 until width()).toList())
        return result.toList()
    }

    /**
     * //TODO check asymptotic
     * utils.Matrix-vector multiplication A*b = c
     * Note that c could be obtained by following:
     * <code>
     *     vectorIndices =
     *     val indices = A.matrixVectorMult(vector)
     *     val c = indices.forEachIndexed{row,col ->
     *        A[row,col] + vector[col]
     *     }
     *     c[row] = A[row, indices]  + vector[j]
     * </code>
     * smawk algorithm implementation. O(n*queryTimeForMatrixElementAccess)
     * @return Indexes of position in a row for each row in matrix
     */
    fun matrixVectorMult(vector: List<T>): MutableList<Int> {
        val result = MutableList(this.height()) { -1 }

        fun smawk(rows: List<Int>, cols: List<Int>) {
            if (rows.isEmpty()) return

            val stack = Stack<Int>()
            for (col in cols) {
                while (true) {
                    if (stack.size == 0) break
                    val row = rows[stack.size - 1]
                    if (get(row, col) + vector[col] >= get(row, stack.peek()) + vector[stack.peek()]) break
                    stack.pop()
                }

                if (stack.size < rows.size) stack.push(col)
            }


            val oddRows = rows.filterIndexed { i, _ -> i % 2 == 1 }
            smawk(oddRows, stack)

            val colToIndex = HashMap<Int, Int>()
            stack.forEachIndexed { index, value -> colToIndex[value] = index }

            var begin = 0
            val optimizedAccess = stack.toIntArray()
            for (i in 0 until rows.size step 2) {
                val row = rows[i]
                var stop = optimizedAccess.size - 1
                if (i < rows.size - 1) stop = colToIndex[result[rows[i + 1]]]!!
                var argmin = optimizedAccess[begin]
                var min = get(row, argmin) + vector[argmin]
                for (c in begin + 1..stop) {
                    val value = get(row, optimizedAccess[c]) + vector[optimizedAccess[c]]
                    if (c == begin || value < min) {
                        argmin = optimizedAccess[c]
                        min = value
                    }
                }

                result[row] = argmin
                begin = stop
            }
        }

        smawk((0 until height()).toList(), (0 until width()).toList())
        return result
    }


}

/**
 * Explicit Monge utils.Matrix with O(n*m) space complexity aka lower bound for multiplication algorithms
 */
class ExplicitMonge<T : ElemWrapper> private constructor() : MongeMatrix<T>() {
    lateinit var matrix: MutableList<MutableList<T>>
    private var height: Int = 0
    private var width: Int = 0


    /**
     * Note Pass by reference
     */
    constructor(init: MutableList<MutableList<T>>) : this() {
        this.height = init.size
        this.width = init[0].size
        matrix = init
    }

    constructor(height: Int, width: Int, initFunc: ((Int, Int) -> T)) : this() {
        this.height = height
        this.width = width
        matrix = MutableList(height) { i -> MutableList(width) { j -> initFunc(i, j) } }
    }


    /**
     * for row returns reference
     * for column return newly created list
     */
    override fun get(position: Int, isRow: Boolean): List<T> {
        // TODO by ref?
        return if (isRow) {
            matrix[position]
        } else {
            (0 until height).map { i -> get(i, position) }
        }
    }

    override fun plus(mongeMatrix: MongeMatrix<T>): MongeMatrix<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clone(): ExplicitMonge<T> = ExplicitMonge(height,width) { i, j -> get(i,j).clone() } as ExplicitMonge<T>


    /**
     * // TODO check asymptotic
     * (Min,+) matrix multiplication over Monge Matrices with running time O(width * height) using smawk
     */
    override fun times(B: MongeMatrix<T>): ExplicitMonge<T> {
        val element = get(0, 0).positiveInfinity // bad code
        val res = ExplicitMonge(height, B.width()) { _, _ -> element }

        //O(n)* smawk
        for (col in 0 until B.width()) {
            val b = B[col, false]
            val c = matrixVectorMult(b)
            c.forEachIndexed { row, j ->
                res[row, col] = get(row, j) + b[j]
            }
        }
        return res as ExplicitMonge<T>
    }


    override fun get(i: Int, j: Int): T = matrix[i][j]

    override fun set(i: Int, j: Int, elem: T) {
        matrix[i][j] = elem
    }

    override fun height(): Int = height

    override fun width(): Int = width

}


/**
 * Naive Monge matrix. Space complexity (n*m) with multiplication complexity O(n^3) aka naive
 * Use only for testing purposes
 */
class NaiveMonge<T : ElemWrapper> private constructor() : MongeMatrix<T>() {
    lateinit var matrix: MutableList<MutableList<T>>
    private var height: Int = 0
    private var width: Int = 0

    constructor(init: MutableList<MutableList<T>>) : this() {
        this.height = init.size
        this.width = init[0].size
        matrix = init
    }

    constructor(height: Int, width: Int, initFunc: ((Int, Int) -> T)) : this() {
        this.height = height
        this.width = width
        matrix = MutableList(height) { i -> MutableList(width) { j -> initFunc(i, j) } }
    }

    override fun get(position: Int, isRow: Boolean): List<T> {
        // TODO by ref?
        return if (isRow) {
            matrix[position]
        } else {
            (0 until height).map { i -> get(i, position) }
        }
    }

    override fun times(B: MongeMatrix<T>): NaiveMonge<T> {
        val element = get(0, 0).positiveInfinity // bad code
        val res = NaiveMonge(height, B.width()) { _, _ -> element }
        for (i in 0 until height) {
            for (k in 0 until B.width()) {
                var tmp = element.positiveInfinity
                for (j in 0 until width) {
                    tmp = tmp.min(this[i, j] + B[j, k])
                }
                res[i, k] = tmp
            }
        }

        return res as NaiveMonge<T>

    }

    override fun plus(mongeMatrix: MongeMatrix<T>): MongeMatrix<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(i: Int, j: Int): T = matrix[i][j]

    override fun set(i: Int, j: Int, elem: T) {
        matrix[i][j] = elem
    }

    override fun height(): Int = height

    override fun width(): Int = width

    override fun clone(): NaiveMonge<T> = NaiveMonge(matrix)
}


//
//class ImplicitMonge<T:ElemWrapper>:MongeMatrix<T>{
//
//}



//
///** Totally monotonne matrix
// *  25 21 13 10 20 13 19 35 37 41 58 66 82 99 124 133 156 178
// *  42 35 26 20 29 21 25 37 36 39 56 64 76 91 116 125 146 164
// *  57 48 35 28 33 24 28 40 37 37 54 61 72 83 107 113 131 146
// *  78 65 51 42 44 35 38 48 42 42 55 61 70 80 100 106 120 135
// *  90 76 58 48 49 39 42 48 39 35 47 51 56 63 80 86 97 110
// *  103 85 67 56 55 44 44 49 39 33 41 44 49 56 71 75 84 96
// *  123 105 86 75 73 59 57 62 51 44 50 52 55 59 72 74 80 92
// *  142 123 100 86 82 65 61 62 50 43 47 45 46 46 58 59 65 73
// *  151 130 104 88 80 59 52 49 37 29 29 24 23 20 28 25 31 39
// */