import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ExplicitMongeTest {
    fun MutableList<Int>.toWrapped(): MutableList<IntWrapper> {
        return this.map { IntWrapper(it) } as MutableList<IntWrapper>
    }

    val row1 = mutableListOf(25, 21, 13, 10, 20, 13, 19, 35, 37, 41, 58, 66, 82, 99, 124, 133, 156, 178).toWrapped()
    val row2 = mutableListOf(42, 35, 26, 20, 29, 21, 25, 37, 36, 39, 56, 64, 76, 91, 116, 125, 146, 164).toWrapped()
    val row3 = mutableListOf(57, 48, 35, 28, 33, 24, 28, 40, 37, 37, 54, 61, 72, 83, 107, 113, 131, 146).toWrapped()
    val row4 = mutableListOf(78, 65, 51, 42, 44, 35, 38, 48, 42, 42, 55, 61, 70, 80, 100, 106, 120, 135).toWrapped()
    val row5 = mutableListOf(90, 76, 58, 48, 49, 39, 42, 48, 39, 35, 47, 51, 56, 63, 80, 86, 97, 110).toWrapped()
    val row6 = mutableListOf(103, 85, 67, 56, 55, 44, 44, 49, 39, 33, 41, 44, 49, 56, 71, 75, 84, 96).toWrapped()
    val row7 = mutableListOf(123, 105, 86, 75, 73, 59, 57, 62, 51, 44, 50, 52, 55, 59, 72, 74, 80, 92).toWrapped()
    val row8 = mutableListOf(142, 123, 100, 86, 82, 65, 61, 62, 50, 43, 47, 45, 46, 46, 58, 59, 65, 73).toWrapped()
    val row9 = mutableListOf(151, 130, 104, 88, 80, 59, 52, 49, 37, 29, 29, 24, 23, 20, 28, 25, 31, 39).toWrapped()

    inline fun <T:ElemWrapper> checkerRoutine(m1: MutableList<MutableList<T>>, m2: MutableList<MutableList<T>> ) {
        val A = NaiveMonge(m1)
        val B = NaiveMonge(m2)
        val C = A * B
        val D = ExplicitMonge(m1)
        val E = ExplicitMonge(m2)
        val F = D * E
        assertEquals(C.matrix, F.matrix)
        println(A.isMongePropertySatisified() && B.isMongePropertySatisified())
        println(C.isMongePropertySatisified())
        C.printMatrix()
    }

    @Test
    fun times4X9With9X18() {
        val matrix9X18 = mutableListOf(row1, row2, row3, row4, row5, row6, row7, row8, row9)
        val matrix4x9 = mutableListOf(row1.subList(0, 9), row2.subList(0, 9), row3.subList(0, 9), row4.subList(0, 9))
        checkerRoutine(matrix4x9, matrix9X18)
    }

    @Test
    fun times1X1With1X1() {
        val matrix1X1 = mutableListOf(mutableListOf(IntWrapper(1)))
        val matrix1X12 = mutableListOf(mutableListOf(IntWrapper(3)))
        checkerRoutine(matrix1X1, matrix1X12)
    }

    @Test
    fun times2X2With2X2() {
        val m1 = mutableListOf(
            mutableListOf(IntWrapper(1), IntWrapper(3)),
            mutableListOf(IntWrapper(0), IntWrapper(2))
        )
        val m2 = mutableListOf(
            mutableListOf(IntWrapper(7), IntWrapper(12)),
            mutableListOf(IntWrapper(4), IntWrapper(8))
        )
        checkerRoutine(m1, m2)
    }


    @Test
    fun times3X2With2X2() {
        val m1 = mutableListOf(
            mutableListOf(IntWrapper(1), IntWrapper(7)),
            mutableListOf(IntWrapper(0), IntWrapper(2)),
            mutableListOf(IntWrapper(0), IntWrapper(2))
        )
        val m2 = mutableListOf(
            mutableListOf(IntWrapper(7), IntWrapper(12)),
            mutableListOf(IntWrapper(4), IntWrapper(8))
        )
        checkerRoutine(m1, m2)
    }

    @Test
    fun times5X3With3X6() {
        val m1 = mutableListOf(
            row2.subList(2, 5),
            row4.subList(2, 5),
            row5.subList(2, 5),
            row6.subList(2, 5),
            row8.subList(2, 5)
        )
        val m2 = mutableListOf(row5.subList(0, 6), row7.subList(0, 6), row9.subList(0, 6))
        checkerRoutine(m1, m2)
    }
    @Test
    fun times5X5XWith5X5() {
        val m1 = mutableListOf(
            mutableListOf(0,1,2).toWrapped(),
            mutableListOf(0,1,2).toWrapped(),
            mutableListOf(0,0,1).toWrapped(),
            mutableListOf(0,0,1).toWrapped(),
            mutableListOf(0,0,0).toWrapped()
        )
        val m2 = mutableListOf(
            mutableListOf(0,0,1,2,2).toWrapped(),
            mutableListOf(0,0,0,1,1).toWrapped(),
            mutableListOf(0,0,0,0,0).toWrapped()
//            mutableListOf(0,0,0,0,1).toWrapped(),
//            mutableListOf(0,0,0,0,0).toWrapped()
        )

        checkerRoutine(m1, m2)
    }

    @Test
    fun times5X6XWith6X4() {
        val m1 = mutableListOf(
            mutableListOf(0,1,2,3,3,4).toWrapped(),
            mutableListOf(0,0,1,2,2,3).toWrapped(),
            mutableListOf(0,0,1,1,1,2).toWrapped(),
            mutableListOf(0,0,1,1,1,1).toWrapped(),
            mutableListOf(0,0,0,0,0,0).toWrapped()
        )
        val m2 = mutableListOf(
            mutableListOf(0,1,2,3).toWrapped(),
            mutableListOf(0,1,2,3).toWrapped(),
            mutableListOf(0,0,1,2).toWrapped(),
            mutableListOf(0,0,1,2).toWrapped(),
            mutableListOf(0,0,1,1).toWrapped(),
            mutableListOf(0,0,0,0).toWrapped()
//            mutableListOf(0,0,0,0,1).toWrapped(),
//            mutableListOf(0,0,0,0,0).toWrapped()
        )
        println('H')
        checkerRoutine(m1, m2)
    }

    @Test
    fun times5X3XWith3X4() {
        val m1 = mutableListOf(
            mutableListOf(0,1,2,3).toWrapped(),
            mutableListOf(0,0,1,2).toWrapped(),
            mutableListOf(0,0,1,1).toWrapped(),
            mutableListOf(0,0,0,0).toWrapped()
//            mutableListOf(0,0,0).toWrapped()
        )
        val m2 = mutableListOf(
            mutableListOf(0,1,2).toWrapped(),
            mutableListOf(0,1,2).toWrapped(),
            mutableListOf(0,0,1).toWrapped(),
            mutableListOf(0,0,0).toWrapped()
//            mutableListOf(0,0,1,2).toWrapped(),
//            mutableListOf(0,0,1,1).toWrapped(),
//            mutableListOf(0,0,0,0).toWrapped()
//            mutableListOf(0,0,0,0,1).toWrapped(),
//            mutableListOf(0,0,0,0,0).toWrapped()
        )
        println('A')
        checkerRoutine(m1, m2)
    }



}