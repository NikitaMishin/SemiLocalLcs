package sequenceAlignment

import Symbol
import java.lang.Double.max


data class ScoringScheme(val matchScore: Double, val mismatchScore: Double, val gapScore: Double) {
    //TODO bug in book?
    val normalizedMismatch =  (mismatchScore - 2 * gapScore) / (matchScore - 2 * gapScore)
    //TODO bug in book?
    fun originalScoreFunc(score: Double, m: Int, n: Int) = score
        //score * (matchScore - 2 * gapScore) + (m + n) * gapScore
}


class NaiveSemiLocalSA<T : Comparable<T>>(val a: List<T>, val b: List<T>, private val scoringScheme: ScoringScheme) :
    ISemiLocalSA {
    private var m = a.size
    private var n = b.size
    private var aSymbolString: List<Symbol<T>> =
        a.map { Symbol(it, SymbolType.AlphabetSymbol) }
    private var bSymbolString: List<Symbol<T>> =
        a.map { Symbol(it, SymbolType.WildCardSymbol) } +
                b.map { Symbol(it, SymbolType.AlphabetSymbol) } +
                a.map { Symbol(it, SymbolType.WildCardSymbol) }

    private fun reverseRegularizationScore(value:Double):Double = scoringScheme.originalScoreFunc(value,m,n)

    internal val matrix = Array(a.size + b.size + 1)
    { i ->
        Array(a.size + b.size + 1)
        { j ->
            if (j <= (i - m)) {
                (j - i + m).toDouble()
            } else {
                this.prefixAlignment(i, j + m)
            }
        }
    }


    /**
     * Asks for sa score between @aSymbolString and substring bSymbolString[i,j].
     * Note that WildCardSymbol matches any other symbol.
     * dummy implementation of SA.
     * @param i start index inclusive
     * @param j end index exclusive
     * @return score or Double.Negative infinity if out of boundaries
     */
    private fun prefixAlignment(i: Int, j: Int): Double {

        val m = a.size + 1
        val bSub = bSymbolString.subList(i, j)
        val n = bSub.size + 1

        val scoreMatrix = Array(m) { Array(n) { 0.0 } }
        for (i in 1 until scoreMatrix.size) {
            for (j in 1 until scoreMatrix[0].size) {
                scoreMatrix[i][j] = max(
                    scoreMatrix[i - 1][j - 1] + (
                            if ((bSub[j - 1].type == SymbolType.WildCardSymbol) ||
                                (bSub[j - 1].type == SymbolType.AlphabetSymbol && aSymbolString[i - 1].symbol == bSub[j - 1].symbol)
                            )
                                1.0 // normalized scoringScheme.matchScore
                            else
                                scoringScheme.normalizedMismatch), // normalized scoringScheme.mismatchScore),
                    max(
                        scoreMatrix[i - 1][j] , //normalized gap =0
                        scoreMatrix[i][j - 1]   // normlaized gap =0
                    )
                )
            }
        }
        return scoreMatrix[m - 1][n - 1]
    }

    override fun getScoringScheme(): ScoringScheme = scoringScheme


    override fun stringSubstringSA(i: Int, j: Int): Double {
        if (i < 0 || i > n || j < 0 || j > n) return Double.NEGATIVE_INFINITY
        return reverseRegularizationScore(matrix[i + m][j])
    }

    override fun prefixSuffixSA(k: Int, j: Int): Double {
        if (k < 0 || k > m || j < 0 || j > n) return Double.NEGATIVE_INFINITY
        return reverseRegularizationScore(matrix[m - k][j]) - k
    }

    override fun suffixPrefixSA(l: Int, i: Int): Double {
        if (l < 0 || l > m || i < 0 || i > n) return Double.NEGATIVE_INFINITY
        return reverseRegularizationScore(matrix[i + m][m + n - l]) - m + l

    }

    override fun substringStringSA(k: Int, l: Int): Double {
        if (k < 0 || k > m || l < 0 || l > m) return Double.NEGATIVE_INFINITY
        return reverseRegularizationScore(matrix[m - k][m + n - l]) - m - k + l
    }

    /**
     * For testing purposes
     */
    internal fun print() {
        for (i in 0 until matrix.size) {
            for (j in 0 until matrix[0].size) {
                print("  ${matrix[i][j]}  ")
            }
            println()
        }

    }


}


