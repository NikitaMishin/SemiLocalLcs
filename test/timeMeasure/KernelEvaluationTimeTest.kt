package timeMeasure

import ISemiLocalLCS
import ImplicitSemiLocalLCS
import NaiveSemiLocalLCS
import PermutationMatrixTwoLists
import RecursiveKernelEvaluation
import ReducingKernelEvaluation
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue


class KernelEvaluationTimeTest {

    private fun checkSemiLocal(a: ISemiLocalLCS, b: ISemiLocalLCS, height: Int, width: Int) {
        for (i in 0 until height) {
            for (j in 0 until width) {
                assertTrue { a.getAtPosition(i, j) == b.getAtPosition(i, j) }
            }
        }
    }

    private fun getSemiLocalRecursive(A: String, B: String): ISemiLocalLCS =
        ImplicitSemiLocalLCS(A.toList(), B.toList(), RecursiveKernelEvaluation {
            PermutationMatrixTwoLists(
                mutableListOf(), 0, 0
            )
        })

    private fun getSemiLocalReducing(A: String, B: String): ISemiLocalLCS =
        ImplicitSemiLocalLCS(A.toList(), B.toList(), ReducingKernelEvaluation {
            PermutationMatrixTwoLists(
                mutableListOf(), 0, 0
            )
        })

    private fun getNaiveSemiLocalLCS(A: String, B: String): ISemiLocalLCS = NaiveSemiLocalLCS(A.toList(), B.toList())


    private fun timeTest(sizeA: Int, sizeB: Int, tries: Int, evaluator: (A: String, B: String) -> ISemiLocalLCS) {
        val random = Random(0)
        var accumulatedTime: Long = 0
        for (i in 0 until tries) {
            val a = ISemiLocalLCS.getRandomString(tries, sizeA, ISemiLocalLCS.alphabet, random).toString()
            val b = ISemiLocalLCS.getRandomString(tries, sizeB, ISemiLocalLCS.alphabet, random).toString()
            accumulatedTime += measureTimeMillis {
                evaluator(a, b)
            }
            val naiveRes = getNaiveSemiLocalLCS(a, b)
            val implicitRes = evaluator(a, b)
            checkSemiLocal(naiveRes, implicitRes, a.length + b.length, a.length + b.length)
        }
        println("The average running time for multiplication is ${accumulatedTime / tries} millisecond")
    }

    @Test
    fun semiLocal100x100Recursive() {
        timeTest(100, 100, 100, ::getSemiLocalRecursive)
    }

    @Test
    fun semiLocal100x100Reducing() {
        timeTest(100, 100, 100, ::getSemiLocalReducing)
    }


    @Test
    fun semiLocal500x500Recursive() {
        timeTest(500, 500, 5, ::getSemiLocalRecursive)
    }

    @Test
    fun semiLocal500x500Reducing() {
        timeTest(500, 500, 5, ::getSemiLocalReducing)
    }

    @Test
    fun semiLocal1000x1000Recursive() {
        timeTest(1000, 1000, 1, ::getSemiLocalRecursive)
    }

    @Test
    fun semiLocal1000x1000Reducing() {
        timeTest(1000, 1000, 1, ::getSemiLocalReducing)
    }


    @Test
    fun semiLocal2000x2000Recursive() {
        timeTest(2000, 2000, 1, ::getSemiLocalRecursive)
    }

    @Test
    fun semiLocal2000x2000Reducing() {
        timeTest(2000, 2000, 1, ::getSemiLocalReducing)
    }



}