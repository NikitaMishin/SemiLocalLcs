import CountingQuery.Companion.dominanceMatrix
import CountingQuery.Companion.topRightSummator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import java.lang.Integer.min
import kotlin.random.Random

internal class SemiLocalLcsKtTest {
    val bookP = listOf<Position2D<Int>>(
        Position2D(0, 4),
        Position2D(1, 3),
        Position2D(2, 10),//ok
        Position2D(3, 2),//ok
        Position2D(4, 11),//ok
        Position2D(5, 15),//ok
        Position2D(6, 6),//ok
        Position2D(7, 9),//ok
        Position2D(8, 19),//ok
        Position2D(9, 13),//ok
        Position2D(10, 12),//ok
        Position2D(11, 1),
        Position2D(12, 0),
        Position2D(13, 17),
        Position2D(14, 18),
        Position2D(15, 7),
        Position2D(16, 16),
        Position2D(17, 5),
        Position2D(18, 14),
        Position2D(19, 8)
    )
    val bookQ = listOf(
        Position2D(0, 4),
        Position2D(1, 6),
        Position2D(2, 10),
        Position2D(3, 9),
        Position2D(4, 0),
        Position2D(5, 12),
        Position2D(6, 2),
        Position2D(7, 3),
        Position2D(8, 14),//ok
        Position2D(9, 18),//ok
        Position2D(10, 5),//ok
        Position2D(11, 11),
        Position2D(12, 1),
        Position2D(13, 15),
        Position2D(14, 17),
        Position2D(15, 13),
        Position2D(16, 7),
        Position2D(17, 8),
        Position2D(18, 16),
        Position2D(19, 19)
    )
    val bookR = listOf(
        Position2D(0, 10),
        Position2D(1, 9),
        Position2D(2, 18),
        Position2D(3, 6),
        Position2D(4, 14),
        Position2D(5, 17),
        Position2D(6, 12),
        Position2D(7, 11),
        Position2D(8, 19),//ok
        Position2D(9, 15),//ok
        Position2D(10, 5),//ok
        Position2D(11, 4),
        Position2D(12, 0),
        Position2D(13, 13),
        Position2D(14, 16),
        Position2D(15, 3),
        Position2D(16, 8),
        Position2D(17, 2),
        Position2D(18, 7),
        Position2D(19, 1)
    )




//    @Test
//    fun steadyAddntRandomTest() {
//        //also bad for non square
//        val A = listOf(
////            Position2D(0,2),
//            Position2D(0, 1)
////            Position2D(1,0)
////            Position2D(3,3)
//        )
//
//        val B = listOf(
//            Position2D(0, 1),
//            Position2D(1, 0),
//            Position2D(2, 2)
////            Position2D(3,2)
//        )
//
//        val a = PermutationMatrixTwoLists(A, 2, 9)
//        val b = PermutationMatrixTwoLists(B, 9, 22)
//        val naiveRes = naiveMultiplicationBraids(a, b)
//        val res = steadyAntWrapper(a, b)
//        res.print()
//        assertTrue(res.IsEquals(naiveRes))
//
//        println()
//
//    }

    @Test
    fun debug() {
        val a = listOf<Position2D<Int>>(
            Position2D(0, 0),
            Position2D(1, 1),
            Position2D(2, 2)
//            Position2D(3, 3)
        )
        val b = listOf(
            Position2D(0, 1),
            Position2D(1, 0),
            Position2D(2, 2)
//            Position2D(3, 2)
        )
        val A = PermutationMatrixTwoLists(a, 3, 3)
        val B = PermutationMatrixTwoLists(b, 3, 3)

        assertTrue(naiveMultiplicationBraids(A, B).IsEquals(steadyAntWrapper(A, B)))


        println()
        println("devug")

        naiveMultiplicationBraids(A, B).print()
    }


    //uncomment to see
    @Test
    fun steadyAntRandomTest() {

        val widthsQ = 20
        val widths = 20
        val heightsP = 25



        for (heightP in 1 until heightsP) {
            for (widtdP in 1 until widths) {
                for (widthQ in 1 until widthsQ)
                    for (nonZeroesP in 1..Math.min(heightP, widthQ)) {
                        for (nonzeroesQ in 1..Math.min(widtdP, widthQ)) {
                            val P = AbstractPermutationMatrix.generatePermutationMatrix(heightP, widtdP, nonZeroesP,0)
                            val Q = AbstractPermutationMatrix.generatePermutationMatrix(widtdP, widthQ, nonzeroesQ,1)
                            val naiveRes = naiveMultiplicationBraids(P, Q)
                            val res = steadyAntWrapper(P, Q)
                            if(!res.IsEquals(naiveRes)){
                                println("debug ")
                                println("Expected")
                                naiveRes.print()
                                println("Actual")
                                res.print()
                                assertTrue(false)
                            }

                        }
                    }
            }
        }


    }

//    @Test
//    fun steadyAntRandomSquareProductPermutationTest() {
//        val tries = 10
//        val maxSize = 100
//        val random = Random(0)
//        for (n in 1..maxSize) {
//
//            for (i in 1..tries) {
//                val a = AbstractPermutationMatrix.generatePermutationMatrix(n, n, n, random.nextInt())
//                val b = AbstractPermutationMatrix.generatePermutationMatrix(n, n, n, random.nextInt())
//                val naiveRes = naiveMultiplicationBraids(a, b)
//                val res = steadyAntWrapper(a, b)
//                assertTrue(res.IsEquals(naiveRes))
//
//            }
//
//        }
//        // strange speed when increase size
//    }


//    @Test
//    fun steadyAntBookExample() {
//
//
//        val matrixP = PermutationMatrixTwoLists(bookP,20,20)
//        val matrixQ = PermutationMatrixTwoLists(bookQ,20,20)
//        val matrixR = steadyAnt(matrixP,matrixQ)
//        val naiveRes = mutableListOf<Position2D<Int>>()
//
//        for(p in matrixR){
//            naiveRes.add(p)
//        }
//
//        assertEquals(bookR,naiveRes)
//
//
//    }
}