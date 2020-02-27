import utils.IntervalQuery
import utils.Position2D
import utils.RangeTree2D

class ImplicitSemiLocalLCS<T, M : Matrix>(val a: List<T>, val b: List<T>, kernelEvaluator: IStrategyKernelEvaluation<T, M>) : IImplicitSemiLocalLCS where T : Comparable<T> {

    private val m = a.size
    private val n = b.size

    private var kernel: Matrix = kernelEvaluator.evaluate(
        a.map { Symbol(it,SymbolType.AlphabetSymbol) }, b.map { Symbol(it,SymbolType.AlphabetSymbol) })
    private var rangeTree2D: RangeTree2D<Int>

    init {
        val mutableList = mutableListOf<Position2D<Int>>()
        for (p in kernel) mutableList.add(p)
        rangeTree2D = RangeTree2D(mutableList)
    }

    /**
     * i from 0 to m + n
     */
    private fun canonicalDecomposition(i: Int, j: Int): Int {
        return j - (i - m) - rangeTree2D.ortoghonalQuery(
            IntervalQuery(i, m + n + 1),
            IntervalQuery(0, j - 1)
        )
    }

    override fun prefixSuffixLCS(k: Int, j: Int): Int {
        if (k < 0 || k > m || j < 0 || j > n) return -1
        return canonicalDecomposition(m - k, j) - k //?TPDP
    }

    override fun stringSubstringLCS(i: Int, j: Int): Int {
        if (i < 0 || i > n || j < 0 || j > n) return -1
        return canonicalDecomposition(i + m, j)
    }

    override fun substringStringLCS(k: Int, l: Int): Int {
        if (k < 0 || k > m || l < 0 || l > m) return -1
        return canonicalDecomposition(m - k, m + n - l) - m - k + l
    }

    override fun suffixPrefixLCS(l: Int, i: Int): Int {
        if (l < 0 || l > m || i < 0 || i > n) return -1
        return canonicalDecomposition(i + m, m + n - l) - m + l
    }

    override fun getAtPosition(i: Int, j: Int): Int = canonicalDecomposition(i,j)

}

