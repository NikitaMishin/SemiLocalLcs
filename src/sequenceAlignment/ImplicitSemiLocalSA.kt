package sequenceAlignment

import utils.CountingQuerySA
import utils.IStochasticMatrix
import longestCommonSubsequence.IStrategyKernelEvaluation
import utils.Matrix
import longestCommonSubsequence.Symbol
import longestCommonSubsequence.SymbolType
import utils.VSubBistochasticMatrix

import utils.*


/**
 *TODO make a note about round,wokrs only with ratiobal scheme (ask tiskin)
 */
class ImplicitSemiLocalSA<T : Comparable<T>>(override val pattern: List<T>, override val text: List<T>,
                                                         private val scoringScheme: IScoringScheme, val kernelEvaluator: IStrategyKernelEvaluation<T>
) : ISemiLocalSA, ISemiLocalSolution<T> {

    private val v = scoringScheme.getNormalizedMismatchScore().denominator
    private val mu = scoringScheme.getNormalizedMismatchScore().numerator
    private val m = pattern.size
    private val n = text.size

    private val rangeTree2D: RangeTree2D<Int>
    private val countinqQuery = CountingQuerySA()
    private val stochasticMatrix: IStochasticMatrix

    init {

        val permMatrixExtended = kernelEvaluator.evaluate(
            pattern.flatMap {
                Symbol(
                    it,
                    SymbolType.GuardSymbol
                ).repeatShallowCopy(mu) +
                        Symbol(
                            it,
                            SymbolType.AlphabetSymbol
                        ).repeatShallowCopy(v - mu)
            },
            text.flatMap {
                Symbol(
                    it,
                    SymbolType.GuardSymbol
                ).repeatShallowCopy(mu) +
                        Symbol(
                            it,
                            SymbolType.AlphabetSymbol
                        ).repeatShallowCopy(v - mu)
            }
        )


        val size = (pattern.size + text.size)
        //Note ν-(sub)bistochastic matrix, there can be at most ν nonzeros in every row and every column
        val points = IntArray(size * size) { 0 } // i*n + j

        for (p in permMatrixExtended) {
            val posInInitialI = p.i / v;
            val posInInitialJ = p.j / v;
            points[posInInitialI * size + posInInitialJ]++
        }

        //first create actual points, then filter all zero points, and pass them to range tree
        val p =
            points.mapIndexed { index, value -> Position2D(index / size, index % size, value) }.filter { it.value != 0 }

        stochasticMatrix = VSubBistochasticMatrix(p, size, size, v)
        rangeTree2D = RangeTree2D(p)

    }

    private fun reverseRegularizationScore(value: Double, i: Int, j: Int): Double =
        scoringScheme.getOriginalScoreFunc(value, m, i, j)

    /**
     * i from 0 to m + n
     */
    private fun canonicalDecomposition(i: Int, j: Int): Double {
        return j - (i - m) - rangeTree2D.ortoghonalQuery(
            IntervalQuery(i, m + n + 1),
            IntervalQuery(0, j - 1)
        ).toDouble() / v
    }

    //Implementation of ISemiLocalSA interface

    override fun prefixSuffixSA(k: Int, j: Int): Double {
        if (k < 0 || k > m || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, j), k, j) - k
    }

    override fun stringSubstringSA(i: Int, j: Int): Double {
        if (i < 0 || i > n || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(i + m, j), i, j)
    }

    override fun substringStringSA(k: Int, l: Int): Double {
        if (k < 0 || k > m || l < 0 || l > m) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, m + n - l), k, l) - m - k + l
    }

    override fun suffixPrefixSA(l: Int, i: Int): Double {
        if (l < 0 || l > m || i < 0 || i > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(i + m, m + n - l), i, l) - m + l
    }

    override fun print() {
        for (i in 0 until m + n + 1) {
            for (j in 0 until m + n + 1) {
                print("  ${getAtPosition(i, j).round(1)}  ")
            }
            println()
        }
    }

    //Implementation of ISemiLocalSolution interface


    override fun getScoringScheme() = scoringScheme

    override fun getAtPosition(i: Int, j: Int): Double = canonicalDecomposition(i, j)

    override fun nextInCol(i: Int, j: Int, rawValue: Double, direction: ISemiLocalSolution.Direction): Double =
        when (direction) {
            ISemiLocalSolution.Direction.Forward -> j - (i + 1 - m) - countinqQuery.dominanceSumTopRightDownMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
            ISemiLocalSolution.Direction.BackWard -> j - (i - 1 - m) - countinqQuery.dominanceSumTopRightUpMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
        }

    override fun nextInRow(i: Int, j: Int, rawValue: Double, direction: ISemiLocalSolution.Direction): Double =
        when (direction) {
            ISemiLocalSolution.Direction.Forward -> j + 1 - (i - m) - countinqQuery.dominanceSumTopRightRightMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
            ISemiLocalSolution.Direction.BackWard -> j - 1 - (i - m) - countinqQuery.dominanceSumTopRightLeftMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
        }

}


//class ExplicitSemiLocalSA<T : Comparable<T>>() : ISemiLocalSA {
//
//    override fun getScoringScheme(): IScoringScheme {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun stringSubstringSA(i: Int, j: Int): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun prefixSuffixSA(k: Int, j: Int): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun suffixPrefixSA(l: Int, i: Int): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun substringStringSA(k: Int, l: Int): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun print() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
////