package sequenceAlignment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Math.abs
import kotlin.random.Random


internal abstract class SemiLocalSABaseTester(val random: Random) {
    val epsilon = 1E-4

    fun compareDouble(a: Double, b: Double): Boolean = kotlin.math.abs(a - b) < epsilon

    fun <T : Comparable<T>> prefixAlignment(a: List<T>, b: List<T>, scoringScheme: ScoringScheme): Double {
        val scoreMatrix = Array(a.size + 1) { Array(b.size + 1) { 0.0 } }
        for (i in 1 until scoreMatrix.size) {
            for (j in 1 until scoreMatrix[0].size) {
                scoreMatrix[i][j] = java.lang.Double.max(
                    scoreMatrix[i - 1][j - 1] + (if (a[i - 1] == b[j - 1]) scoringScheme.matchScore
                    else scoringScheme.mismatchScore),
                    java.lang.Double.max(
                        scoreMatrix[i - 1][j] + scoringScheme.gapScore,
                        scoreMatrix[i][j - 1] + scoringScheme.gapScore
                    )
                )
            }
        }

        return  scoreMatrix[a.size][b.size]// - (a.size+b.size)*scoringScheme.gapScore) / (scoringScheme.matchScore-2*scoringScheme.mismatchScore)
    }

    abstract fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): ISemiLocalSA

    val alphabet = arrayListOf(
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z'
    )


    private fun <E : Comparable<E>> checkStringSubstringProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (j in 0..B.size) {
            for (i in 0 until j) {
                val subList = B.subList(i, j)
//                println(solution.getScoringScheme())
//                println(solution.getScoringScheme().normalizedMismatch)
//                println(solution.getScoringScheme().originalScoreFunc(solution.stringSubstringSA(i, j),subList.size,A.size))
//                print("${A},${subList}")
                if (!compareDouble(
                        prefixAlignment(A, subList, solution.getScoringScheme()),
                        solution.stringSubstringSA(i, j)
                    )
                ) {
                    assertEquals(
                        prefixAlignment(A, subList, solution.getScoringScheme()),
                        solution.stringSubstringSA(i, j)
                    )
                }

            }
        }
    }

    private fun <E : Comparable<E>> checkSubstringStringProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (j in 0..A.size) {
            for (i in 0 until j) {
                val subList = A.subList(i, j)
                if (!compareDouble(
                        prefixAlignment(subList, B, solution.getScoringScheme()),
                        solution.substringStringSA(i, j)
                    )
                )
                    assertEquals(
                        prefixAlignment(subList, B, solution.getScoringScheme()),
                        solution.substringStringSA(i, j)
                    )
            }
        }
    }

    private fun <E : Comparable<E>> checkPrefixSuffixProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (i in A.indices) {
            for (j in 0..B.size) {
                val subListA = A.subList(i, A.size)
                val subListB = B.subList(0, j)


                if (!compareDouble(
                        prefixAlignment(subListA, subListB, solution.getScoringScheme()),
                        solution.prefixSuffixSA(i, j)
                    )
                )
                    assertEquals(
                        prefixAlignment(subListA, subListB, solution.getScoringScheme()),
                        solution.prefixSuffixSA(i, j)
                    )
            }
        }
    }

    private fun <E : Comparable<E>> checkSuffixPrefixProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (i in 0..A.size) {
            for (j in 0 until B.size) {
                val subListA = A.subList(0, i)
                val subListB = B.subList(j, B.size)
                if (!compareDouble(
                        prefixAlignment(subListA, subListB, solution.getScoringScheme()),
                        solution.suffixPrefixSA(i, j)
                    )
                )
                    assertEquals(
                        prefixAlignment(subListA, subListB, solution.getScoringScheme()),
                        solution.suffixPrefixSA(i, j)
                    )
            }
        }
    }

    fun <E : Comparable<E>> checkSemiLocalSA(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        checkStringSubstringProblem(A, B, solution)
        checkPrefixSuffixProblem(A, B, solution)
        checkSuffixPrefixProblem(A, B, solution)
        checkSubstringStringProblem(A, B, solution)
    }

    @Test
    fun randomCheckerTest() {
        val random = Random(0)
        val sizeA = random.nextInt(100)
        val sizeB = random.nextInt(100)
        val repeats = 250
        for (r in 0 until repeats) {
            val A = (0 until sizeA).map { alphabet[kotlin.math.abs(random.nextInt()) % alphabet.size] }
            val B = (0 until sizeB).map { alphabet[kotlin.math.abs(random.nextInt()) % alphabet.size] }
            val solution = getSemiLocalSolution(A, B)
            checkSemiLocalSA(A, B, solution)
        }
    }

    @Test
    fun randomMismatchPairTest() {
        val random = Random(0)
        val sizeA = random.nextInt(100)
        val sizeB = random.nextInt(100)
        val repeats = 250
        for (r in 0 until repeats) {
            val A = (0 until sizeA).map { 'a' }
            val B = (0 until sizeB).map { 'b' }
            val solution = getSemiLocalSolution(A, B)
            checkSemiLocalSA(A, B, solution)
        }
    }

    @Test
    fun randomSmallAlphabetTest() {
        val random = Random(0)
        val sizeA = random.nextInt(100)
        val sizeB = random.nextInt(100)
        val repeats = 250
        val alphabetSize = 3
        for (r in 0 until repeats) {
            val A = (0 until sizeA).map { alphabet[kotlin.math.abs(random.nextInt()) % alphabetSize] }
            val B = (0 until sizeB).map { alphabet[kotlin.math.abs(random.nextInt()) % alphabetSize] }
            val solution = getSemiLocalSolution(A, B)
            checkSemiLocalSA(A, B, solution)
        }
    }

    @Test
    fun randomFullMatchPairTest() {
        val random = Random(0)
        val sizeA = random.nextInt(100)
        val sizeB = random.nextInt(100)
        val repeats = 250
        for (r in 0 until repeats) {
            val A = (0 until sizeA).map { 'a' }
            val B = (0 until sizeB).map { 'a' }
            val solution = getSemiLocalSolution(A, B)
            checkSemiLocalSA(A, B, solution)
        }
    }

    @Test
    fun fullyMismatchedTest() {
        val A = "aaaaa"
        val B = "bb"
        checkSemiLocalSA(A.toList(), B.toList(), getSemiLocalSolution(A.toList(), B.toList()))
    }

    @Test
    fun fullyMatchedTest() {
        val A = "aa"
        val B = "aa"
        (getSemiLocalSolution(A.toList(), B.toList()) as NaiveSemiLocalSA<Char>).print()
        checkSemiLocalSA(A.toList(), B.toList(), getSemiLocalSolution(A.toList(), B.toList()))
    }
}