package utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class SparseTableRMQ2DTest {

    val random = Random(1)

    @Test
    fun query() {

        for (tries in 0 until 1000) {

            val a = random.nextInt(1, 100)
            val b = random.nextInt(1, 100)
            val matrix = Array(a) { DoubleArray(b) { random.nextDouble(1.0, 1000.0) } }
            val sp = SparseTableRMQ2D({i,j->matrix[i][j]}, a, b)
            val spNaive = NaiveRMQ2D(matrix, a, b)

            for (q in 0 until 25000) {

                val x1 = random.nextInt(0, a)
                val x2 = random.nextInt(x1, a)

                val y1 = random.nextInt(0, b)
                val y2 = random.nextInt(y1, b)
                assertEquals(spNaive.query(x1, x2, y1, y2), sp.query(x1, x2, y1, y2))
            }
        }
    }
}