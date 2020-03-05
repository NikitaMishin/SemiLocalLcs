package longestCommonSubsequence

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class NaiveSemiLocalLCSTest : SemiLocalLCSBaseTester(Random(0)) {

    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): ISemiLocalLCS =
        NaiveSemiLocalLCS(A, B)

    @Test
    fun bookExampleTest() {
        val knownMatrix = arrayListOf(
            arrayListOf(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(5, 6, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(4, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(3, 4, 5, 5, 5, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(2, 3, 4, 4, 4, 5, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(1, 2, 3, 3, 4, 5, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(0, 1, 2, 3, 4, 5, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
            arrayListOf(-1, 0, 1, 2, 3, 4, 5, 5, 6, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8),
            arrayListOf(-2, -1, 0, 1, 2, 3, 4, 4, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8),
            arrayListOf(-3, -2, -1, 0, 1, 2, 3, 3, 4, 5, 5, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8),
            arrayListOf(-4, -3, -2, -1, 0, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 8),
            arrayListOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 8),
            arrayListOf(-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 3, 4, 4, 5, 5, 6, 7, 7, 8, 8, 8, 8),
            arrayListOf(-7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 8, 8, 8),
            arrayListOf(-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 3, 4, 4, 5, 6, 7, 8, 8, 8, 8),
            arrayListOf(-9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 5, 6, 7, 8, 8, 8, 8),
            arrayListOf(-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 3, 4, 5, 6, 7, 7, 7, 8),
            arrayListOf(-11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 2, 3, 4, 5, 6, 7, 7, 8),
            arrayListOf(-12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 7, 8),
            arrayListOf(-13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8)
        )

        val semiLocalSolution =
            NaiveSemiLocalLCS("baabcbca".toList(), "baabcabcabaca".toList())
        for (i in 0 until knownMatrix.size) {
            for (j in 0 until knownMatrix[0].size)
                assertEquals(knownMatrix[i][j], semiLocalSolution.semiLocalLCSMatrix[i][j])
        }

    }



}