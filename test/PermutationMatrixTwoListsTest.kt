import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import utils.Position2D

internal class PermutationMatrixTwoListsTest {

    @Test
    operator fun iterator() {
        // matrix
        // 1,0,0 0
        // 0 0 1 0
        val points = mutableListOf(Position2D(0, 0), Position2D(1, 2))
        val matrix = PermutationMatrixTwoLists(points, 2, 4)
        val zeroMatrix = matrix.createZeroMatrix(width = 5, height = 9)

        var size = 0
        for (p in matrix) {
            size++
            assertTrue(p in points)
        }
        matrix.print()

        assertEquals(points.size, size)

        var emptyMatrixSize = 0
        for (x in zeroMatrix) {
            emptyMatrixSize++
        }
        println()
        zeroMatrix.print()

        zeroMatrix.print()

        assertEquals(0, emptyMatrixSize)
    }
}