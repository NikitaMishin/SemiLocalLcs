import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import utils.IntervalQuery
import utils.Position2D
import utils.RangeTree2D
import kotlin.random.Random

/**
 * Test class for testing 2D range Query for unique points i.e
 * no  exist p1 and p2 such p1.x == p2.x or p1.y == p2.y
 */
internal class RangeTree2DTest {

    private val presetPoints = listOf(
        Position2D(1, 2),
        Position2D(2, 3),
        Position2D(3, 5),
        Position2D(4, 7),
        Position2D(5, 4),
        Position2D(6, 1)
    )
    private val preSetTree = RangeTree2D(presetPoints)


    @Test
    fun emptySearchByXUnique() {
        assertEquals(
            0, preSetTree.ortoghonalQuery(
                IntervalQuery(-5, 0),
                IntervalQuery(1, 100)
            )
        )
        assertEquals(
            0, preSetTree.ortoghonalQuery(
                IntervalQuery(505, 0),
                IntervalQuery(1000, 100)
            )
        )
    }

    @Test
    fun emptySearchByYUnique() {
        assertEquals(
            0, preSetTree.ortoghonalQuery(
                IntervalQuery(1, 6),
                IntervalQuery(80, 100)
            )
        )
        assertEquals(
            0, preSetTree.ortoghonalQuery(
                IntervalQuery(1, 6),
                IntervalQuery(-80, -100)
            )
        )
    }

    @Test
    fun fullSearchUnique() {
        assertEquals(
            presetPoints.size, preSetTree.ortoghonalQuery(
                IntervalQuery(1, 6),
                IntervalQuery(0, 7)
            )
        )
        assertEquals(
            1, preSetTree.ortoghonalQuery(
                IntervalQuery(2, 4),
                IntervalQuery(1, 4)
            )
        )
    }


    @Test
    fun ortoghonalQueryUnique() {
        val testRun = 100
        val size = 10000
        val queriesAmoount = 5000

        for (i in 0 until testRun) {
            val random = Random(i)
            val random2 = Random(testRun - i)
            val x = (generateSequence { random.nextInt() }.distinct()).take(size)
            val y = (generateSequence { random2.nextInt() }.distinct()).take(size)
            val points = x.zip(y).map { Position2D(it.first, it.second) }.toList()
            val tree = RangeTree2D(points)
            val queries = generateSequence {
                var l1 = random.nextInt()
                var l2 = random2.nextInt()
                var l3 = random2.nextInt()
                var l4 = random.nextInt()
                if (l1 > l2) {
                    val tmp = l1
                    l1 = l2
                    l2 = tmp
                }
                if (l3 > l4) {
                    val tmp = l3
                    l3 = l4
                    l4 = tmp
                }

                Pair(
                    IntervalQuery(l1, l2),
                    IntervalQuery(l3, l4)
                )
            }.distinct().take(queriesAmoount)
            queries.forEach { q ->
                val res = tree.ortoghonalQuery(q.first, q.second)
                val expectedRes = points.filter { it.isInside(q.first, q.second) }.size
                assertEquals(res, expectedRes)
            }
        }
    }
}