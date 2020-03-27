package sequenceAlignment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.IScoringScheme
import kotlin.random.Random


internal abstract class SemiLocalSABaseTester(val random: Random) {
    val epsilon = 1E-5

    fun compareDouble(a: Double, b: Double): Boolean = kotlin.math.abs(a - b) < epsilon

    fun <T : Comparable<T>> prefixAlignment(a: List<T>, b: List<T>, scoringScheme: IScoringScheme): Double {
        val scoreMatrix = Array(a.size + 1) { Array(b.size + 1) { 0.0 } }
        val match = scoringScheme.getMatchScore().toDouble()
        val mismatch = scoringScheme.getMismatchScore().toDouble()
        val gap = scoringScheme.getGapScore().toDouble()

        for (i in 1 until scoreMatrix.size) {
            for (j in 1 until scoreMatrix[0].size) {
                scoreMatrix[i][j] = java.lang.Double.max(
                    scoreMatrix[i - 1][j - 1] + (if (a[i - 1] == b[j - 1]) match
                    else mismatch),
                    java.lang.Double.max(
                        scoreMatrix[i - 1][j] + gap,
                        scoreMatrix[i][j - 1] + gap
                    )
                )
            }
        }

        return scoreMatrix[a.size][b.size]// - (a.size+b.size)*scoringScheme.gapScore) / (scoringScheme.matchScore-2*scoringScheme.mismatchScore)
    }

    abstract fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): Pair<ISemiLocalSA, IScoringScheme>

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


    private fun <E : Comparable<E>> checkStringSubstringProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA,
                                                                                        scoringScheme: IScoringScheme) {
        for (j in 0..B.size) {
            for (i in 0 until j) {
                val subList = B.subList(i, j)
                if (!compareDouble(
                        prefixAlignment(A, subList, scoringScheme),
                        solution.stringSubstring(i, j)
                    )
                ) {
                    assertEquals(
                        prefixAlignment(A, subList, scoringScheme),
                        solution.stringSubstring(i, j)
                    )
                }

            }
        }
    }

    private fun <E : Comparable<E>> checkSubstringStringProblem(A: List<E>,B: List<E>,solution: ISemiLocalSA,
                                                                                        scoringScheme: IScoringScheme) {
        for (j in 0..A.size) {
            for (i in 0 until j) {
                val subList = A.subList(i, j)
                if (!compareDouble(
                        prefixAlignment(subList, B, scoringScheme),
                        solution.substringString(i, j)
                    )
                )
                    assertEquals(
                        prefixAlignment(subList, B, scoringScheme),
                        solution.substringString(i, j)
                    )
            }
        }
    }

    private fun <E : Comparable<E>> checkPrefixSuffixProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA,
                                                                                        scoringScheme: IScoringScheme) {
        for (i in A.indices) {
            for (j in 0..B.size) {
                val subListA = A.subList(i, A.size)
                val subListB = B.subList(0, j)


                if (!compareDouble(
                        prefixAlignment(subListA, subListB, scoringScheme),
                        solution.prefixSuffix(i, j)
                    )
                ) {
                    assertEquals(
                        prefixAlignment(subListA, subListB, scoringScheme),
                        solution.prefixSuffix(i, j)
                    )
                }
            }
        }
    }

    private fun <E : Comparable<E>> checkSuffixPrefixProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA,
                                                                                        scoringScheme: IScoringScheme) {
        for (i in 0..A.size) {
            for (j in 0 until B.size) {
                val subListA = A.subList(0, i)
                val subListB = B.subList(j, B.size)
                if (!compareDouble(
                        prefixAlignment(subListA, subListB, scoringScheme),
                        solution.suffixPrefix(i, j)
                    )
                )
                    assertEquals(
                        prefixAlignment(subListA, subListB, scoringScheme),
                        solution.suffixPrefix(i, j)
                    )
            }
        }
    }

    fun <E : Comparable<E>> checkSemiLocalSA(A: List<E>, B: List<E>, solution: ISemiLocalSA,
                                                                                    scoringScheme: IScoringScheme) {
        checkStringSubstringProblem(A, B, solution, scoringScheme)
        checkPrefixSuffixProblem(A, B, solution, scoringScheme)
        checkSuffixPrefixProblem(A, B, solution, scoringScheme)
        checkSubstringStringProblem(A, B, solution, scoringScheme)
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
            checkSemiLocalSA(A, B, solution.first, solution.second)
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
            checkSemiLocalSA(A, B, solution.first, solution.second)
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
            checkSemiLocalSA(A, B, solution.first, solution.second)
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
            checkSemiLocalSA(A, B, solution.first, solution.second)
        }
    }

    @Test
    fun fullyMismatchedTest() {
        val A = "aaaaa"
        val B = "bb"
        val solution = getSemiLocalSolution(A.toList(), B.toList())
        checkSemiLocalSA(A.toList(), B.toList(), solution.first, solution.second)
    }

    @Test
    fun fullyMatchedTest() {
        val A = "aa"
        val B = "aa"
        val solution = getSemiLocalSolution(A.toList(), B.toList())
        checkSemiLocalSA(A.toList(), B.toList(), solution.first, solution.second)
    }
}