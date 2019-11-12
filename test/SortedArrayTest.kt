import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class SortedArrayTest {

    @Test
    fun countElementsBetween() {
        val runs = 100
        val size = 1000
        val qSize = 1000
        for (i in 0 until runs) {
            val random1 = Random(i)
            val random2 = Random(runs - 1)
            val arr = SortedArray(generateSequence { random1.nextInt() }.take(size).toList().sorted())
            val query = generateSequence {
                var a = random2.nextInt()
                var b = random2.nextInt()
                if (a > b) {
                    val tmp = a
                    a = b
                    b = tmp
                }
                IntervalQuery(a, b)
            }.take(qSize).forEach { q ->
                val expected = arr.sortedPointsByY.filter { it -> it >= q.leftInclusive && it <= q.rightInclusive }.size
                assertEquals(expected, arr.countElementsBetween(q))
            }
        }
    }
}