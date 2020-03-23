package longestCommonSubsequence

import utils.Matrix
import utils.IntervalQuery
import utils.Position2D
import utils.RangeTree2D

class ImplicitSemiLocalLCS<T> : IImplicitSemiLocalLCS where T : Comparable<T> {
    override val kernel: Matrix

    private lateinit var rangeTree2D: RangeTree2D<Int>
    private lateinit var a: List<T>
    private lateinit var b: List<T>
    private var m: Int = 0
    private var n: Int = 0

    constructor(a: List<T>, b: List<T>, kernelEvaluator: IStrategyKernelEvaluation<T>) {

        kernel = kernelEvaluator.evaluate(
            a.map {
                Symbol(
                    it,
                    SymbolType.AlphabetSymbol
                )
            }, b.map {
                Symbol(
                    it,
                    SymbolType.AlphabetSymbol
                )
            })
        init(a, b)
    }

    constructor(a: List<T>, b: List<T>, kernel: Matrix) {
        this.kernel = kernel
        init(a, b)
    }

    private fun init(a: List<T>, b: List<T>) {
        this.a = a
        this.b = b
        m = a.size
        n = b.size
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

    override fun getAtPosition(i: Int, j: Int): Int = canonicalDecomposition(i, j)

}

