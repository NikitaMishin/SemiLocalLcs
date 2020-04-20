package longestCommonSubsequence

import sequenceAlignment.ISemiLocalPrefixSuffixProblem
import sequenceAlignment.ISemiLocalStringSubstringProblem
import sequenceAlignment.ISemiLocalSubstringStringProblem
import sequenceAlignment.ISemiLocalSuffixPrefixProblem
import utils.IScoringScheme
import utils.Matrix
import kotlin.math.max
import kotlin.random.Random

/**
 *longestCommonSubsequence.Symbol type refer to longestCommonSubsequence.Symbol
 */
enum class SymbolType {
    AlphabetSymbol,
    WildCardSymbol,// '?' - symbol not presented in alphabet that mathes everyother
    GuardSymbol,// '$'- symbol not presented in alphabet that mathes only with other gard TODO what if guard in one side and ? in ohter
    //...
}

/**
 * longestCommonSubsequence.Symbol is extended alphabet for semiLocalLCS
 */
data class Symbol<T>(val symbol: T, val type: SymbolType) {
    override fun equals(other: Any?): Boolean {
        if (other !is Symbol<*>) return false // todo bad kotlin type erasure
        if (this.type == SymbolType.WildCardSymbol || other.type == SymbolType.WildCardSymbol ||
            (this.type == SymbolType.AlphabetSymbol && other.type == SymbolType.AlphabetSymbol && this.symbol == other.symbol) ||
            this.type == SymbolType.GuardSymbol && other.type == SymbolType.GuardSymbol
        ) return true
        return false
    }

    fun repeatShallowCopy(times: Int): List<Symbol<T>> = (0 until times).map { this }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

}

/**
 *  @param  matrixInstance to get type M (see type erasure)
 */
class RecursiveKernelEvaluation<M : Matrix>(matrixInstance: () -> M) :
    IStrategyKernelEvaluation {
    private val instance = matrixInstance().createZeroMatrix(0, 0)

    /**
     * The recursive algorithm based on steady ant braid multiplication.
     * See page 64
     * @param a string of size m
     * @param b string of size n
     * @return Permutation matrix of type resMatrix for the semilocalLCS problem (aka return semi-local lcs kernel)
     */
    override fun <T> evaluate(a: List<T>, b: List<T>): Matrix {

        if (a.size == 1 && b.size == 1) {
            val identityMatrix = instance.createZeroMatrix(2, 2)
            if (a == b) {
                identityMatrix[0, 0] = true
                identityMatrix[1, 1] = true
            } else {
                identityMatrix[1, 0] = true
                identityMatrix[0, 1] = true
            }
            return identityMatrix
        }

        if (b.size > a.size) {
            val n1 = b.size / 2
            val b1 = b.subList(0, n1)
            val b2 = b.subList(n1, b.size)
            return getPermBA(
                staggeredStickyMultiplication(
                    evaluate(b1, a),
                    evaluate(b2, a), a.size
                ), a.size, b.size
            )
        } else {
            val m1 = a.size / 2
            val a1 = a.subList(0, m1)
            val a2 = a.subList(m1, a.size)

            return staggeredStickyMultiplication(
                evaluate(a1, b),
                evaluate(a2, b),
                b.size
            )
        }


    }
}


/**
 *  @param matrixInstance is for providing createZeroMatrix function of specified type (bad kotlin)
 */
class ReducingKernelEvaluation<M : Matrix>(matrixInstance: () -> M) : IStrategyKernelEvaluation {
    private val instance = matrixInstance().createZeroMatrix(0, 0)

