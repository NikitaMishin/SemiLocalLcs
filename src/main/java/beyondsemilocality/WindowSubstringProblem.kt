package beyondsemilocality


import longestCommonSubsequence.*
import sequenceAlignment.ISemiLocalCombined
import sequenceAlignment.ISemiLocalFastAccess
import sequenceAlignment.ImplicitSemiLocalSA
import utils.*
import java.lang.IllegalArgumentException


/**
 * Given strings a, b, and a window length w, the window-substring (respectively, window-window) SA problem asks for the SA
 * score of every w-window in string a against every substring (respectively, every w-window) in string b.
 */
interface IWindowSubstringSA<T> {
    /**
     *
     */
    fun solve(a: List<T>, b: List<T>, w: Int, scoringScheme: IScoringScheme): IWindowSubstringSolution<T>
}

/**
 * Interface for accessing solution of  WindowSubstring problem
 */
interface IWindowSubstringSolution<T> {
    /**
     * size of window
     */
    val w: Int

    /**
     *
     */
    val a: List<T>

    /**
     *
     */
    val b: List<T>

    /**
     * @param endPosA the end index in a of specified window that start have size windowSize and ends at endPosA
     * @param endPosB the end index in b of specified window that start have size windowSize and ends at endPosB
     * @return alignment score for windows, specifically for window [endPos-windowSize,endPosA) and
     * [endPosB-windowSize,endPosB)
     */
    fun getWindowScoreFor(endPosA: Int, endPosB: Int): Double

    /**
     *
     */
    fun getSolution(windowNumber: Int): ISemiLocalCombined<T>

    /**
     *
     */
    fun getSolutions(): List<ISemiLocalCombined<T>>


    /**
     * scoring scheme of alignment
     */
    fun getScoringScheme(): IScoringScheme

    /**
     * return alignment plot
     */
    fun constructAlignmentPlot(): Array<DoubleArray>
}


class WindowSubstringSolution<T>(
    private var solutions: List<ISemiLocalCombined<T>>,
    override val w: Int,
    override val a: List<T>,
    override val b: List<T>
) : IWindowSubstringSolution<T> {


    private val rows = solutions.size
    private val cols = b.size + 1 - w

    override fun getWindowScoreFor(endPosA: Int, endPosB: Int): Double {
        val solutionIndexI = endPosA - w
        val solutionIndexJ = endPosB - w
        if (solutionIndexI < 0 || solutionIndexJ < 0) throw  IllegalArgumentException("wring endPositions")
        val rawValue = solutions[solutionIndexI].getAtPosition(endPosB - w + w, endPosB)
        return solutions[solutionIndexI]
            .getScoringScheme().getOriginalScoreFunc(rawValue, w, endPosB - w, endPosB)

    }

    override fun getSolution(windowNumber: Int): ISemiLocalCombined<T> = solutions[windowNumber]

    override fun getSolutions(): List<ISemiLocalCombined<T>> = solutions

    override fun getScoringScheme(): IScoringScheme = solutions.first().getScoringScheme()

    /**
     * O(nm)
     */
    override fun constructAlignmentPlot(): Array<DoubleArray> {
        val dotPlot = Array(rows) { DoubleArray(cols) }
        val w = w



        for (i in 0 until rows) {
            val sol = solutions[i]
            val scheme = sol.getScoringScheme()
            var internalI = w
            var internalJ = w
            var rawScore = sol.getAtPosition(internalI, internalJ)

            dotPlot[i][0] = scheme.getOriginalScoreFunc(rawScore, w, internalI - w, internalJ)

            for (j in 1 until cols) {
                rawScore = sol.nextInRow(internalI, internalJ, rawScore, ISemiLocalFastAccess.Direction.Forward)
                internalJ++
                rawScore = sol.nextInCol(internalI, internalJ, rawScore, ISemiLocalFastAccess.Direction.Forward)
                internalI++

                dotPlot[i][j] = scheme.getOriginalScoreFunc(rawScore, w, internalI - w, internalJ)
            }
        }
        return dotPlot
    }
}

/**
 * TODO add description
 */
class WindowSubstringSANaiveImplicit<T>(val kernelEvaluator: IStrategyKernelEvaluation) :
    IWindowSubstringSA<T> {
    override fun solve(
        a: List<T>,
        b: List<T>,
        w: Int,
        scoringScheme: IScoringScheme
    ): IWindowSubstringSolution<T> {
        if (w > a.size || w > b.size) {
            println(w)
            println(a.size)
            println(b.size)
            throw IllegalArgumentException("The window is greater then lists sizes")
        }

        val res = (w..a.size).map { endPos ->
            val subListA = a.subList(endPos - w, endPos)
            ImplicitSemiLocalSA(subListA, b, scoringScheme, kernelEvaluator)
        }.toMutableList()
        return WindowSubstringSolution(res, w, a, b)
    }
}


class WindowSubstringSA<T>(private var provider: IFragmentSubstringProvider<T>) : IWindowSubstringSA<T> {
    override fun solve(a: List<T>, b: List<T>, w: Int, scoringScheme: IScoringScheme): IWindowSubstringSolution<T> =
        WindowSubstringSolution((0..a.size - w).map { index -> provider.getSolutionFor(index, index + w) }
            .toMutableList(), w, a, b)
}

