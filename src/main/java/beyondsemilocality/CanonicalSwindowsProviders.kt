package beyondsemilocality

import longestCommonSubsequence.*
import utils.IScoringScheme
import utils.Matrix
import utils.dummyPermutationMatrixTwoLists
import utils.permutationMatrixTwoListsInstaneProvider
import kotlin.math.log2
import kotlin.math.pow


/**
 * Provides function to calculate canonical s-windows decomposition for lcs scoring scheme.
 * Note that this implementation is for implicit calculation of braid multiplication
 */
class ImplicitCanonicalSWindowsLCSProvider {
    fun <T> canonicalSWindowsImplicit(a: List<T>, b: List<T>): Array<Array<Matrix>> {

        val m = a.size.toDouble()
        val depth = log2(m).toInt()
        val s = 2.toDouble().pow(depth).toInt()

        val arr =
            Array(depth + 1) { i ->
                Array((m / 2.0.pow(i.toDouble()).toInt()).toInt())
                { dummyPermutationMatrixTwoLists }
            } as Array<Array<Matrix>>
        // i.e precalc
        this.canonicalSWindowsImplicit(a, b, arr, s, depth)
        return arr
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
    private fun <T> canonicalSWindowsImplicit(a: List<T>, b: List<T>, arr: Array<Array<Matrix>>, s: Int, depth: Int) {

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
        canonicalSWindowsImplicit(a, b, arr, s / 2, depth - 1)
        for (atThisLevel in 0 until a.size / s) {
            arr[depth][atThisLevel] =
                staggeredStickyMultiplication(
                    arr[depth - 1][atThisLevel * 2],
                    arr[depth - 1][atThisLevel * 2 + 1],
                    b.size
                )
        }
    }
}


/**
 * Provides function to calculate canonical s-windows decomposition for specified scoring scheme.
 * Note that this implementation is for explicit calculation of braid multiplication
 */
class ExplicitCanonicalSWindowsProvider(var scoringScheme: IScoringScheme) {
    fun <T> canonicalSWindows(a: List<T>, b: List<T>): Array<Array<AbstractMongeMatrix>> {
        val m = a.size.toDouble()
        val depth = log2(m).toInt()
        val s = 2.toDouble().pow(depth).toInt()

        val dummyMatrix = MongeMatrix(1, 1)
        val arr =
            Array(depth + 1)
            { i ->
                Array(
                    (m / 2.0.pow(i.toDouble()).toInt()).toInt()
                ) { dummyMatrix }
            } as Array<Array<AbstractMongeMatrix>>

        canonicalSWindowsExplicit(a, b, arr, s, depth)
        return arr
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
    private fun <T> canonicalSWindowsExplicit(
        a: List<T>,
        b: List<T>,
        arr: Array<Array<AbstractMongeMatrix>>,
        s: Int,
        depth: Int
    ) {
        if (s == 1) {
            //base case
            for (elem in a.withIndex()) {
                val res = ExplicitKernelEvaluation(scoringScheme).evaluate(mutableListOf(elem.value), b)
                arr[depth][elem.index] = res
            }
        } else {
            canonicalSWindowsExplicit(a, b, arr, s / 2, depth - 1)
            for (atThisLevel in 0 until a.size / s) {
                arr[depth][atThisLevel] =
                    staggeredExplicitMultiplication(
                        arr[depth - 1][atThisLevel * 2],
                        arr[depth - 1][atThisLevel * 2 + 1],
                        b.size
                    )
            }
        }
    }

}

