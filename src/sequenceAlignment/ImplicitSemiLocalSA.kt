package sequenceAlignment

import longestCommonSubsequence.*
import utils.CountingQuerySA
import utils.IStochasticMatrix
import utils.VSubBistochasticMatrix

import utils.*


/**
 *TODO make a note about round,wokrs only with ratiobal scheme (ask tiskin)
 */
class ImplicitSemiLocalSA<T : Comparable<T>> : ISemiLocalCombined<T> {

    override val pattern: List<T>
    override val text: List<T>

    private var v = 0
    private var mu = 0
    private var m = 0
    private var n = 0
    private val scoringScheme:IScoringScheme

    private val rangeTree2D: RangeTree2D<Int>
    private val countingQuerySA = CountingQuerySA()
    private val  stochasticMatrix: IStochasticMatrix


    constructor(a:List<T>, b:List<T>, scoringScheme: IScoringScheme, kernelEvaluator: IStrategyKernelEvaluation<T>){
        v = scoringScheme.getNormalizedMismatchScore().denominator
        mu = scoringScheme.getNormalizedMismatchScore().numerator
        m = a.size
        n = b.size
        pattern = a
        text = b
        this.scoringScheme = scoringScheme

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

    constructor(lcsSolution: IImplicitSemiLocalLCSSolution<T>){
        this.stochasticMatrix = lcsSolution.kernel
        rangeTree2D = RangeTree2D(lcsSolution.kernel.iterator().asSequence().toMutableList())
        v = 1
        mu = 0
        m = lcsSolution.pattern.size
        n = lcsSolution.text.size
        pattern = lcsSolution.pattern
        text = lcsSolution.text
        scoringScheme = RegularScoringScheme(0,1)
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

    override fun prefixSuffix(k: Int, j: Int): Double {
        if (k < 0 || k > m || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, j), k, j) - k
    }

    override fun stringSubstring(i: Int, j: Int): Double {
        if (i < 0 || i > n || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(i + m, j), i, j)
    }

    override fun substringString(k: Int, l: Int): Double {
        if (k < 0 || k > m || l < 0 || l > m) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, m + n - l), k, l) - m - k + l
    }

    override fun suffixPrefix(l: Int, i: Int): Double {
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

    override fun nextInCol(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> j - (i + 1 - m) - countingQuerySA.dominanceSumTopRightDownMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
            ISemiLocalFastAccess.Direction.BackWard -> j - (i - 1 - m) - countingQuerySA.dominanceSumTopRightUpMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
        }


    override fun nextInRow(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> j + 1 - (i - m) - countingQuerySA.dominanceSumTopRightRightMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
            ISemiLocalFastAccess.Direction.BackWard -> j - 1 - (i - m) - countingQuerySA.dominanceSumTopRightLeftMove(
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