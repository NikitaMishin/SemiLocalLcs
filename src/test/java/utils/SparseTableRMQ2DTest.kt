package utils

import duplicateDetection.ApproximateMatchingViaRangeQuery
import longestCommonSubsequence.ReducingKernelEvaluation
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import sequenceAlignment.ImplicitSemiLocalProvider
import sequenceAlignment.ImplicitSemiLocalSA
import kotlin.random.Random

internal class SparseTableRMQ2DTest {

    val random = Random(1)

    @Test
    fun query() {

        for (tries in 0 until 1000) {

            val a = random.nextInt(2, 100)
            val b = random.nextInt(2, 100)
            val matrix = Array(a) { DoubleArray(b) { random.nextDouble(1.0, 1000.0) } }
            val sp = SparseTableRMQ2D({ i, j -> matrix[i][j] }, a, b)
            val spNaive = NaiveRMQ2D({ i, j -> matrix[i][j] }, a, b)

            for (q in 0 until 25000) {

                val x1 = random.nextInt(0, a-1)
                val x2 = random.nextInt(x1+1, a)

                val y1 = random.nextInt(0, b-1)
                val y2 = random.nextInt(y1+1, b)
                assertEquals(spNaive.query(x1, x2, y1, y2), sp.query(x1, x2, y1, y2))
            }
        }
    }

    @Test
    fun tmp() {
        val match = Fraction(1, 1)
        val mismatch = Fraction(-2, 1)
        val gap = Fraction(-1, 1)
        val scheme = FixedScoringScheme(match, mismatch, gap)
        val f = ApproximateMatchingViaRangeQuery<Char>(ImplicitSemiLocalProvider(ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists }, scheme)), scheme, 0.4)
        val fragments = mutableListOf(
                "arbba spartak".toList(),
                "abba mouse noise moise doiche cruchec".toList(),
                "abbadur".toList(),
                "cruche spartak jvnirjnv".toList()
        )
        f.find(fragments[0],fragments[3]).forEach {
            println(it)
        }

    }
}