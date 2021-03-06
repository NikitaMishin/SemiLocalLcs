package longestCommonSubsequence

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import sequenceAlignment.ISemiLocalSA
import java.lang.Integer.max
import kotlin.math.max
import kotlin.random.Random


internal abstract class SemiLocalLCSBaseTester(val random: Random) {

    abstract fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): ISemiLocalSA

    /**
     *
     *
     *
     * @param a List of elements in a
     * @param n
     */
    fun <Elem> dummyLcs(a: List<Elem>, b: List<Elem>): Array<DoubleArray> {
        val n = a.count() + 1
        val m = b.count() + 1

        val lcsMatrix = Array(n) { _ -> DoubleArray(m) { _ -> 0.0 } }
        for (rowNum in 1 until n) {
            for (colNum in 1 until m) {
                if (a[rowNum - 1] == b[colNum - 1]) lcsMatrix[rowNum][colNum] = lcsMatrix[rowNum - 1][colNum - 1] + 1
                else {
                    lcsMatrix[rowNum][colNum] = max(lcsMatrix[rowNum - 1][colNum], lcsMatrix[rowNum][colNum - 1])
                }
            }
        }
        return lcsMatrix
    }

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


    fun getRandomString(randToSkip: Int, stringSize: Int, alphabetString: List<Char>): List<Char> {
        for (i in 0 until randToSkip) random.nextInt()
        return (0 until stringSize).map { alphabetString[kotlin.math.abs(random.nextInt()) % alphabetString.size] }
    }

    private fun <E : Comparable<E>> checkStringSubstringProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (j in 0..B.size) {
            for (i in 0 until j) {
                val subList = B.subList(i, j)
                Assertions.assertEquals(dummyLcs(A, subList)[A.size][subList.size], solution.stringSubstring(i, j))
            }
        }
    }

    private fun <E : Comparable<E>> checkSubstringStringProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (j in 0..A.size) {
            for (i in 0 until j) {
                val subList = A.subList(i, j)
                Assertions.assertEquals(dummyLcs(subList, B)[subList.size][B.size], solution.substringString(i, j))
            }
        }
    }

    private fun <E : Comparable<E>> checkPrefixSuffixProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (i in A.indices) {
            for (j in 0..B.size) {
                val subListA = A.subList(i, A.size)
                val subListB = B.subList(0, j)
                Assertions.assertEquals(
                    dummyLcs(subListA, subListB)[subListA.size][subListB.size],
                    solution.prefixSuffix(i, j)
                )
            }
        }
    }

    private fun <E : Comparable<E>> checkSuffixPrefixProblem(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
        for (i in 0..A.size) {
            for (j in 0 until B.size) {
                val subListA = A.subList(0, i)
                val subListB = B.subList(j, B.size)
                Assertions.assertEquals(
                    dummyLcs(subListA, subListB)[subListA.size][subListB.size],
                    solution.suffixPrefix(i, j)
                )
            }
        }
    }

    fun <E : Comparable<E>> checkSemiLocalLCS(A: List<E>, B: List<E>, solution: ISemiLocalSA) {
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
            checkSemiLocalLCS(A, B, solution)
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
            checkSemiLocalLCS(A, B, solution)
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
            checkSemiLocalLCS(A, B, solution)
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
            checkSemiLocalLCS(A, B, solution)
        }
    }

    @Test
    fun fullyMismatchedTest() {
        val A = "aaaaa"
        val B = "bb"
        checkSemiLocalLCS(A.toList(), B.toList(), getSemiLocalSolution(A.toList(), B.toList()))
    }

    @Test
    fun fullyMatchedTest() {
        val A = "aaaaa"
        val B = "aaaaa"
        checkSemiLocalLCS(A.toList(), B.toList(), getSemiLocalSolution(A.toList(), B.toList()))
    }
}