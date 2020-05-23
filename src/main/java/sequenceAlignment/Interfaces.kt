package sequenceAlignment


import longestCommonSubsequence.AbstractMongeMatrix
import utils.IScoringScheme


typealias ISemiLocalSA = ISemiLocalSequenceAlignment


interface ISemiLocalStringSubstringProblem {
    /**
     *For a given A and B asks for sa score for A and B[i:j]
     */
    fun stringSubstring(i: Int, j: Int): Double
}

interface ISemiLocalPrefixSuffixProblem {
    /**
     *For a given A and B asks for sa  score for A[k:A.size] and B[0:j]
     */
    fun prefixSuffix(k: Int, j: Int): Double
}

interface ISemiLocalSuffixPrefixProblem {
    /**
     *For a given A and B asks for sa score for A[0:l] and B[i:B.size]
     */
    fun suffixPrefix(l: Int, i: Int): Double
}

interface ISemiLocalSubstringStringProblem {
    /**
     *For a given A and B asks for sa score for A[k:l] and B
     */
    fun substringString(k: Int, l: Int): Double
}


/**
 * Interface for semiLocal SA problem for the two given lists of comparable elements.
 * The definition of semiLocal SA problem see book "The algebra of string comparison: Computing with sticky braids",
 * page 89
 */
interface ISemiLocalSequenceAlignment : ISemiLocalSubstringStringProblem, ISemiLocalSuffixPrefixProblem,
    ISemiLocalPrefixSuffixProblem, ISemiLocalStringSubstringProblem {

    fun print()
}


interface ISemiLocalData<T> {
    /**
     * the first list that compared
     */
    val pattern: List<T>

    /**
     * the second list that compared
     */
    val text: List<T>


}

interface ISemiLocalFastAccess {

    /**
     * if index grow then forward else backward
     */
    enum class Direction {
        Forward,
        BackWard
    }

    /**
     * Provides fast access to the next matrix element for semilocal matrix,given current position and rawValue in it.
     * For example, given i=0,j=0,rawValue=0,direction=forwards it returns a raw value in position i=0, j=1
     * @param i \in [0,a.size + b.size]
     * @param j \in [0,a.size + b.size]
     * @param rawValue value that holds semilocal matrix without application of reverse normalization and so on
     * @return returns raw value that stored in matrix i.e no reverse normalization is applied
     */
    fun nextInRow(i: Int, j: Int, rawValue: Double, direction: Direction): Double

    /**
     * Provides fast access to the next matrix element for semilocal matrix,given current position and rawValue in it.
     * For example, given i=0,j=0,rawValue=0,direction=forwards it returns a raw value in position i=1, j=0
     * @param i \in [0,a.size + b.size]
     * @param j \in [0,a.size + b.size]
     * @param rawValue value that holds semilocal matrix without application of reverse normalization and so on
     * @return returns raw value that stored in matrix i.e no reverse normalization is applied
     */
    fun nextInCol(i: Int, j: Int, rawValue: Double, direction: Direction): Double


    /**
     * Provides optimal according to implementation access to matrix element for semilocal matrix,given current position in it.
     * Take into account that returns double. Possible usage of round
     * @param i \in [0,a.size + b.size]
     * @param j \in [0,a.size + b.size]
     * @param rawValue value that holds semilocal matrix without application of reverse normalization and so on
     * @return returns raw value that stored in matrix i.e no reverse normalization is applied
     */
    fun getAtPosition(i: Int, j: Int): Double

    /**
     * returns scoring scheme for semilocal problem
     */
    fun getScoringScheme(): IScoringScheme

    /**
     * returns monge matrix associated with semi-local solution
     */
    fun getMatrix():AbstractMongeMatrix

}

//TODO lcs should implement this interface
/**
 * Interface that provides access to solution to matrix element of semilocal problem for the given lists of comparable elements.
 */
interface ISemiLocalSolution<T> : ISemiLocalData<T>, ISemiLocalFastAccess


/**
 *
 */
interface ISemiLocalCombined<T>: ISemiLocalSA, ISemiLocalSolution<T>


