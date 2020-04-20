package longestCommonSubsequence

import sequenceAlignment.*
import utils.Matrix
import kotlin.random.Random


interface ISemiLocalStringSubstringLCSProblem {
    /**
     *For a given A and B asks for score for A and B[i:j]
     */
    fun stringSubstringLCS(i: Int, j: Int): Int
}

interface ISemiLocalPrefixSuffixLCSProblem {
    /**
     *For a given A and B asks for  score for A[k:A.size] and B[0:j]
     */
    fun prefixSuffixLCS(k: Int, j: Int): Int
}

interface ISemiLocalSuffixPrefixLCSProblem {
    /**
     *For a given A and B asks for  score for A[0:l] and B[i:B.size]
     */
    fun suffixPrefixLCS(l: Int, i: Int): Int
}

interface ISemiLocalSubstringStringLCSProblem {
    /**
     *For a given A and B asks for lcs score for A[k:l] and B
     */
    fun substringStringLCS(k: Int, l: Int): Int
}


/**
 * Interface for semiLocal LCS problem for the two given lists of comparable elements.
 * The definition of semiLocal LCS problem see book "The algebra of string comparison: Computing with sticky braids",
 * page 51
 */
interface ISemiLocalLCS : ISemiLocalSA, ISemiLocalSubstringStringLCSProblem, ISemiLocalSuffixPrefixLCSProblem,
    ISemiLocalPrefixSuffixLCSProblem, ISemiLocalStringSubstringLCSProblem {

    fun getAtPosition(i: Int, j: Int): Double

    companion object {
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

        fun getRandomString(randToSkip: Int, stringSize: Int, alphabetString: List<Char>, random: Random): List<Char> {
            for (i in 0 until randToSkip) random.nextInt()
            return (0 until stringSize).map { alphabetString[kotlin.math.abs(random.nextInt()) % alphabetString.size] }
        }
    }
}



interface IImplicitSemiLocalLCSSolution<T> : ISemiLocalData<T> {
    val kernel: Matrix
}


