package beyondsemilocality

import longestCommonSubsequence.IStrategyKernelEvaluation
import sequenceAlignment.ISemiLocalSolution
import sequenceAlignment.ImplicitSemiLocalSA
import utils.IScoringScheme
import utils.Matrix
import java.lang.IllegalArgumentException

/**
 * Solution for WindowSubstring problem
 */
interface IWindowSubstringSolution<T:Comparable<T>>{
    /**
     * size of window
     */
    val windowSize:Int

    val a: List<T>

    val b: List<T>

    /**
     * @param endPosA the end index in a of specified window that start have size windowSize and ends at endPosA
     * @param endPosB the end index in b of specified window that start have size windowSize and ends at endPosB
     * @return alignment score for windows, specifically for window [endPos-windowSize,endPosA) and
     * [endPosB-windowSize,endPosB)
     */
    fun getScoreFor(endPosA:Int,endPosB: Int):Double

    /**
     * scoring scheme of alignment
     */
    fun getScoringScheme():IScoringScheme

    /**
     * return alignment plot
     */
    fun constructAlignmentPlot():Array<DoubleArray>
}


/**
 * Given strings a, b, and a window length w, the window-substring (respectively, window-window) LCS problem asks for the LCS
 * score of every w-window in string a against every substring (respectively, every w-window) in string b.
 */
interface IWindowSubstringSA<T:Comparable<T>> {
    /**
     *
     */
    fun solve(a:List<T>, b:List<T>, windowLen:Int,scoringScheme:IScoringScheme):IWindowSubstringSolution<T>
}





class WindowSubstringSolutionNaiveImplicit<T:Comparable<T>>(private var solutions: List<ISemiLocalSolution<T>>, override val windowSize: Int, override val a: List<T>, override val b: List<T>) :IWindowSubstringSolution<T>{

    private val rows = solutions.size
    private val cols = b.size + 1 - windowSize

    override fun getScoreFor(endPosA: Int, endPosB: Int):Double {
        val solutionIndexI = endPosA - windowSize
        val solutionIndexJ = endPosB - windowSize
        if(solutionIndexI < 0 || solutionIndexJ < 0) throw  IllegalArgumentException("wring endPositions")
        val rawValue = solutions[solutionIndexI].getAtPosition(windowSize+endPosB - windowSize, endPosB)
        return solutions[solutionIndexI]
                .getScoringScheme().getOriginalScoreFunc(rawValue,windowSize,windowSize+endPosB - windowSize,endPosB)

    }

    override fun getScoringScheme(): IScoringScheme = solutions.first().getScoringScheme()

    /**
     * logn^2 * n*w
     */
    override fun constructAlignmentPlot(): Array<DoubleArray> {
        val dotPlot = Array(rows) {DoubleArray(cols)}

        for (i in 0 until rows) {

            for (j in 0 until cols) {
                dotPlot[i][j] = getScoreFor(windowSize + i,j + windowSize)
            }
        }
        return dotPlot
    }
}

/**
 * TODO add description
 */
class WindowSubstringSANaiveImplicit<T:Comparable<T>,M:Matrix>(val kernelEvaluator: IStrategyKernelEvaluation<T,M>):IWindowSubstringSA<T>{
    override fun solve(a: List<T>, b: List<T>, windowLen: Int, scoringScheme: IScoringScheme): IWindowSubstringSolution<T> {
        if (windowLen > a.size || windowLen > b.size) throw IllegalArgumentException("The window is greater then lists sizes")

        val res = (windowLen ..  a.size).map { endPos->
            val subListA = a.subList(endPos - windowLen, endPos)
            ImplicitSemiLocalSA(subListA, b, scoringScheme, kernelEvaluator)
        }.toMutableList()
        return WindowSubstringSolutionNaiveImplicit(res, windowLen, a, b)
    }

}