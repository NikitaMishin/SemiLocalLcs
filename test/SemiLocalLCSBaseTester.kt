import org.junit.jupiter.api.Assertions
import kotlin.random.Random

internal open class SemiLocalLCSBaseTester(val random: Random) {

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


    fun getRandomString(randToSkip:Int,stringSize:Int,alphabetString:List<Char>): List<Char> {
        for (i in  0 until randToSkip) random.nextInt()
        return (0 until stringSize).map { alphabetString[kotlin.math.abs(random.nextInt()) % alphabetString.size]  }
    }

    fun <E : Comparable<E>> checkStringSubstringProblem(A: List<E>, B: List<E>, solution: ISemiLocalLCS) {
        for (j in 0..B.size) {
            for (i in 0 until j) {
                val subList = B.subList(i, j)
                println("${i},${j},${B.size}")
                println(A)
                println(subList)
                Assertions.assertEquals(dummyLcs(A, subList)[A.size][subList.size], solution.stringSubstringLCS(i, j))
            }
        }
    }

    fun <E : Comparable<E>> checkSubstringStringProblem(A: List<E>, B: List<E>, solution: ISemiLocalLCS) {
        for (j in 0..A.size) {
            for (i in 0 until j) {
                val subList = A.subList(i, j)
                Assertions.assertEquals(dummyLcs(subList, B)[subList.size][B.size], solution.substringStringLCS(i, j))
            }
        }
    }

    fun <E : Comparable<E>> checkPrefixSuffixProblem(A: List<E>, B: List<E>, solution: ISemiLocalLCS) {
        for (i in 0 until A.size) {
            for (j in 0..B.size) {
                val subListA = A.subList(i, A.size)
                val subListB = B.subList(0, j)
                Assertions.assertEquals(
                    dummyLcs(subListA, subListB)[subListA.size][subListB.size],
                    solution.prefixSuffixLCS(i, j)
                )
            }
        }
    }

    fun <E : Comparable<E>> checkSuffixPrefixProblem(A: List<E>, B: List<E>, solution: ISemiLocalLCS) {
        for (i in 0..A.size) {
            for (j in 0 until B.size) {
                val subListA = A.subList(0, i)
                val subListB = B.subList(j, B.size)
                Assertions.assertEquals(
                    dummyLcs(subListA, subListB)[subListA.size][subListB.size],
                    solution.suffixPrefixLCS(i, j)
                )
            }
        }
    }
    fun <E : Comparable<E>> checkSemiLocalLCS(A: List<E>, B: List<E>, solution: ISemiLocalLCS) {
        checkStringSubstringProblem(A,B,solution)
        checkPrefixSuffixProblem(A,B,solution)
        checkSuffixPrefixProblem(A,B,solution)
        checkSubstringStringProblem(A,B,solution)
    }

    }