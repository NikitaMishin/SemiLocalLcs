package timeMeasure

import longestCommonSubsequence.*
import utils.PermutationMatrixTwoLists
import org.junit.jupiter.api.Test
import sequenceAlignment.ExplicitSemiLocalSA
import sequenceAlignment.ISemiLocalCombined
import utils.LCSScoringScheme
import utils.isEquals
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue


class KernelEvaluationTimeTest {

    private fun checkSemiLocal(a: ISemiLocalCombined<Char>, b: ISemiLocalCombined<Char>, height: Int, width: Int) {
        for (i in 0 until height) {
            for (j in 0 until width) {
                assertTrue { a.getAtPosition(i, j).isEquals( b.getAtPosition(i, j)) }
            }
        }
    }

    private fun getSemiLocalRecursive(A: String, B: String) =
        ImplicitSemiLocalLCS(
            A.toList(),
            B.toList(),
            RecursiveKernelEvaluation {
                PermutationMatrixTwoLists(
                    mutableListOf(), 0, 0
                )
            })

    private fun getSemiLocalNaive(A:String,B: String) =
        NaiveSemiLocalLCS(A.toList(),B.toList())

    private fun getSemiLocalExplicit(A: String,B: String) =
        ExplicitSemiLocalSA(A.toList(),B.toList(),LCSScoringScheme(),ExplicitKernelEvaluation(LCSScoringScheme()))

    private fun getSemiLocalReducing(A: String, B: String) =
        ImplicitSemiLocalLCS(
            A.toList(),
            B.toList(),
            ReducingKernelEvaluation {
                PermutationMatrixTwoLists(
                    mutableListOf(), 0, 0
                )
            })

    private fun getNaiveSemiLocalLCS(A: String, B: String) =
        NaiveSemiLocalLCS(A.toList(), B.toList())


    private fun timeTest(sizeA: Int, sizeB: Int, tries: Int, evaluator: (A: String, B: String) -> ISemiLocalCombined<Char>, withCheckingCorrectness:Boolean) {
        val random = Random(0)
        var accumulatedTime: Long = 0
        for (i in 0 until tries) {
            println("Processed $i from $tries ")
            val a = ISemiLocalLCS.getRandomString(tries, sizeA, ISemiLocalLCS.alphabet, random).toString()
            val b = ISemiLocalLCS.getRandomString(tries, sizeB, ISemiLocalLCS.alphabet, random).toString()

            val cur = measureTimeMillis {
                evaluator(a, b)
            }

            println(cur)
            accumulatedTime += cur

            if(withCheckingCorrectness) {
                val naiveRes = getNaiveSemiLocalLCS(a, b)
                val implicitRes = evaluator(a, b)
                checkSemiLocal(naiveRes, implicitRes, a.length + b.length + 1, a.length + b.length + 1)
            }
        }
        println("The average running time for multiplication is ${accumulatedTime / tries} millisecond")
    }

    @Test
    fun semiLocal100x100Recursive() {
        timeTest(100, 100, 5, ::getSemiLocalRecursive,true)
    }

    @Test
    fun semiLocal100x100Reducing() {
        timeTest(100, 100, 5, ::getSemiLocalReducing,true)
    }

    @Test
    fun semiLocal100x100Naive() {
        timeTest(100, 100, 2, ::getSemiLocalNaive,false)
    }



    @Test
    fun semiLocal100x100RExplicit() {
        timeTest(100, 100, 5, ::getSemiLocalExplicit,true)
    }





    @Test
    fun semiLocal500x500Recursive() {
        timeTest(500, 500, 2, ::getSemiLocalRecursive,false)
    }

    @Test
    fun semiLocal500x500Reducing() {
        timeTest(500, 500, 2, ::getSemiLocalReducing,false)
    }

    @Test
    fun semiLocal500x500RExplicit() {
        timeTest(500, 500, 2, ::getSemiLocalExplicit,false)
    }



    @Test
    fun semiLocal1000x1000Recursive() {
        timeTest(1000, 1000, 1, ::getSemiLocalRecursive,false)
    }

    @Test
    fun semiLocal1000x1000Reducing() {
        timeTest(1000, 1000, 1, ::getSemiLocalReducing,false)
    }

    @Test
    fun semiLocal1000x1000RExplicit() {
        timeTest(1000, 1000, 1, ::getSemiLocalExplicit,false)
    }


//
//
//    @Test
//    fun semiLocal2000x2000Recursive() {
//        timeTest(2000, 2000, 1, ::getSemiLocalRecursive,false)
//    }
//
//    @Test
//    fun semiLocal2000x2000Reducing() {
//        timeTest(2000, 2000, 1, ::getSemiLocalReducing,false)
//    }
//
//    @Test
//    fun semiLocal2000x2000RExplicit() {
//        timeTest(2000, 2000, 1, ::getSemiLocalExplicit,false)
//    }
//
//
//    @Test
//    fun semiLocal10000x10000Explicit() {
//        timeTest(10000, 10000, 1, ::getSemiLocalExplicit,false)
//    }
//
//
//
//    @Test
//    fun semiLocal10000x10000Reducing() {
//        timeTest(10000, 10000, 1, ::getSemiLocalReducing,false)
//    }


}