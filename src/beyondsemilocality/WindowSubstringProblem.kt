package beyondsemilocality

import longestCommonSubsequence.*
import sequenceAlignment.ISemiLocalCombined
import sequenceAlignment.ISemiLocalFastAccess
import sequenceAlignment.ISemiLocalSolution
import sequenceAlignment.ImplicitSemiLocalSA
import utils.*
import java.lang.IllegalArgumentException
import kotlin.math.log2
import kotlin.math.pow


/**
 */
interface IFragmentSubstringProvider<T : Comparable<T>> {
    /**
     *
     */
    val a: List<T>

    /**
     *
     */
    val b: List<T>

    /**
     *
     */
    val scoringScheme: IScoringScheme

    /**
     *
     */
    fun getSolutionFor(fragmentStart: Int, fragmentEnd: Int): ISemiLocalCombined<T>
}

/**
 * provides semi-local lcs solution for specific fragment
 */
class ImplicitFragmentSubstringLCSProvider<T : Comparable<T>> : IFragmentSubstringProvider<T> {

    override val a: List<T>
    override val b: List<T>
    override val scoringScheme = LCSScoringScheme()

    private val arr: Array<Array<Matrix>>

    constructor(a: List<T>, b: List<T>) {
        this.a = a
        this.b = b

        val m = a.size.toDouble()
        val depth = log2(m).toInt()
        val s = 2.toDouble().pow(depth).toInt()

        this.arr =
            Array(depth + 1) { i -> Array((m / 2.0.pow(i.toDouble()).toInt()).toInt()) { dummyPermutationMatrixTwoLists } } as Array<Array<Matrix>>
        // i.e precalc
        canonicalSWindows(a, b, arr, s, depth)
    }

    constructor(a: List<T>, b: List<T>, precalc: Array<Array<Matrix>>) {
        this.a = a
        this.b = b
        this.arr = precalc
    }

    override fun getSolutionFor(fragmentStart: Int, fragmentEnd: Int): ISemiLocalCombined<T> {
        return ImplicitSemiLocalSA(
            ImplicitSemiLocalLCS(
                a.subList(fragmentStart, fragmentEnd),
                b,
                secondPhrase(fragmentStart, fragmentEnd, arr, 1, 0, fragmentEnd - fragmentStart, this.b.size)
            )
        )
    }

    /**
     * compute all (s,t) strings with  s = 1 and t = w as first call
     * no extra
     */
    private fun secondPhrase(k: Int, l: Int, arr: Array<Array<Matrix>>, s: Int, access: Int, t: Int, n: Int): Matrix {
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

}

/**
 * Solution for WindowSubstring problem
 */
interface IWindowSubstringSolution<T : Comparable<T>> {
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
    fun getSolutions(solutionNumber: Int): List<ISemiLocalCombined<T>>


    /**
     * scoring scheme of alignment
     */
    fun getScoringScheme(): IScoringScheme

    /**
     * return alignment plot
     */
    fun constructAlignmentPlot(): Array<DoubleArray>
}


interface IFragmentSubstringSolution<T : Comparable<T>> {

    fun getSolution(solutionNumber: Int): ISemiLocalCombined<T>

    fun getSolutions(): List<ISemiLocalCombined<T>>
}

class FragmentSubstringSolution<T:Comparable<T>>
    (a:List<T>, b:List<T>,val sol:List<ISemiLocalCombined<T>>):IFragmentSubstringSolution<T>{
    override fun getSolution(solutionNumber: Int): ISemiLocalCombined<T> = sol[solutionNumber]

    override fun getSolutions(): List<ISemiLocalCombined<T>> = sol
}

/**
 * Given strings a, b, and a window length w, the window-substring (respectively, window-window) SA problem asks for the SA
 * score of every w-window in string a against every substring (respectively, every w-window) in string b.
 */
interface IWindowSubstringSA<T : Comparable<T>> {
    /**
     *
     */
    fun solve(a: List<T>, b: List<T>, w: Int, scoringScheme: IScoringScheme): IWindowSubstringSolution<T>
}


/**
 * Given strings a, b, and a r fragment intervals in a, the fragment-substring  SA problem asks for the SA
 * score of every fragment in string a against every substring  in string b.
 */
interface IFragmentSubstringSA<T : Comparable<T>> {
    /**
     *
     */
    fun solve(a: List<T>, b: List<T>, fragments:List<Interval>, scoringScheme: IScoringScheme): IFragmentSubstringSolution<T>
}

//TODO убрать схему оценки
class ImplicitFragmentSubstringLCS<T:Comparable<T>>:IFragmentSubstringSA<T> {
    override fun solve(a: List<T>, b: List<T>, fragments:List<Interval>, scoringScheme: IScoringScheme): IFragmentSubstringSolution<T> {
        val provider = ImplicitFragmentSubstringLCSProvider(a,b)
        return FragmentSubstringSolution(a,b,
            fragments.map { interval -> provider.getSolutionFor(interval.startInclusive,interval.endExclusive) })
    }
}

class WindowSubstringSolutionImplicit<T : Comparable<T>>(
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
        val rawValue = solutions[solutionIndexI].getAtPosition(w + endPosB - w, endPosB)
        return solutions[solutionIndexI]
            .getScoringScheme().getOriginalScoreFunc(rawValue, w, w + endPosB - w, endPosB)

    }

