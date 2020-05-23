package utils

import utils.CountingQuerySA.Companion.dominanceMatrix
import utils.CountingQuerySA.Companion.topRightSummator
import utils.IStochasticMatrix.Companion.generateStochasticMatrix
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class CountingQuerySATest {

    val countingQuery = CountingQuerySA()

    fun dominanceSumTest(
        summatorType: (Position2D<Int>, Int, Int) -> Boolean,
        evaluator: (Int, Int, Array<Array<Double>>, IStochasticMatrix) -> Unit
    ) {

        val randomizer = Random(0)
        val queriesCount = 5000
        val heights = 25
        val widths = 25


        for (height in 1 until heights) {
            for (width in 1 until widths) {
                for (nonZeroes in 1..Math.min(height, width)) {
                    val genMatrix =
                        generateStochasticMatrix(height, width, nonZeroes, System.currentTimeMillis().toInt())

                    val dominanceMatrix = dominanceMatrix(genMatrix, summatorType)

                    for (q in 0 until queriesCount) {
                        val i = Math.abs(randomizer.nextInt()) % dominanceMatrix.size
                        val j = Math.abs(randomizer.nextInt()) % dominanceMatrix[0].size
                        evaluator(i, j, dominanceMatrix, genMatrix)
                    }
                }
            }
        }
    }

    @Test
    fun dominanceSumTopRightDownMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i == dominanceMatrix.size - 1) i1--
            assertTrue(
                dominanceMatrix[i1 + 1][j].isEquals(
                    countingQuery.dominanceSumTopRightDownMove(
                        i1,
                        j,
                        dominanceMatrix[i1][j],
                        genMatrix
                    )
                )
            )
        })
    }

    @Test
    fun dominanceSumTopRightLeftMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == 0) j1++
            assertTrue(
                dominanceMatrix[i][j1 - 1].isEquals(
                    countingQuery.dominanceSumTopRightLeftMove(
                        i,
                        j1,
                        dominanceMatrix[i][j1],
                        genMatrix
                    )
                )
            )
        })
    }

    @Test
    fun dominanceSumTopRightRightMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == dominanceMatrix[0].size - 1) j1--
            assertTrue(
                dominanceMatrix[i][j1 + 1].isEquals(
                    countingQuery.dominanceSumTopRightRightMove(
                        i,
                        j1,
                        dominanceMatrix[i][j1],
                        genMatrix
                    )
                )
            )
        })
    }

    @Test
    fun dominanceSumTopRightUpMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i == 0) i1++
            assertTrue(
                dominanceMatrix[i1 - 1][j].isEquals(
                    countingQuery.dominanceSumTopRightUpMove(
                        i1,
                        j,
                        dominanceMatrix[i1][j],
                        genMatrix
                    )
                )
            )
        })
    }
}