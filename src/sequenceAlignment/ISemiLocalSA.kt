package sequenceAlignment

import javax.xml.crypto.dom.DOMCryptoContext


typealias ISemiLocalSA = ISemiLocalSequenceAlignment


/**
 * Interface for semiLocal SA problem for the two given lists of comparable elements.
 * The definition of semiLocal SA problem see book "The algebra of string comparison: Computing with sticky braids",
 * page 89
 */
interface ISemiLocalSequenceAlignment {

    fun getScoringScheme():ScoringScheme

    /**
     *For a given A and B asks for sa score for A and B[i:j]
     */
    fun stringSubstringSA(i: Int, j: Int): Double

    /**
     *For a given A and B asks for sa score for A[k:A.size] and B[0:j]
     */
    fun prefixSuffixSA(k: Int, j: Int): Double

    /**
     *For a given A and B asks for sa score for A[0:l] and B[i:B.size]
     */
    fun suffixPrefixSA(l: Int, i: Int): Double

    /**
     *For a given A and B asks for sa score for A[k:l] and B
     */
    fun substringStringSA(k: Int, l: Int): Double

}