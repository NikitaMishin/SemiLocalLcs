import java.util.*
import kotlin.collections.HashMap

abstract class ElemWrapper {
    abstract operator fun plus(mongeElement: ElemWrapper): ElemWrapper
    abstract operator fun times(mongeElement: ElemWrapper): ElemWrapper
    abstract operator fun plusAssign(mongeElement: ElemWrapper)
    abstract operator fun timesAssign(mongeElement: ElemWrapper)
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
    abstract operator fun compareTo(elemWrapper: ElemWrapper): Int
    abstract fun copy(): ElemWrapper
    abstract val positiveInfinity: ElemWrapper
    abstract val negativeInfinity: ElemWrapper
    abstract val neutralElement: ElemWrapper

    fun min(other: ElemWrapper): ElemWrapper = if (this <= other) this else other
    fun max(other: ElemWrapper): ElemWrapper = if (this >= other) this else other
}

class IntWrapper(var number: Int) : ElemWrapper() {
    override fun plus(mongeElement: ElemWrapper): ElemWrapper =
        IntWrapper((mongeElement as IntWrapper).number + number)

    override fun times(mongeElement: ElemWrapper): ElemWrapper =
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

    override fun copy(): ElemWrapper {
        return IntWrapper(number)
    }

    override val positiveInfinity: ElemWrapper
        get() = IntWrapper(Int.MAX_VALUE)
    override val negativeInfinity: ElemWrapper
        get() = IntWrapper(Int.MIN_VALUE)
    override val neutralElement: ElemWrapper
        get() = IntWrapper(0)
}


abstract class Matrix<T : ElemWrapper> {
    abstract operator fun times(mongeMatrix: Matrix<T>): Matrix<T>
    abstract operator fun plus(mongeMatrix: Matrix<T>): Matrix<T>
    abstract operator fun get(i: Int, j: Int): T
    abstract operator fun set(i: Int, j: Int, elem: T)
    abstract fun height(): Int
    abstract fun width(): Int

    fun printMatrix() {
        for (rowNum in 0 until height()) {
            for (colNum in 0 until width()) {
                print("${this[rowNum, colNum]} ")
            }
            println()
        }
    }
}

abstract class MongeMatrix<T : ElemWrapper> : Matrix<T>() {

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
}


//
//
//class NaiveMongeMatrix<T : ElemWrapper>(private var height: Int, private var width: Int,
//                                        initializer: (Int) -> T, initValue:T ) :
//    MongeMatrix<T>() {
//
////TODO check
//    private val negativeInfinity  = initValue.negativeInfinity
//    private val positiveInfinity  = initValue.positiveInfinity
//    private var matrix: MutableList<MutableList<T>> = MutableList(height) { MutableList(width, initializer) }
//
//    override fun times(mongeMatrix: Matrix<T>): Matrix<T> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun plus(mongeMatrix: Matrix<T>): Matrix<T> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun get(i: Int, j: Int): T = matrix[i][j]
//
//    override fun set(i: Int, j: Int, elem:T) {
//       matrix[i][j] = elem
//    }
//
//
//    override fun height(): Int = this.height
//
//    override fun width(): Int = this.width
//
//    private fun getZeroMatrixInstance(height: Int, width: Int): MutableList<MutableList<T>> {
//        return MutableList(height) { _ -> MutableList<T>(width) { _ -> negativeInfinity.copy() as T } }
//    }
//
//
//
//
//
//     fun dotProduct(other: NaiveMongeMatrix<T>): Matrix<Double> {
//
//        if (width != other.height()) {
//            throw NotImplementedError("Implement errors")
//        }
//
//         val result = NaiveMongeMatrix(height,other.width(),{ i->  })
//
//
//        for (i in 0 until height) {
//            for (k in 0 until other.width()) {
//                var tmp = negativeInfinity
//                for (j in 0 until width) {
//                    tmp = this[i,j] + other[j,k]
//                    tmp = tmp.min(matrix[i,j] + other[j,k])
//                    tmp = min(tmp, (mongeArray[i][j] + other.getElem(j, k)))
//                }
//                result.setElem(i, k, tmp)
//            }
//        }
//
//        return result
//    }
//}
//
//// O(n^2) for dot multiplication via searching row minima
//
//class SmartExplicitMongeMatrixDouble(height: Int, width: Int) : MongeMatrix<Double>(height, width) {
//
//    inline override fun plus(a: Double, b: Double): Double =
//        min(a, b) //To change body of created functions use File | Settings | File Templates.
//
//    inline override fun mul(a: Double, b: Double): Double = a + b
//
//    override fun plus(a: Double, b: Double): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun mul(a: Double, b: Double): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun setElem(rowNum: Int, colNum: Int, elem: Double) {
//
//
//    }
//
//    override fun getElem(rowNum: Int, colNum: Int): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun dotProduct(other: Matrix<Double>): Matrix<Double> {
//        /**
//         *     Aggarwal, A., Klawe, M., Moran, S., Shor, P., & Wilber, R. (1986).
//         *     Geometric applications of a matrix searching algorithm. Proceedings of
//         *     for arbitrary there restriction of worst case O(nlogn)
//         *     Linear asymptotic for totallymonotone monge
//         */
//
//        //TODO fast concatenation in O(1) need to implmeennt custom iterator
//        fun rowMinima(vectorNum: Int, rowIndices: Sequence<Double>, colIndices: Sequence<Double>) {
//            // search row minima in A' where A'[i,j] = A[i,j] + other[:,j][j] for all i,j
//
//            //base case
//            if (rowIndices.size == 0) {
//                return to
//            }
//
//
//            // Reduce step
//
//            // Interpolate step
//
//
//        }
//
//        fun reduce(vectorNum: Int) {
//
//        }
//
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
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