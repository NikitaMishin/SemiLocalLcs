package beyondsemilocality

import longestCommonSubsequence.*
import sequenceAlignment.ISemiLocalSolution
import sequenceAlignment.ImplicitSemiLocalSA
import utils.IScoringScheme
import utils.Matrix
import utils.permutationMatrixTwoListsInstaneProvider
import java.lang.IllegalArgumentException
import kotlin.math.log2
import kotlin.math.pow

/**
 * Solution for WindowSubstring problem
 */
interface IWindowSubstringSolution<T : Comparable<T>> {
    /**
     * size of window
     */
    val windowSize: Int

    val a: List<T>

    val b: List<T>

    /**
     * @param endPosA the end index in a of specified window that start have size windowSize and ends at endPosA
     * @param endPosB the end index in b of specified window that start have size windowSize and ends at endPosB
     * @return alignment score for windows, specifically for window [endPos-windowSize,endPosA) and
     * [endPosB-windowSize,endPosB)
     */
    fun getScoreFor(endPosA: Int, endPosB: Int): Double

    /**
     * scoring scheme of alignment
     */
    fun getScoringScheme(): IScoringScheme

    /**
     * return alignment plot
     */
    fun constructAlignmentPlot(): Array<DoubleArray>
}


/**
 * Given strings a, b, and a window length w, the window-substring (respectively, window-window) LCS problem asks for the LCS
 * score of every w-window in string a against every substring (respectively, every w-window) in string b.
 */
interface IWindowSubstringSA<T : Comparable<T>> {
    /**
     *
     */
    fun solve(a: List<T>, b: List<T>, windowLen: Int, scoringScheme: IScoringScheme): IWindowSubstringSolution<T>
}


//class Tmp{
//    constructor(f:Int){
//    }
//    constructor(g:String)
//}
// fun f (){
//     val  t = Tmp()
// }

class WindowSubstringSolutionImplicit<T : Comparable<T>>(
    private var solutions: List<ISemiLocalSolution<T>>,
    override val windowSize: Int,
    override val a: List<T>,
    override val b: List<T>
) : IWindowSubstringSolution<T> {


    private val rows = solutions.size
    private val cols = b.size + 1 - windowSize

    override fun getScoreFor(endPosA: Int, endPosB: Int): Double {
        val solutionIndexI = endPosA - windowSize
        val solutionIndexJ = endPosB - windowSize
        if (solutionIndexI < 0 || solutionIndexJ < 0) throw  IllegalArgumentException("wring endPositions")
        val rawValue = solutions[solutionIndexI].getAtPosition(windowSize + endPosB - windowSize, endPosB)
        return solutions[solutionIndexI]
            .getScoringScheme().getOriginalScoreFunc(rawValue, windowSize, windowSize + endPosB - windowSize, endPosB)

    }

    override fun getScoringScheme(): IScoringScheme = solutions.first().getScoringScheme()

    /**
     * O(nm)
     */
    override fun constructAlignmentPlot(): Array<DoubleArray> {
        val dotPlot = Array(rows) { DoubleArray(cols) }
        val w = windowSize

        for (i in 0 until rows) {
            val sol = solutions[i]
            val scheme = sol.getScoringScheme()
            var internalI = w
            var internalJ = w
            var rawScore = sol.getAtPosition(internalI, internalJ)

            dotPlot[i][0] = scheme.getOriginalScoreFunc(rawScore, w, internalI, internalJ)

            for (j in 1 until cols) {
                rawScore = sol.nextInRow(internalI, internalJ, rawScore, ISemiLocalSolution.Direction.Forward)
                internalJ++
                rawScore = sol.nextInCol(internalI, internalJ, rawScore, ISemiLocalSolution.Direction.Forward)
                internalI++

                dotPlot[i][j] = scheme.getOriginalScoreFunc(rawScore, w, internalI, internalJ)
            }
        }
        return dotPlot
    }
}

/**
 * TODO add description
 */
