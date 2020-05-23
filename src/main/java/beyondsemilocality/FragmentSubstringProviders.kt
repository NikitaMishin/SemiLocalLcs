package beyondsemilocality

import longestCommonSubsequence.*
import sequenceAlignment.ExplicitSemiLocalSA
import sequenceAlignment.ISemiLocalCombined
import sequenceAlignment.ImplicitSemiLocalSA
import utils.IScoringScheme
import utils.Matrix


/**
 * Provides interface for fragment-substring solutions
 */
interface IFragmentSubstringProvider<T> {
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
     * Given fragment interval return SemiLocal solution for given fragment against b
     * @param fragmentStart inclusive
     * @param fragmentEnd exclusive
     * @return
     */
    fun getSolutionFor(fragmentStart: Int, fragmentEnd: Int): ISemiLocalCombined<T>
}


class ExplicitFragmentSubstringProvider<T> : IFragmentSubstringProvider<T> {
    override val a: List<T>
    override val b: List<T>
    override val scoringScheme: IScoringScheme

    private val arr: Array<Array<AbstractMongeMatrix>>

    constructor(a: List<T>, b: List<T>, scoringScheme: IScoringScheme) {
        this.a = a
        this.b = b
        this.scoringScheme = scoringScheme
        this.arr = ExplicitCanonicalSWindowsProvider(scoringScheme).canonicalSWindows(a, b)
    }


    override fun getSolutionFor(fragmentStart: Int, fragmentEnd: Int): ISemiLocalCombined<T> {
        val matrix = secondPhrase(fragmentStart, fragmentEnd, 1, 0, fragmentEnd - fragmentStart, this.b.size)
        for (i in 0 until matrix.height()) {
            for (j in 0 until matrix.width()) {
                matrix[i, j] = j - (i - (fragmentEnd - fragmentStart)) - matrix[i, j]
            }
        }
        return ExplicitSemiLocalSA(
            a.subList(fragmentStart, fragmentEnd),
            b,
            scoringScheme,
            matrix
        )
    }


    /**
     * compute all (s,t) strings with  s = 1 and t = w as first call
     * no extra
     */
    private fun secondPhrase(k: Int, l: Int, s: Int, access: Int, t: Int, n: Int): AbstractMongeMatrix {
        if (t == 1) {
            return arr[access][k / s];
        }
        return when {
            t % 2 == 0 -> {
                staggeredExplicitMultiplication(
                    secondPhrase(k, l - s, s, access, t - 1, n),
                    arr[access][(l - s) / s],
                    n
                )
            } //note k already by s
            k / s % 2 == 0 -> {
                staggeredExplicitMultiplication(
                    secondPhrase(k, l - s, 2 * s, access + 1, (t - 1) / 2, n),
                    arr[access][(l - s) / s],
                    n
                )
            }
            else -> {
                staggeredExplicitMultiplication(
                    arr[access][k / s],
                    secondPhrase(k + s, l, 2 * s, access + 1, (t - 1) / 2, n),
                    n
                )
            }
        }

    }
}


/**
 * provides semi-local lcs solution for specific fragment
 */
class ImplicitFragmentSubstringProvider<T> : IFragmentSubstringProvider<T> {

    override val a: List<T>
    override val b: List<T>
    override val scoringScheme: IScoringScheme

    private val arr: Array<Array<Matrix>>

    constructor(a: List<T>, b: List<T>, scoringScheme: IScoringScheme) {
        this.a = a
        this.b = b
        this.scoringScheme = scoringScheme

        // blow-up technique
        val mismatch = scoringScheme.getNormalizedMismatchScore()
        val aExtended = blowUp(mismatch.numerator, mismatch.denominator, a)
        val bExtended = blowUp(mismatch.numerator, mismatch.denominator, b)
        this.arr = ImplicitCanonicalSWindowsLCSProvider().canonicalSWindowsImplicit(aExtended, bExtended)
    }

    constructor(a: List<T>, b: List<T>, precalc: Array<Array<Matrix>>, scoringScheme: IScoringScheme) {
        this.a = a
        this.b = b
        this.arr = precalc
        this.scoringScheme = scoringScheme
    }

    override fun getSolutionFor(fragmentStart: Int, fragmentEnd: Int): ISemiLocalCombined<T> {
        return ImplicitSemiLocalSA(
            a.subList(fragmentStart, fragmentEnd),
            b,
            scoringScheme,
            getMatrixSolution(fragmentStart, fragmentEnd)
        )
    }


    fun getMatrixSolution(fragmentStart: Int, fragmentEnd: Int): Matrix {
        val v = scoringScheme.getNormalizedMismatchScore().denominator
        return secondPhrase(
            fragmentStart * v,
            fragmentEnd * v,
            arr,
            1,
            0,
            v * (fragmentEnd - fragmentStart),
            v * this.b.size
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

    private fun blowUp(mu: Int, v: Int, lst: List<T>) =
        lst.flatMap {
            Symbol(it, SymbolType.GuardSymbol).repeatShallowCopy(mu) +
                    Symbol(it, SymbolType.AlphabetSymbol).repeatShallowCopy(v - mu)
        }.toMutableList()


}

