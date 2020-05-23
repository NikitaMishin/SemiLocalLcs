package longestCommonSubsequence

import sequenceAlignment.ISemiLocalCombined
import sequenceAlignment.ISemiLocalFastAccess
import utils.*

//ISemiLocalCombined
class ImplicitSemiLocalLCS<T> : ISemiLocalCombined<T> {
    val kernel: Matrix

    private lateinit var rangeTree2D: RangeTree2D<Int>
    override lateinit var pattern: List<T>
    override lateinit var text: List<T>

    private val scoringScheme = LCSScoringScheme()


    private var m: Int = 0
    private var n: Int = 0

    constructor(a: List<T>, b: List<T>, kernelEvaluator: IStrategyKernelEvaluation) {
        kernel = kernelEvaluator.evaluate(a, b)
        init(a, b)
    }

//    constructor(a: List<T>, b: List<T>, kernel: Matrix) {
//        this.kernel = kernel
//        init(a, b)
//    }

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

    fun prefixSuffixLCS(k: Int, j: Int): Int {
        if (k < 0 || k > m || j < 0 || j > n) return -1
        return canonicalDecomposition(m - k, j) - k
    }

    fun stringSubstringLCS(i: Int, j: Int): Int {
        if (i < 0 || i > n || j < 0 || j > n) return -1
        return canonicalDecomposition(i + m, j)
    }

    fun substringStringLCS(k: Int, l: Int): Int {
        if (k < 0 || k > m || l < 0 || l > m) return -1
        return canonicalDecomposition(m - k, m + n - l) - m - k + l
    }

    fun suffixPrefixLCS(l: Int, i: Int): Int {
        if (l < 0 || l > m || i < 0 || i > n) return -1
        return canonicalDecomposition(i + m, m + n - l) - m + l
    }

    override fun stringSubstring(i: Int, j: Int): Double = stringSubstringLCS(i, j).toDouble()

    override fun prefixSuffix(k: Int, j: Int): Double = prefixSuffixLCS(k, j).toDouble()

    override fun suffixPrefix(l: Int, i: Int): Double = suffixPrefixLCS(l, i).toDouble()

    override fun substringString(k: Int, l: Int): Double = substringStringLCS(k, l).toDouble()

    override fun getAtPosition(i: Int, j: Int): Double = canonicalDecomposition(i, j).toDouble()

    override fun getScoringScheme(): IScoringScheme = scoringScheme

    private val countingQuerySA = CountingQuerySA()


    override fun nextInCol(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> j - (i + 1 - m) - countingQuerySA.dominanceSumTopRightDownMove(
                i,
                j,
                j - i + m - rawValue,
                kernel
            )
            ISemiLocalFastAccess.Direction.BackWard -> j - (i - 1 - m) - countingQuerySA.dominanceSumTopRightUpMove(
                i,
                j,
                j - i + m - rawValue,
                kernel
            )
        }


    override fun nextInRow(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> j + 1 - (i - m) - countingQuerySA.dominanceSumTopRightRightMove(
                i,
                j,
                j - i + m - rawValue,
                kernel
            )
            ISemiLocalFastAccess.Direction.BackWard -> j - 1 - (i - m) - countingQuerySA.dominanceSumTopRightLeftMove(
                i,
                j,
                j - i + m - rawValue,
                kernel
            )
        }


    override fun print() {
        for (i in 0 until m + n + 1) {
            for (j in 0 until m + n + 1) {
                print("  ${getAtPosition(i, j)}  ")
            }
            println()
        }
    }

    override fun getMatrix(): AbstractMongeMatrix {
        val mongeMatrix = MongeMatrix(m + n + 1, m + n + 1)
        for (i in 0 until m + n + 1) {
            for (j in 0 until m + n + 1) {
                mongeMatrix[i, j] = getAtPosition(i, j)
            }

        }
        return mongeMatrix
    }


}

