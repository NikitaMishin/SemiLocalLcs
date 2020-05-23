package utils

import utils.AbstractPermutationMatrix.Companion.generatePermutationMatrix
import utils.CountingQueryLCS.Companion.bottomRightSummator
import utils.CountingQueryLCS.Companion.dominanceMatrix
import utils.CountingQueryLCS.Companion.topLeftSummator
import utils.CountingQueryLCS.Companion.topRightSummator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Math.abs
import java.lang.Math.min
import kotlin.random.Random

internal class CountingQueryTest {

    val positions = listOf(
        Position2D(0, 0),
        Position2D(1, 1),
        Position2D(2, 4),
        Position2D(3, 2),
        Position2D(4, 3)
    )
    val countingQuery = CountingQueryLCS()


    fun dominanceSumTest(
        summatorType: (Position2D<Int>, Int, Int) -> Boolean,
        evaluator: (Int, Int, Array<Array<Int>>, AbstractPermutationMatrix) -> Unit
    ) {

        val randomizer = Random(0)
        val queriesCount = 5000
        val heights = 25
        val widths = 25


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

    fun printMat(matrix: Array<Array<Int>>) {
        for (i in matrix.indices) {
            for (j in matrix[0].indices) {
                print("${matrix[i][j]}  ")

            }
            println()
        }
    }


    @Test
    fun dominanceSumTopLeftRightMove() {
        dominanceSumTest(topLeftSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == dominanceMatrix[0].size - 1) j1--

            assertEquals(
                dominanceMatrix[i][j1 + 1],
                countingQuery.dominanceSumTopLeftRightMove(i, j1, dominanceMatrix[i][j1], genMatrix)
            )
        })
    }


    @Test
    fun dominanceSumBottomRightUpMove() {
        dominanceSumTest(bottomRightSummator, { i, j, dominanceMatrix, genMatrix ->
            //            val f = utils.PermutationMatrixTwoLists(listOf(utils.Position2D(0,1)),2,2)
//            f.print()
//            val matrix = CountingQuery.dominanceMatrix(f,CountingQuery.bottomRightSummator)

//            for(i in matrix.indices){
//                for (j in matrix[0].indices)
//                {
//                    print("${matrix[i][j]}  ")
//
//                }
//                println()
//            }


            var i1 = i
            if (i1 == 0) i1++
            assertEquals(
                dominanceMatrix[i1 - 1][j],
                countingQuery.dominanceSumBottomRightUpMove(i1, j, dominanceMatrix[i1][j], genMatrix)
            )
        })

    }

    //
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

    @Test
    fun dominanceSumTopLeftLeftMove() {
        dominanceSumTest(topLeftSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == 0) j1++

            assertEquals(
                dominanceMatrix[i][j1 - 1],
                countingQuery.dominanceSumTopLeftLeftMove(i, j1, dominanceMatrix[i][j1], genMatrix)
            )
        })
    }

    @Test
    fun dominanceSumTopLeftDownMove() {
        dominanceSumTest(topLeftSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i == dominanceMatrix.size - 1) i1--

            assertEquals(
                dominanceMatrix[i1 + 1][j],
                countingQuery.dominanceSumTopLeftDownMove(i1, j, dominanceMatrix[i1][j], genMatrix)
            )
        })
    }


    @Test
    fun dominanceSumBottomRightLeftMove() {
        dominanceSumTest(bottomRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == 0) j1++



            assertEquals(
                dominanceMatrix[i][j1 - 1],
                countingQuery.dominanceSumBottomRightLeftMove(i, j1, dominanceMatrix[i][j1], genMatrix)
            )
        })
    }

    @Test
    fun dominanceSumBottomRightDownMove() {
        dominanceSumTest(bottomRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i == dominanceMatrix.size - 1) i1--
//            genMatrix.print()
//            println()
//
//            val matrix = CountingQuery.dominanceMatrix(genMatrix,CountingQuery.bottomRightSummator)
//
//            for(i in matrix.indices){
//                for (j in matrix[0].indices)
//                {
//                    print("${matrix[i][j]}  ")
//
//                }
//                println()
//            }
//            println("query=${i1},${j},sum=${dominanceMatrix[i1][j]}")

            assertEquals(
                dominanceMatrix[i1 + 1][j],
                countingQuery.dominanceSumBottomRightDownMove(i1, j, dominanceMatrix[i1][j], genMatrix)
            )
        })
    }


    @Test
    fun dominanceSumTopRightDownMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i == dominanceMatrix.size - 1) i1--
            assertEquals(
                dominanceMatrix[i1 + 1][j],
                countingQuery.dominanceSumTopRightDownMove(i1, j, dominanceMatrix[i1][j], genMatrix)
            )
        })
    }

    @Test
    fun dominanceSumTopRightLeftMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == 0) j1++



            assertEquals(
                dominanceMatrix[i][j1 - 1],
                countingQuery.dominanceSumTopRightLeftMove(i, j1, dominanceMatrix[i][j1], genMatrix)
            )
        })
    }

    @Test
    fun dominanceSumTopRightRightMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var j1 = j
            if (j == dominanceMatrix[0].size - 1) j1--
            assertEquals(
                dominanceMatrix[i][j1 + 1],
                countingQuery.dominanceSumTopRightRightMove(i, j1, dominanceMatrix[i][j1], genMatrix)
            )
        })
    }
    @Test
    fun dominanceSumTopRightUpMove() {
        dominanceSumTest(topRightSummator, { i, j, dominanceMatrix, genMatrix ->
            var i1 = i
            if (i == 0) i1++
            assertEquals(
                dominanceMatrix[i1 - 1][j],
                countingQuery.dominanceSumTopRightUpMove(i1, j, dominanceMatrix[i1][j], genMatrix)
            )
        })
    }
}