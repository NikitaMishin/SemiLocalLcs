package longestCommonSubsequence

import utils.Matrix
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
data class Symbol<T>(val symbol: T, val type: SymbolType) where T : Comparable<T>{
    override fun equals(other: Any?): Boolean {
        if (other !is Symbol<*>) return false // todo bad kotlin type erasure
        if(this.type== SymbolType.WildCardSymbol || other.type== SymbolType.WildCardSymbol ||
            (this.type== SymbolType.AlphabetSymbol && other.type== SymbolType.AlphabetSymbol && this.symbol==other.symbol) ||
                    this.type== SymbolType.GuardSymbol && other.type== SymbolType.GuardSymbol
        ) return true
        return false
    }

    fun repeatDeeopCopy(times:Int):List<Symbol<T>> = (0 until times).map { this.copy(this.symbol,this.type) }
    fun repeatShallowCopy(times:Int):List<Symbol<T>> = (0 until times).map { this }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}


/**
 * Interface for semiLocal LCS problem for the two given lists of comparable elements.
 * The definition of semiLocal LCS problem see book "The algebra of string comparison: Computing with sticky braids",
 * page 51
 */
interface ISemiLocalLCS {
    /**
     *For a given A and B asks for lcs score for A and B[i:j]
     */
    fun stringSubstringLCS(i: Int, j: Int): Int

    /**
     *For a given A and B asks for lcs score for A[k:A.size] and B[0:j]
     */
    fun prefixSuffixLCS(k: Int, j: Int): Int

    /**
     *For a given A and B asks for lcs score for A[0:l] and B[i:B.size]
     */
    fun suffixPrefixLCS(l: Int, i: Int): Int

    /**
     *For a given A and B asks for lcs score for A[k:l] and B
     */
    fun substringStringLCS(k: Int, l: Int): Int


    fun getAtPosition(i:Int,j:Int):Int

    companion object{
        val alphabet = arrayListOf(
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'y',
            'z'
        )
        fun getRandomString(randToSkip: Int, stringSize: Int, alphabetString: List<Char>,random: Random): List<Char> {
            for (i in 0 until randToSkip) random.nextInt()
            return (0 until stringSize).map { alphabetString[kotlin.math.abs(random.nextInt()) % alphabetString.size] }
        }
    }
}

interface IImplicitSemiLocalLCS : ISemiLocalLCS{
    val kernel: Matrix

}
interface IStrategyKernelEvaluation<T : Comparable<T>> {
    fun evaluate(a: List<Symbol<T>>, b: List<Symbol<T>>): Matrix
}

/**
 *  @param  matrixInstance to get type M (see type erasure)
 */
class RecursiveKernelEvaluation<T : Comparable<T>, M : Matrix>(matrixInstance: () -> M) :
    IStrategyKernelEvaluation<T> {
    private val instance = matrixInstance().createZeroMatrix(0, 0)
    /**
     * The recursive algorithm based on steady ant braid multiplication.
     * See page 64
     * @param a string of size m
     * @param b string of size n
     * @return Permutation matrix of type resMatrix for the semilocalLCS problem (aka return semi-local lcs kernel)
     */
    override fun evaluate(a: List<Symbol<T>>, b: List<Symbol<T>>): Matrix {

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
class ReducingKernelEvaluation<T : Comparable<T>, M : Matrix>(matrixInstance: () -> M) : IStrategyKernelEvaluation<T> {
    private val instance = matrixInstance().createZeroMatrix(0, 0)

    /**
     * iterative version of semilocalLCS (the second one with idea of unswepen unreduced sticky braid to reduced one
     * to get semi local lcs kernel).
     * See page 68.
     * @param a string of size m
     * @param b string of size n
     * @return Permutation matrix of type resMatrix for the semilocalLCS problem (aka return semi-local lcs kernel)
     */
    override fun evaluate(a: List<Symbol<T>>, b: List<Symbol<T>>): Matrix {

//    fun isCrossedPreviously(strandLeft: Int, strandTop: Int): Boolean =
//        //странд слева > странд сверху
//        strandLeft > strandTop


        val solution = instance.createZeroMatrix(a.size + b.size, a.size + b.size)
        val strandMap = IntArray(a.size+b.size) { i->i}// what strand is now at left and top edge of current cell strand
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


//    /**
//     * The iterative algorithm for semilocal lcs kernel.
//     * See page 67
//     *
//     */
//    fun semiLocalLCSIterative(a: String, b: String, resMatrix: utils.AbstractPermutationMatrix): utils.AbstractPermutationMatrix {
//
//        val P = resMatrix.createZeroMatrix(a.length + b.length, a.length + b.length)
//        for (i in 0 until a.length + b.length) P[a.length + b.length - 1 - i, i] = true // fully mismatched
////    for(i in 0 until a.length+b.length) P[i,i] = true // fully mismatched
//
//        val PStroke = resMatrix.createZeroMatrix(P.height(), 2)
//        val cell = resMatrix.createZeroMatrix(2, 2)
//
//        for (i in 1 until P.height()) {
//            for (j in 1 until P.width()) {
//                //fresh
//                PStroke.resetInRow(0)
//                PStroke.resetInRow(1)
//                PStroke[P[j - 1, utils.AbstractPermutationMatrix.GetType.COLUMN], 0] = true
//                PStroke[P[j, utils.AbstractPermutationMatrix.GetType.COLUMN], 1] = true
//                cell.resetInRow(0)
//                cell.resetInRow(1)
//                if (a[i] == b[j]) {
//                    // cell.set(0,0,true)
//                    //cell.set(1,1,true)
//                } else {
//                    cell[1, 0] = true
//                    cell[0, 1] = true
//                    P.resetInColumn(j)
//                    P.resetInColumn(j - 1)
//                    val product = steadyAntWrapper(PStroke, cell)
//                    for (p in product) P[p.i, p.j + j - 1] = true
//                }
//            }
//
//        }
//        //  P.print()
//
//        return P
//    }