class WindowSubstringSANaiveImplicit<T : Comparable<T>>(val kernelEvaluator: IStrategyKernelEvaluation<T>) :
    IWindowSubstringSA<T> {
    override fun solve(
        a: List<T>,
        b: List<T>,
        windowLen: Int,
        scoringScheme: IScoringScheme
    ): IWindowSubstringSolution<T> {
        if (windowLen > a.size || windowLen > b.size) throw IllegalArgumentException("The window is greater then lists sizes")

        val res = (windowLen..a.size).map { endPos ->
            val subListA = a.subList(endPos - windowLen, endPos)
            ImplicitSemiLocalSA(subListA, b, scoringScheme, kernelEvaluator)
        }.toMutableList()
        return WindowSubstringSolutionImplicit(res, windowLen, a, b)
    }
}


/**
 * Compute recursively all (s,1)- substring for a fixed s, 1<=s<=m
 * Call this with s:=m, where m  is length of the first string
 * and fill matrix triangular
 * 0->m elements
 * 1->m/2 elements
 * 2->m/4
 * m elements ....
 * можено выделить 4m элементов  одномерный массив и внутренняя индексация
 * arr indexed by s is degree
 * TODO add provider for type of multiplication depends on when we calculate string substring or whole
 * now for whole
 */
fun <T : Comparable<T>> canonicalSWindows(a: List<T>, b: List<T>, arr: Array<Array<Matrix>>, s: Int, depth: Int) {

    if (s == 1) {
        //base case
        for (elem in a.withIndex()) {
            arr[depth][elem.index] =
                ImplicitSemiLocalLCS(
                    mutableListOf(elem.value),
                    b,
                    ReducingKernelEvaluation(::permutationMatrixTwoListsInstaneProvider)
                ).kernel
        }
        return
    }
    canonicalSWindows(a, b, arr, s / 2, depth - 1)
    for (atThisLevel in 0 until a.size / s) {
        arr[depth][atThisLevel] =
            staggeredStickyMultiplication(arr[depth - 1][atThisLevel * 2], arr[depth - 1][atThisLevel * 2 + 1], b.size)
    }
}


class WindowSubstringLCSImplicit<T : Comparable<T>> : IWindowSubstringSA<T> {


    /**
     * compute all (s,t) strings with  s = 1 and t = w as first call
     * no extra
     */
     fun secondPhrase(k: Int, l: Int, arr: Array<Array<Matrix>>, s: Int, access: Int, t: Int, n: Int): Matrix {
        if (t == 1) {
            return arr[access][k / s];
        }
        return when {
            t % 2 == 0 -> {
                staggeredStickyMultiplication(
                    secondPhrase(k, l - s, arr, s, access, t - 1, n),
                    arr[access][(l - s) / s],
                    n
                )
            } //note k already by s
            k / s % 2 == 0 -> {
                staggeredStickyMultiplication(
                    secondPhrase(k, l - s, arr, 2 * s, access + 1, (t - 1) / 2, n),
                    arr[access][(l - s) / s],
                    n
                )
            }
            else -> {
                staggeredStickyMultiplication(
                    arr[access][k / s],
                    secondPhrase(k + s, l, arr, 2 * s, access + 1, (t - 1) / 2, n),
                    n
                )
            }
        }

    }

    override fun solve(
        a: List<T>,
        b: List<T>,
        windowLen: Int,
        scoringScheme: IScoringScheme
    ): IWindowSubstringSolution<T> {
//        val m = a.size
//        val depth = log2(m.toDouble()).toInt().toDouble().toInt()
//        val s = 2.toDouble().pow(depth).toInt()
//        canonicalSWindows(a, b, arr, s, depth)
//
//        return WindowSubstringSolutionImplicit(
//        (0..m - windowLen).map { index ->
//            ImplicitSemiLocalLCS(
//                a.subList(index * windowLen, index * (windowLen + 1)),
//                b,
//                secondPhrase(index * windowLen, index * (windowLen + 1), arr, 1, 0, windowLen, b.size)
//            )
//        }.toMutableList()
//        )
TODO()
    }
}

//TODO same as window just call with different windows size  
class FragmentSubstinng(){

}