package longestCommonSubsequence

import sequenceAlignment.ISemiLocalData
import utils.*

class ImplicitSemiLocalLCS<T> : ISemiLocalLCS, IImplicitSemiLocalLCSSolution<T> where T : Comparable<T> {
    override val kernel: Matrix

    private lateinit var rangeTree2D: RangeTree2D<Int>
    override lateinit var pattern: List<T>
    override lateinit var text: List<T>
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
        this.pattern = a
        this.text = b
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
        return canonicalDecomposition(m - k, j) - k
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

    override fun stringSubstring(i: Int, j: Int): Double = stringSubstringLCS(i,j).toDouble()

    override fun prefixSuffix(k: Int, j: Int): Double = prefixSuffixLCS(k,j).toDouble()

    override fun suffixPrefix(l: Int, i: Int): Double = suffixPrefixLCS(l,i).toDouble()

    override fun substringString(k: Int, l: Int): Double = substringStringLCS(k,l).toDouble()

    override fun getAtPosition(i: Int, j: Int): Int = canonicalDecomposition(i, j)

    override fun print() {
        for (i in 0 until m + n + 1) {
            for (j in 0 until m + n + 1) {
                print("  ${getAtPosition(i, j)}  ")
            }
            println()
        }
    }


}

