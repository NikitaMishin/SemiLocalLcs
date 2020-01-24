import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import java.lang.Integer.min
import kotlin.random.Random

internal class SemiLocalLcsKtTest {

    fun generatePermutationMatrix(height: Int, width: Int, nonZerosCount: Int, seed: Int): AbstractPermutationMatrix {
        if (nonZerosCount > kotlin.math.min(height, width)) throw Exception("")
        val randomizer = Random(seed)
        var positions2D = mutableListOf<Position2D<Int>>()
        var remainingCount = nonZerosCount
        while (remainingCount > 0) {
            val randI = Math.abs(randomizer.nextInt()) % height
            val randJ = Math.abs(randomizer.nextInt()) % width
            if (positions2D.none { it.i == randI || it.j == randJ }) {
                positions2D.add(Position2D(randI, randJ))
                remainingCount--;
            }
        }
        return PermutationMatrixTwoLists(positions2D, height, width)
    }
    val topRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
        { pos, row, col -> pos.i >= row && pos.j < col }

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

    private fun naiveMultiplicationBraids(a: AbstractPermutationMatrix, b: AbstractPermutationMatrix) {
        val aDominance = dominanceMatrix(a, topRightSummator)
        val bDominance = dominanceMatrix(b, topRightSummator)
        val cDominance: Array<Array<Int>> = Array(a.height() + 1) { Array(b.width() + 1) { 0 } }
        a.print()
        println()
        b.print()
        println()

//        for(i in 0 until aDominance.size){
//            for (j in  0 until aDominance[0].size){
//                print("${aDominance[i][j]} ")
//            }
//            println();
//        }
//        println()
//        for(i in 0 until bDominance.size){
//            for (j in  0 until bDominance[0].size){
//                print("${bDominance[i][j]} ")
//            }
//            println();
//        }
//        println()
        for (i in 0 until a.height() + 1) {
            for (k in 0 until b.width() + 1) {
                var tmp = Int.MAX_VALUE
                for (j in 0 until a.width()+1) {
                    tmp = min(aDominance[i][j]+bDominance[j][k],tmp)

                }
                cDominance[i][k] = tmp
            }
        }

        val c =a.createZeroMatrix(a.height(),b.width())


        for (i in 0 until  a.height()){
            for(j in 0 until  b.width()){
                val v = cDominance[i][j+1] + cDominance[i+1][j]  - cDominance[i][j] - cDominance[i+1][j+1]
                c[i,j] = v == 1
                println(c[i,j])
            }
        }
        //TODO fix problem with stragne behaviour of for


//        println("matrix")
        c.print()



    }

    @Test
    fun steadyAnt() {

        naiveMultiplicationBraids(generatePermutationMatrix(2,5,2,0),generatePermutationMatrix(5,4,4,1))
    }
}