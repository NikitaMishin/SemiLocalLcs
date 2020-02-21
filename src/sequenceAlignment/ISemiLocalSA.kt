package sequenceAlignment

import javax.xml.crypto.dom.DOMCryptoContext
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round


typealias ISemiLocalSA = ISemiLocalSequenceAlignment

const val epsilon = 1E-5

data class ScoringScheme(val matchScore: Double, val mismatchScore: Double, val gapScore: Double) {
    //TODO bug in book?
    val normalizedMismatch: Double
    val numeratorMismatch: Int
    val denominator: Int

    private val isAlreadyNormalized: Boolean

    init {
        var a: Double
        var b: Double

        // if alredy normalized
        if (abs(matchScore - 1.0) < epsilon && abs(gapScore) < epsilon && mismatchScore > 0 && mismatchScore < 1) {
            isAlreadyNormalized = true
            a = abs(mismatchScore)
            b = 1.0
            while (floor(a) != a) {
                a *= 10
                b *= 10.0
            }
        } else {
            isAlreadyNormalized = false
            //TODO make normal just lazy solution
            a = ((mismatchScore - 2 * gapScore) * 1000)
            b = ((matchScore - 2 * gapScore) * 1000)
        }

        val nok = gcd(a.toInt(), b.toInt())
        numeratorMismatch = a.toInt() / nok
        denominator = b.toInt() / nok
        normalizedMismatch = numeratorMismatch.toDouble() / denominator


    }

    //TODO bug in book?
    fun originalScoreFunc(score: Double, m: Int, n: Int) =
        if (isAlreadyNormalized) score else score * (matchScore - 2 * gapScore) + (m + n) * gapScore

    private fun gcd(g: Int, e: Int): Int {
        var a = abs(g)
        var b = abs(e)
        while (b != 0) {
            val t = b
            b = a % b
            a = t
        }
        return a
    }
}

/**
 * Interface for semiLocal SA problem for the two given lists of comparable elements.
 * The definition of semiLocal SA problem see book "The algebra of string comparison: Computing with sticky braids",
 * page 89
 */
interface ISemiLocalSequenceAlignment {

    fun getScoringScheme(): ScoringScheme

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