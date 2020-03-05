package sequenceAlignment

import longestCommonSubsequence.Symbol
import longestCommonSubsequence.SymbolType
import utils.IScoringScheme
import java.lang.Double.max

//TODO  naive only works with rational scoring scheme
class NaiveSemiLocalSA<T : Comparable<T>>(val a: List<T>, val b: List<T>, private val scoringScheme: IScoringScheme) :
    ISemiLocalSA, ISemiLocalSolution<T> {
    override val pattern: List<T> = a
    override val text: List<T> = b

    private var m = a.size
    private var n = b.size
    private var aSymbolString: List<Symbol<T>> =
        a.map {
            Symbol(
                it,
                SymbolType.AlphabetSymbol
            )
        }
    private var bSymbolString: List<Symbol<T>> =
        a.map {
            Symbol(
                it,
                SymbolType.WildCardSymbol
            )
        } +
                b.map {
                    Symbol(
                        it,
                        SymbolType.AlphabetSymbol
                    )
                } +
                a.map {
                    Symbol(
                        it,
                        SymbolType.WildCardSymbol
                    )
                }

    private fun reverseRegularizationScore(value: Double, i: Int, j: Int): Double =
        value //scoringScheme.getOriginalScoreFunc(value,m,i,j)

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

        val matchScore = scoringScheme.getMatchScore().toDouble()
        val mismatchScore = scoringScheme.getMismatchScore().toDouble()
        val gapScore = scoringScheme.getGapScore().toDouble()


        val scoreMatrix = Array(m) { Array(n) { 0.0 } }
        for (i in 1 until scoreMatrix.size) {
            for (j in 1 until scoreMatrix[0].size) {
                scoreMatrix[i][j] = max(
                    scoreMatrix[i - 1][j - 1] + (
                            if ((bSub[j - 1].type == SymbolType.WildCardSymbol) ||
                                (bSub[j - 1].type == SymbolType.AlphabetSymbol && aSymbolString[i - 1].symbol == bSub[j - 1].symbol)
                            )
                                matchScore
                            else
                                mismatchScore),
                    max(
                        scoreMatrix[i - 1][j] + gapScore,
                        scoreMatrix[i][j - 1] + gapScore
                    )
                )
            }
        }
        return scoreMatrix[m - 1][n - 1]
    }

    // Implementation of ISemiLocalSA interface

    override fun stringSubstringSA(i: Int, j: Int): Double {
        if (i < 0 || i > n || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(matrix[i + m][j], i + m, j)
    }

    override fun prefixSuffixSA(k: Int, j: Int): Double {
        if (k < 0 || k > m || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(matrix[m - k][j], m - k, j) - k
    }

    override fun suffixPrefixSA(l: Int, i: Int): Double {
        if (l < 0 || l > m || i < 0 || i > n) return Double.NaN
        return reverseRegularizationScore(matrix[i + m][m + n - l], i + m, m + n - l) - m + l

    }

    override fun substringStringSA(k: Int, l: Int): Double {
        if (k < 0 || k > m || l < 0 || l > m) return Double.NaN
        return reverseRegularizationScore(matrix[m - k][m + n - l], m - k, m + n - l) - m - k + l
    }


    override fun print() {
        for (i in 0 until matrix.size) {
            for (j in 0 until matrix[0].size) {
                print("  ${matrix[i][j]}  ")
            }
            println()
        }
    }

    //Implementation of ISemiLocalSolution interface

    override fun getScoringScheme() = scoringScheme

    override fun nextInRow(i: Int, j: Int, rawValue: Double, direction: ISemiLocalSolution.Direction) =
        when (direction) {
            ISemiLocalSolution.Direction.Forward -> matrix[i][j + 1]
            ISemiLocalSolution.Direction.BackWard -> matrix[i][j - 1]
        }

    override fun nextInCol(i: Int, j: Int, rawValue: Double, direction: ISemiLocalSolution.Direction) =
        when (direction) {
            ISemiLocalSolution.Direction.Forward -> matrix[i + 1][j]
            ISemiLocalSolution.Direction.BackWard -> matrix[i - 1][j]
        }

    override fun getAtPosition(i: Int, j: Int): Double = matrix[i][j]


}


