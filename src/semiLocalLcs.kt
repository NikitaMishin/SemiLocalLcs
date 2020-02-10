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


/**
 * page 60
 * the product dimension should be (P.height +   Q.height - k) X (P.width +   Q.width - k)
 * see TODO
 *
 */
fun staggeredStickyMultiplication(P: AbstractPermutationMatrix, Q: AbstractPermutationMatrix, k: Int): AbstractPermutationMatrix {
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
            for (i in 0 until PIdentitySize) QExt[Q.height() + i, Q.width() + i] = true
            for (q in Q) QExt[q.i, q.j] = true
            return steadyAntWrapper(PExt, QExt)
        }
    }


}


/**
 * see theorem 5.21
 */
fun getPermBA(A: AbstractPermutationMatrix, m: Int, n: Int): AbstractPermutationMatrix {
    val B = A.createZeroMatrix(A.height(), A.width())
    for (a in A) B[n - a.i, m + n - a.j] = true
    return B
}

fun semiLocalLCSRecursive(
    a: String,
    b: String,
    m: Int,
    n: Int,
    resMatrix: AbstractPermutationMatrix
): AbstractPermutationMatrix {
    if (n == 1 && m == 1) {
        val identityMatrix = resMatrix.createZeroMatrix(2, 2)
        identityMatrix[0, 0] = true
        identityMatrix[1, 1] = true
        return identityMatrix
    }

    if (n > m) {
        val n1 = n / 2
        val n2 = n - n / 2
        val b1 = b.substring(0, n1)
        val b2 = b.substring(n1, n)
        val res = staggeredStickyMultiplication(
            getPermBA(semiLocalLCSRecursive(b1, a, n1, m, resMatrix),m,n1),
            getPermBA(semiLocalLCSRecursive(b2, a, n2, m, resMatrix),m,n2),
            m
        )
        return res
    } else {
        val m1 = m / 2
        val m2 = m - m / 2
        val a1 = a.substring(0, m1)
        val a2 = a.substring(m1, m)
        return staggeredStickyMultiplication(
            semiLocalLCSRecursive(a1, b, m1, n, resMatrix),
            semiLocalLCSRecursive(a2, b, m2, n, resMatrix),
            n
        )
    }

}