    /**
     * iterative version of semilocalLCS (the second one with idea of unswepen unreduced sticky braid to reduced one
     * to get semi local lcs kernel).
     * See page 68.
     * @param a string of size m
     * @param b string of size n
     * @return Permutation matrix of type resMatrix for the semilocalLCS problem (aka return semi-local lcs kernel)
     */
    override fun <T> evaluate(a: List<T>, b: List<T>): Matrix {

//    fun isCrossedPreviously(strandLeft: Int, strandTop: Int): Boolean =
//        //странд слева > странд сверху
//        strandLeft > strandTop


        val solution = instance.createZeroMatrix(a.size + b.size, a.size + b.size)
        val strandMap =
            IntArray(a.size + b.size) { i -> i }// what strand is now at left and top edge of current cell strand
//        for (i in 0 until a.size + b.size) strandMap[i] = i

        for (i in a.indices) {
            for (j in b.indices) {
                val leftEdge = a.size - 1 - i
                val topEdge = a.size + j
                val leftStrand = strandMap[leftEdge]
                val rightStrand = strandMap[topEdge]

                if (a[i] == b[j] || (a[i] != b[j] && leftStrand > rightStrand)) {
                    strandMap[leftEdge] = rightStrand
                    strandMap[topEdge] = leftStrand
                }

                if (j == b.size - 1) {
                    val strandEnd = leftEdge + b.size
                    val strandStart = strandMap[leftEdge]
                    solution[strandStart, strandEnd] = true
                }

                if (i == a.size - 1) {
                    val strandEnd = topEdge - a.size
                    val strandStart = strandMap[topEdge]
                    solution[strandStart, strandEnd] = true
                }

            }
        }
        return solution
    }
}


/**
 * see theorem 5.21
 * Allows get P_{a,b} when you have P_{b,a}
 */
fun getPermBA(A: Matrix, m: Int, n: Int): Matrix {
    val B = A.createZeroMatrix(A.height(), A.width())
    for (a in A)
        B[n + m - 1 - a.i, m + n - 1 - a.j] = true

    return B
}


//TODO new

/**
 *
 */
fun getMongeMatrixBA(A: AbstractMongeMatrix, m: Int, n: Int): AbstractMongeMatrix {
    val B = A.createNewMatrix(A.height(), A.width())
    for (i in 0 until B.height()) {//-n to m
        for (j in 0 until B.width()) {
//            B[n + m - i, m + n - j] = A[i,j] +  (i - n) - j + m
            B[i, j] = A[n + m - i, m + n - j] - (i - n) + j - n
        }
    }

    return B
}

interface IStrategyExplicitMatrixEvaluation {
    fun <T> evaluate(a: List<T>, b: List<T>): AbstractMongeMatrix
}


/**
 * For given scoring scheme provides solve method that
 */
class ExplicitKernelEvaluation(private val scheme: IScoringScheme):IStrategyExplicitMatrixEvaluation {
    val mu = scheme.getNormalizedMismatchScore().numerator
    val v = scheme.getNormalizedMismatchScore().denominator

    //base cases
    val matchIdMatrix = MongeMatrix(3, 3)
    val mismatchMatrix = MongeMatrix(3, 3)

    init {
        // fill mismatch matrix
        mismatchMatrix[0, 0] = 0.0
        mismatchMatrix[1, 0] = 0.0
        mismatchMatrix[2, 0] = 0.0

        mismatchMatrix[2, 1] = 0.0
        mismatchMatrix[2, 2] = 0.0

        mismatchMatrix[0, 1] = 1.0
        mismatchMatrix[0, 2] = 2.0

        mismatchMatrix[1, 1] = 1.toDouble() - mu.toDouble() / v
        mismatchMatrix[1, 2] = 1.toDouble()


        for (i in 0 until matchIdMatrix.height()) {
            for (j in 0 until matchIdMatrix.width()) {
                matchIdMatrix[i, j] = max(0, j - i).toDouble()
            }
        }

    }

    private fun <T> sol(a: List<T>, b: List<T>): AbstractMongeMatrix = when {
        a.size == 1 && b.size == 1 && a[0] == b[0] -> matchIdMatrix
        a.size == 1 && b.size == 1 && a[0] != b[0] -> mismatchMatrix
        b.size > a.size -> {
            val n1 = b.size / 2
            val b1 = b.subList(0, n1)
            val b2 = b.subList(n1, b.size)

            getMongeMatrixBA((staggeredExplicitMultiplication(sol(b1, a), sol(b2, a), a.size)), a.size, b.size)
        }
        else -> {
            val m1 = a.size / 2
            val a1 = a.subList(0, m1)
            val a2 = a.subList(m1, a.size)

            staggeredExplicitMultiplication(sol(a1, b), sol(a2, b), b.size)

        }
    }


    override fun <T> evaluate(a: List<T>, b: List<T>): AbstractMongeMatrix {
        var solution = sol(a, b)
        if (solution[solution.height() - 1, 0] != 0.0) solution = getMongeMatrixBA(solution, a.size, b.size)
        //do not forget apply
        return solution
    }

}



