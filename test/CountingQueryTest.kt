import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import java.lang.Math.abs
import java.lang.Math.min
import java.sql.Time
import kotlin.concurrent.timer
import kotlin.random.Random

internal class CountingQueryTest {

    val positions = listOf(
        Position2D(0, 0), Position2D(1, 1), Position2D(2, 4), Position2D(3, 2), Position2D(4, 3)
    )
    val countingQuery = CountingQuery()

    val topLeftSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
        { pos, row, col -> pos.i < row && pos.j < col }
    val bottomRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
        { pos, row, col -> pos.i >= row && pos.j >= col }

    val topRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
        { pos, row, col -> pos.i >= row && pos.j < col }


    fun generatePermutationMatrix(height: Int, width: Int, nonZerosCount: Int, seed: Int): AbstractPermutationMatrix {
        if (nonZerosCount > kotlin.math.min(height, width)) throw Exception("")
        val randomizer = Random(seed)
        var positions2D = mutableListOf<Position2D<Int>>()
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

    private fun dominanceMatrix(matrix: AbstractPermutationMatrix, func: (Position2D<Int>, Int, Int) -> Boolean)
            : Array<Array<Int>> {
        //half sizes for permutation matrix
        // and for croos is integer
        val dominanceMatrix = Array(matrix.height() + 1) { Array(matrix.width() + 1) { 0 } }
        for (row in dominanceMatrix.indices) {
            for (col in dominanceMatrix[0].indices) {
                for (pos in matrix) {
                    if (func(pos, row, col)) dominanceMatrix[row][col]++
                }
            }
        }
        return dominanceMatrix
    }


    fun dominanceSumTest(
        summatorType: (Position2D<Int>, Int, Int) -> Boolean,
        evaluator: (Int, Int, Array<Array<Int>>, AbstractPermutationMatrix) -> Unit
    ) {

        val randomizer = Random(0)
        val queriesCount = 1000
        val heights = 15
        val widths = 15


        for (height in 1 until heights) {
            for (width in 1 until widths) {
                for (nonZeroes in 1..min(height, width)) {
                    val genMatrix =
                        generatePermutationMatrix(height, width, nonZeroes, System.currentTimeMillis().toInt())

                    val dominanceMatrix = dominanceMatrix(genMatrix, summatorType)

                    for (q in 0 until queriesCount) {
                        val i = abs(randomizer.nextInt()) % dominanceMatrix.size
                        val j = abs(randomizer.nextInt()) % dominanceMatrix[0].size
                        evaluator(i, j, dominanceMatrix, genMatrix)
                    }
                }
            }
        }
    }

    @Test
    fun dominanceSumTopLeftUpMove() {
        dominanceSumTest(topLeftSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i == 0) i1++
            assertEquals(
                dominanceMatrix[i1 - 1][j],
                countingQuery.dominanceSumTopLeftUpMove(i1, j, dominanceMatrix[i1][j], genMatrix)
            )
        })
    }


    @Test
    fun dominanceSumTopLeftRightMove() {
        dominanceSumTest(topLeftSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == dominanceMatrix[0].size - 1) j1--
//            println(j1)
            assertEquals(
                dominanceMatrix[i][j1 + 1],
                countingQuery.dominanceSumTopLeftRightMove(i, j1, dominanceMatrix[i][j1], genMatrix)
            )
        })
    }


    @Test
    fun dominanceSumBottomRightUpMove() {
        dominanceSumTest(bottomRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i1 == 0) i1++
            assertEquals(
                dominanceMatrix[i1 - 1][j],
                countingQuery.dominanceSumBottomRightUpMove(i1, j, dominanceMatrix[i1][j], genMatrix)
            )
        })
    }

    @Test
    fun dominanceSumBottomRightRightMove() {
        dominanceSumTest(bottomRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == dominanceMatrix[0].size - 1) j1--
            assertEquals(
                dominanceMatrix[i][j1 + 1],
                countingQuery.dominanceSumBottomRightRightMove(i, j1, dominanceMatrix[i][j1], genMatrix)
            )
        })
    }
}