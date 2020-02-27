package sequenceAlignment

import IStrategyKernelEvaluation
import Matrix
import Symbol
import utils.IScoringScheme
import utils.IntervalQuery
import utils.Position2D
import utils.RangeTree2D


/**
 *
 */
class ImplicitSemiLocalSA<T : Comparable<T>, M : Matrix>(val A: List<T>, val B: List<T>, private val scoringScheme: IScoringScheme, val kernelEvaluator: IStrategyKernelEvaluation<T, M>) : ISemiLocalSA {

    private val v = scoringScheme.getNormalizedMismatchScore().denominator
    private val mu = scoringScheme.getNormalizedMismatchScore().numerator
    private val m = A.size
    private val n = B.size

    private val rangeTree2D: RangeTree2D<Int>

    private fun reverseRegularizationScore(value: Double,i: Int,j: Int): Double =
        scoringScheme.getOriginalScoreFunc(value, m, i,j)

    init {

        val permMatrixExtended = kernelEvaluator.evaluate(
            A.flatMap {
                Symbol(it, SymbolType.GuardSymbol).repeatShallowCopy(mu) +
                        Symbol(it, SymbolType.AlphabetSymbol).repeatShallowCopy(v - mu)
            },
            B.flatMap {
                Symbol(it, SymbolType.GuardSymbol).repeatShallowCopy(mu) +
                        Symbol(it, SymbolType.AlphabetSymbol).repeatShallowCopy(v - mu)
            }
        )


        val size = (A.size + B.size)
        //Note ν-(sub)bistochastic matrix, there can be at most ν nonzeros in every row and every column
        val points = IntArray(size * size) { 0 } // i*n + j

//        println(A.size)
//        println(B.size)
//        println(v)
//
//        // time complexity is v(m+n)
//        println(permMatrixExtended.width())
//        println(permMatrixExtended.height())
        for (p in permMatrixExtended) {
            val posInInitialI = p.i / v;
            val posInInitialJ = p.j / v;
            points[posInInitialI * size + posInInitialJ]++
        }

        //first create actual points, then filter all zero points, and pass them to range tree
        val p =
            points.mapIndexed { index, value -> Position2D(index / size, index % size, value) }.filter { it.value != 0 }

        rangeTree2D = RangeTree2D(p)


    }

    override fun getScoringScheme()= scoringScheme


    /**
     * i from 0 to m + n
     */
    private fun canonicalDecomposition(i: Int, j: Int): Double {
        return j - (i - m) - rangeTree2D.ortoghonalQuery(
            IntervalQuery(i, m + n + 1),
            IntervalQuery(0, j - 1)
        ).toDouble() / v
    }

    override fun prefixSuffixSA(k: Int, j: Int): Double {
        if (k < 0 || k > m || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, j),m - k,j) - k //?TPDP
    }

    override fun stringSubstringSA(i: Int, j: Int): Double {
        if (i < 0 || i > n || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(i + m, j),i + m ,j)
    }

    override fun substringStringSA(k: Int, l: Int): Double {
        if (k < 0 || k > m || l < 0 || l > m) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, m + n - l),m - k, m + n - l) - m - k + l
    }

    override fun suffixPrefixSA(l: Int, i: Int): Double {
        if (l < 0 || l > m || i < 0 || i > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(i + m, m + n - l), i + m, m + n - l) - m + l
    }
}


class ExplicitSemiLocalSA<T : Comparable<T>>() : ISemiLocalSA {
    override fun getScoringScheme(): IScoringScheme {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stringSubstringSA(i: Int, j: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun prefixSuffixSA(k: Int, j: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun suffixPrefixSA(l: Int, i: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun substringStringSA(k: Int, l: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}