    override fun getSolution(windowNumber: Int): ISemiLocalCombined<T> = solutions[windowNumber]

    override fun getSolutions(solutionNumber: Int): List<ISemiLocalCombined<T>> = solutions

    override fun getScoringScheme(): IScoringScheme = solutions.first().getScoringScheme()

    /**
     * O(nm)
     */
    override fun constructAlignmentPlot(): Array<DoubleArray> {
        val dotPlot = Array(rows) { DoubleArray(cols) }
        val w = w
//        for (sol in solutions){
//            sol.ma
//        }


        for (i in 0 until rows) {
            val sol = solutions[i]
            val scheme = sol.getScoringScheme()
            var internalI = w
            var internalJ = w
            var rawScore = sol.getAtPosition(internalI, internalJ)

            dotPlot[i][0] = scheme.getOriginalScoreFunc(rawScore, w, internalI, internalJ)

            for (j in 1 until cols) {
                rawScore = sol.nextInRow(internalI, internalJ, rawScore, ISemiLocalFastAccess.Direction.Forward)
                internalJ++
                rawScore = sol.nextInCol(internalI, internalJ, rawScore, ISemiLocalFastAccess.Direction.Forward)
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
        w: Int,
        scoringScheme: IScoringScheme
    ): IWindowSubstringSolution<T> {
        if (w > a.size || w > b.size) throw IllegalArgumentException("The window is greater then lists sizes")

        val res = (w..a.size).map { endPos ->
            val subListA = a.subList(endPos - w, endPos)
            ImplicitSemiLocalSA(subListA, b, scoringScheme, kernelEvaluator)
        }.toMutableList()
        return WindowSubstringSolutionImplicit(res, w, a, b)
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

    override fun solve(a: List<T>, b: List<T>, w: Int, scoringScheme: IScoringScheme): IWindowSubstringSolution<T> {
        val provider = ImplicitFragmentSubstringLCSProvider(a, b)

        return WindowSubstringSolutionImplicit((0..a.size - w).map { index ->
            provider.getSolutionFor(index, index + w)
        }.toMutableList(), w, a, b)
    }
}

class WindowSubstringSAImplicit<T : Comparable<T>> : IWindowSubstringSA<T> {


    override fun solve(a: List<T>, b: List<T>, w: Int, scoringScheme: IScoringScheme): IWindowSubstringSolution<T> {

        if(scoringScheme is LCSScoringScheme) return WindowSubstringLCSImplicit<T>().solve(a, b, w, scoringScheme)

        var v = 0
        var mu = 0

        val extA = a.flatMap {
            Symbol(
                it,
                SymbolType.GuardSymbol
            ).repeatShallowCopy(mu) +
                    Symbol(
                        it,
                        SymbolType.AlphabetSymbol
                    ).repeatShallowCopy(v - mu)
        }.toMutableList()

        val extB = b.flatMap {
            Symbol(
                it,
                SymbolType.GuardSymbol
            ).repeatShallowCopy(mu) +
                    Symbol(
                        it,
                        SymbolType.AlphabetSymbol
                    ).repeatShallowCopy(v - mu)
        }.toMutableList()
        val extW = w * v
        TODO()
//        WindowSubstringLCSImplicit<Symbol<T>>().solve(extA,extB,extW,scoringScheme)
//
//
//                v = scoringScheme.getMismatchScore().numerator
//                mu = scoringScheme.getMismatchScore().denominator
//                WindowSubstringLCSImplicit<T>()
//                    .solve(a.map { mutableListOf<T>() }
//                    ,b,w * v, scoringScheme)
//
//            }
//
//        }



//        TODO()
//        val newA = TODO()
//        val newB = TODO()
//        val newWinLen = TODO()
//
//        return WindowSubstringSolutionImplicit<T>(getSoluitions(), w, a, b)
    }

    private fun getSoluitions(): List<ISemiLocalSolution<T>> {
        TODO()
    }
}


