import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SMAWKTest {

    internal class DummyMongeMatrix(
        private var height: Int,
        private var width: Int,
        arr: Array<Array<IntWrapper>>? = null
    ) : MongeMatrix<IntWrapper>() {

        var matrix = Array(height) { i -> Array(width) { IntWrapper(Int.MAX_VALUE) } }

        init {
            if (arr != null) matrix = arr
        }


        override fun times(mongeMatrix: Matrix<IntWrapper>): Matrix<IntWrapper> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun plus(mongeMatrix: Matrix<IntWrapper>): Matrix<IntWrapper> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun get(i: Int, j: Int): IntWrapper = matrix[i][j]
        override fun set(i: Int, j: Int, elem: IntWrapper) {
            matrix[i][j] = elem
        }

        override fun height(): Int = height

        override fun width(): Int = width

    }

    private fun Array<Int>.toIntWrapperArray(): Array<IntWrapper> {
        return this.map { it -> IntWrapper(it) }.toTypedArray()
    }

    private fun Array<IntWrapper>.min(): IntWrapper {
        if (this.size == 0) throw ArrayIndexOutOfBoundsException("")
        var minInt = this.first()
        for (e in this) {
            if (e.number < minInt.number) minInt = e
        }
        return minInt
    }

    val row1 = arrayOf(25, 21, 13, 10, 20, 13, 19, 35, 37, 41, 58, 66, 82, 99, 124, 133, 156, 178).toIntWrapperArray()
    val row2 = arrayOf(42, 35, 26, 20, 29, 21, 25, 37, 36, 39, 56, 64, 76, 91, 116, 125, 146, 164).toIntWrapperArray()
    val row3 = arrayOf(57, 48, 35, 28, 33, 24, 28, 40, 37, 37, 54, 61, 72, 83, 107, 113, 131, 146).toIntWrapperArray()
    val row4 = arrayOf(78, 65, 51, 42, 44, 35, 38, 48, 42, 42, 55, 61, 70, 80, 100, 106, 120, 135).toIntWrapperArray()
    val row5 = arrayOf(90, 76, 58, 48, 49, 39, 42, 48, 39, 35, 47, 51, 56, 63, 80, 86, 97, 110).toIntWrapperArray()
    val row6 = arrayOf(103, 85, 67, 56, 55, 44, 44, 49, 39, 33, 41, 44, 49, 56, 71, 75, 84, 96).toIntWrapperArray()
    val row7 = arrayOf(123, 105, 86, 75, 73, 59, 57, 62, 51, 44, 50, 52, 55, 59, 72, 74, 80, 92).toIntWrapperArray()
    val row8 = arrayOf(142, 123, 100, 86, 82, 65, 61, 62, 50, 43, 47, 45, 46, 46, 58, 59, 65, 73).toIntWrapperArray()
    val row9 = arrayOf(151, 130, 104, 88, 80, 59, 52, 49, 37, 29, 29, 24, 23, 20, 28, 25, 31, 39).toIntWrapperArray()


    @Test
    fun smawk9() {
        val c = DummyMongeMatrix(9, 18, arrayOf(row1, row2, row3, row4, row5, row6, row7, row8, row9))
        c.rowMinima().forEachIndexed { index, col -> assertEquals(c.matrix[index].min(), c.get(index, col)) }
    }

    @Test
    fun smawk1() {
        val c = DummyMongeMatrix(1, 18, arrayOf(row1))
        c.rowMinima().forEachIndexed { index, col -> assertEquals(c.matrix[index].min(), c.get(index, col)) }
    }

    @Test
    fun smawk4() {
        val c = DummyMongeMatrix(4, 18, arrayOf(row1, row3, row4, row5))
        c.rowMinima().forEachIndexed { index, col -> assertEquals(c.matrix[index].min(), c.get(index, col)) }
    }

    @Test
    fun smawkRowDominated() {
        val reduceRow1 = row1.slice(IntRange(0, 3)).toTypedArray()
        val reduceRow2 = row2.slice(IntRange(0, 3)).toTypedArray()
        val reduceRow3 = row3.slice(IntRange(0, 3)).toTypedArray()
        val reduceRow4 = row4.slice(IntRange(0, 3)).toTypedArray()
        val reduceRow5 = row5.slice(IntRange(0, 3)).toTypedArray()
        val c = DummyMongeMatrix(5, 4, arrayOf(reduceRow1, reduceRow2, reduceRow3, reduceRow4, reduceRow5))
        c.rowMinima().forEachIndexed { index, col -> assertEquals(c.matrix[index].min(), c.get(index, col)) }
    }


}