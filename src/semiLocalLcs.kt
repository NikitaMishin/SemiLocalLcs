import java.lang.IllegalArgumentException
import kotlin.math.min

///**
// *  Semi Local problems
// */
//
//
///**
// *
// */
//abstract class SemiLocalLCS<E> {
//    abstract var fragmentA: List<E>
//    abstract var fragmentB: List<E>
//    // TODO ideompotence operation
//    abstract fun solve()
//
//    // TODO waht args
//    abstract fun query(i: Int, j: Int)
//}
//
///**
// *
// */
//class SemiLocalLCSByMonge<E, T : MatrixElem, M : MongeMatrix<T>>(
//    private var solver: SemiLocalLCSSolveStrategy<T, M>,
//    override var fragmentA: List<E>,
//    override var fragmentB: List<E>) : SemiLocalLCS<E>() {
//    private lateinit var matrix: M
//    override fun solve() {
//        //should be ideompotence
//        matrix = solver.solve(fragmentA, fragmentB)
//    }
//
//    fun getMatrix(): M = matrix
//
//    fun setSolver(newSolver: SemiLocalLCSSolveStrategy<T, M>) {
//        solver = newSolver
//    }
//
//    override fun query(i: Int, j: Int) {
//        //matrix.get()
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}
//
//
///**TODO K constraint comparable and hash
// * or move him to class declaration
// *
// */
//abstract class SemiLocalLCSSolveStrategy<T : MatrixElem, M : MongeMatrix<T>>() {
//    abstract fun <K> solve(a: List<K>, b: List<K>): M
//}
//
///**
// *
// */
//class SemiLocalLCSSolveStrategyRecursive<T : MatrixElem, M : MongeMatrix<T>>() : SemiLocalLCSSolveStrategy<T, M>() {
//    override fun <K> solve(a: List<K>, b: List<K>): M {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
//
///**
// *
// */
//class SemiLocalLCSSolveStrategyIterative<T : MatrixElem, M : MongeMatrix<T>>() : SemiLocalLCSSolveStrategy<T, M>() {
//    override fun <K> solve(a: List<K>, b: List<K>): M {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}


/**
 *Symbol type refer to Symbol
 */
enum class SymbolType {
    AlphabetSymbol,
    WildCardSymbol,// '?' - symbol not presented in alphabet
    //...
}

/**
 * Symbol is extended alphabet for semiLocalLCS
 */
data class Symbol<T>(val symbol: T, val type: SymbolType) where T : Comparable<T>


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
}


//data class ImplicitSemiLocalLCS<Element,PermMatrixType:AbstractPermutationMatrix>(val a: List<Element>, val b: List<Element>) :
//    ISemiLocalLCS where Element : Comparable<Element> {
//    val m = a.size
//    val n = b.size
//
//    private var permutationMatrix:PermMatrixType? = null
//    private var rangeTree2D:RangeTree2D<Position2D<Int>>? = null
//
//    private fun CanonicalDecomposition(i:Int,j:Int){
//        return j - (m+n-i) - rangeTree2D.ortoghonalQuery()
//
//    }
//
//
//
//}


/**
 * page 60
 * the product dimension should be (P.height +   Q.height - k) X (P.width +   Q.width - k)
 * see TODO
 *
 */
fun staggeredStickyMultiplication(
    P: AbstractPermutationMatrix,
    Q: AbstractPermutationMatrix,
    k: Int
): AbstractPermutationMatrix {
    if (k < 0 || k > min(P.width(), Q.height())) throw IllegalArgumentException("0<=k<=${P.width()},${Q.height()}")
    val PIdentitySize = Q.height() - k
    val QIdentitySize = P.width() - k
    when {
        k == 0 -> {
            val res = P.createZeroMatrix(P.height() + Q.height(), P.width() + Q.width())
            for (p in P) res[p.i + Q.height(), p.j + Q.width()] = true
            for (q in Q) res[q.i, q.j] = true
            return res
        }
        k == P.width() && k == Q.height() -> return steadyAntWrapper(P, Q)
        else -> {
            // TODO not optimal ask Tiskin or discuss with Danya
            val PExt = P.createZeroMatrix(P.height() + Q.height() - k, PIdentitySize + P.width())
            for (i in 0 until PIdentitySize) PExt[i, i] = true
            for (p in P) PExt[p.i + PIdentitySize, p.j + PIdentitySize] = true

            val QExt = Q.createZeroMatrix(Q.height() + QIdentitySize, P.width() + Q.width() - k)

            for (i in 0 until QIdentitySize) {
                QExt[Q.height() + i, Q.width() + i] = true
            }
            for (q in Q) QExt[q.i, q.j] = true
            return steadyAntWrapper(PExt, QExt)
        }
    }


}


/**
 * see theorem 5.21
 * Allows get P_{a,b} when you have P_{b,a}
 */
fun getPermBA(A: AbstractPermutationMatrix, m: Int, n: Int): AbstractPermutationMatrix {
    val B = A.createZeroMatrix(A.height(), A.width())
    for (a in A)
        //TODO seems like this
        B[n + m - 1 - a.i, m + n - 1 - a.j] = true

    return B
}

/**
 * The recursive algorithm based on steady ant braid multiplication.
 * See page 64
 * @param a string of size m
 * @param b string of size n
 * @param resMatrix is for providing createZeroMatrix function of specified type (bad kotlin)
 * @return Permutation matrix of type resMatrix for the semilocalLCS problem (aka return semi-local lcs kernel)
 */
fun semiLocalLCSRecursive(a: String, b: String, resMatrix: AbstractPermutationMatrix): AbstractPermutationMatrix {
    if (a.length == 1 && b.length == 1) {
        val identityMatrix = resMatrix.createZeroMatrix(2, 2)
        if (a == b) {
            identityMatrix[0, 0] = true
            identityMatrix[1, 1] = true
        } else {
            identityMatrix[1, 0] = true
            identityMatrix[0, 1] = true
        }
        return identityMatrix
    }

    if (b.length > a.length) {
        val n1 = b.length / 2
        val b1 = b.substring(0, n1)
        val b2 = b.substring(n1, b.length)
        return getPermBA(
            staggeredStickyMultiplication(
                semiLocalLCSRecursive(b1, a, resMatrix),
                semiLocalLCSRecursive(b2, a, resMatrix), a.length
            ), a.length, b.length
        )
    } else {
        val m1 = a.length / 2
        val a1 = a.substring(0, m1)
        val a2 = a.substring(m1, a.length)

        return staggeredStickyMultiplication(
            semiLocalLCSRecursive(a1, b, resMatrix),
            semiLocalLCSRecursive(a2, b, resMatrix),
            b.length
        )
    }

}
