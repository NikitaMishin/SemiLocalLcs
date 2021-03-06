package timeMeasure

import longestCommonSubsequence.IBraidMultiplication
import longestCommonSubsequence.MongeBraidMultiplication
import longestCommonSubsequence.NaiveBraidMultiplication
import longestCommonSubsequence.SteadyAntMultiplication
import utils.isEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.AbstractPermutationMatrix
import kotlin.math.min
import kotlin.system.measureTimeMillis

class BraidMultiplicationTimeTest {

    private fun multiplicationTimeMeasure(
        height: Int,
        width: Int,
        tries: Int,
        braidMultiplication: IBraidMultiplication
    ) {
        var accumulatedTime: Long = 0
        for (i in 0 until tries) {
            val a = AbstractPermutationMatrix.generateSquarePermutationMatrix(height, width,i)
            val b = AbstractPermutationMatrix.generateSquarePermutationMatrix(height, width, -i)
            accumulatedTime += measureTimeMillis {
                braidMultiplication.multiply(a, b)
            }
        }
        println("The average running time for multiplication is ${accumulatedTime / tries} millisecond")
    }

    private fun timeCorrectnessCheck(height: Int, width: Int, tries: Int,braidMultiplication: IBraidMultiplication = SteadyAntMultiplication()) {
        val naiveBraidMultiplication = NaiveBraidMultiplication()
        SteadyAntMultiplication()
        var time: Long = 0
        for (i in 0 until tries) {
            val a = AbstractPermutationMatrix.generatePermutationMatrix(height, width, min(height, width), i)
            val b = AbstractPermutationMatrix.generatePermutationMatrix(height, width, min(height, width), -i)
            val naiveRes = naiveBraidMultiplication.multiply(a, b)
            val start = System.currentTimeMillis()
            val braid = braidMultiplication.multiply(a, b)
            time +=  System.currentTimeMillis() - start
            Assertions.assertTrue(naiveRes.isEquals(braid))
        }
        println("The average running time for multiplication is ${time / tries} millisecond")
    }

//    @Test
//    fun multiplication100Naive() {
//        multiplicationTimeMeasure(100, 100, 200, NaiveBraidMultiplication())
//    }
//
//    @Test
//    fun multiplication100Monge() {
//        multiplicationTimeMeasure(100, 100, 200, MongeBraidMultiplication())
////        timeCorrectnessCheck(100,100,200,MongeBraidMultiplication())
//    }
//
//    @Test
//    fun multiplication100SteadyAnt() {
//        multiplicationTimeMeasure(100, 100, 200, SteadyAntMultiplication())
//        timeCorrectnessCheck(100, 100, 200)
//    }
//

    //78s
    @Test
    fun multiplication1000Naive() {
        multiplicationTimeMeasure(1000, 1000, 1, NaiveBraidMultiplication())
    }

    //64s
    @Test
    fun multiplication1000Monge() {
        multiplicationTimeMeasure(1000, 1000, 1, MongeBraidMultiplication())
    }

//
//    @Test
//    fun multiplication1000SteadyAnt() {
//        multiplicationTimeMeasure(1000, 1000, 2, SteadyAntMultiplication())
//       // timeCorrectnessCheck(1000, 1000, 2)
//    }
//
////    @Test
////    fun multiplication2000Naive() {
////        multiplicationTimeMeasure(2000, 2000, 1, longestCommonSubsequence.NaiveBraidMultiplication())
////    }
//
//    @Test
//    fun multiplication2000SteadyAnt() {
//        multiplicationTimeMeasure(2000, 2000, 1, SteadyAntMultiplication())
//       // timeCorrectnessCheck(2000, 2000, 1)
//    }
//
//    @Test
//    fun multiplication2000Monge() {
//        multiplicationTimeMeasure(2000, 2000, 1, MongeBraidMultiplication())
//        // timeCorrectnessCheck(2000, 2000, 1)
//    }
//
//
//    @Test
//    fun multiplication5000SteadyAnt() {
//        multiplicationTimeMeasure(5000, 5000, 1, SteadyAntMultiplication())
////        timeCorrectnessCheck(5000, 5000, 1)
//    }
//
//    @Test
//    fun multiplication5000Monge() {
//        multiplicationTimeMeasure(5000, 5000, 1, MongeBraidMultiplication())
////        timeCorrectnessCheck(5000, 5000, 1)
//    }

//
//    @Test
//    fun multiplication7000SteadyAnt() {
//        multiplicationTimeMeasure(7000, 7000, 1, longestCommonSubsequence.SteadyAntMultiplication())
//    }


}