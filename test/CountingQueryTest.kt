import AbstractPermutationMatrix.Companion.generatePermutationMatrix
import CountingQuery.Companion.bottomRightSummator
import CountingQuery.Companion.dominanceMatrix
import CountingQuery.Companion.topLeftSummator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Math.abs
import java.lang.Math.min
import java.util.zip.CheckedOutputStream
import kotlin.random.Random

internal class CountingQueryTest {

    val positions = listOf(
        Position2D(0, 0), Position2D(1, 1), Position2D(2, 4), Position2D(3, 2), Position2D(4, 3)
    )
    val countingQuery = CountingQuery()






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

    fun printMat(matrix:Array<Array<Int>>){
        for(i in matrix.indices){
            for (j in matrix[0].indices)
            {
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
//            val f = PermutationMatrixTwoLists(listOf(Position2D(0,1)),2,2)
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
}