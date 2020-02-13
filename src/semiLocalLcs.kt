import org.w3c.dom.ranges.Range
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

//TODO add semilocalLCS with fast query
interface IImplicitSemiLocalLCS : ISemiLocalLCS


class ImplicitSemiLocalLCS<Element>(
    val a: List<Element>,
    val b: List<Element>,
    var kernelEvaluator: (List<Element>, List<Element>) -> AbstractPermutationMatrix
) :
    ISemiLocalLCS where Element : Comparable<Element> {

    val m = a.size
    val n = b.size

    internal var permutationMatrix: AbstractPermutationMatrix = kernelEvaluator(a, b)
    internal var rangeTree2D: RangeTree2D<Int>

    init {
        val mutableList = mutableListOf<Position2D<Int>>()
        for (p in permutationMatrix) mutableList.add(p)
        rangeTree2D = RangeTree2D(mutableList)

    }

    /**
     * i from 0 to m + n
     */
    fun canonicalDecomposition(i: Int, j: Int): Int =
        j - (i - m) - rangeTree2D.ortoghonalQuery(IntervalQuery(i, m + n), IntervalQuery(-1, j - 1))


    override fun prefixSuffixLCS(k: Int, j: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stringSubstringLCS(i: Int, j: Int): Int {
        if (i < 0 || i > n || j < 0 || j > n) return -1
        return canonicalDecomposition(i + m, j)
    }

    override fun substringStringLCS(k: Int, l: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun suffixPrefixLCS(l: Int, i: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}


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

    when {
        k == 0 -> {
            val res = P.createZeroMatrix(P.height() + Q.height(), P.width() + Q.width())
            for (p in P) res[p.i + Q.height(), p.j + Q.width()] = true
            for (q in Q) res[q.i, q.j] = true
            return res
        }
        k == P.width() && k == Q.height() -> return steadyAntWrapper(P, Q)
        else -> {
            // take first k columns from P and last k rows from Q, multiply and to bottom left corner of extended matrix

            val reducedP = P.createZeroMatrix(P.height(), k)
            for (column in 0 until k) {
                val row = P[column, AbstractPermutationMatrix.GetType.COLUMN]
                if (row != P.NOPOINT) reducedP[row, column] = true
            }
            val reducedQ = Q.createZeroMatrix(k, Q.width())
            for (row in 0 until k) {
                val column = Q[Q.height() - k + row, AbstractPermutationMatrix.GetType.ROW]
                if (column != Q.NOPOINT) reducedQ[row, column] = true
            }
            val res = P.createZeroMatrix(P.height() + Q.height() - k, P.width() + Q.width() - k)
            val reducedRes = steadyAntWrapper(reducedP, reducedQ)


            for (p in reducedRes) res[Q.height() - k + p.i, p.j] = true

            for (q in Q)
                if (q.i < Q.height() - k) res[q.i, q.j] = true


            for (p in P)
                if (p.j >= k) res[p.i + Q.height() - k, p.j + Q.width() - k] = true

            return res
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
fun <Elem : Comparable<Elem>> semiLocalLCSRecursive(
    a: List<Elem>,
    b: List<Elem>,
    resMatrix: AbstractPermutationMatrix
): AbstractPermutationMatrix {
    if (a.size == 1 && b.size == 1) {
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

    if (b.size > a.size) {
        val n1 = b.size / 2
        val b1 = b.subList(0, n1)
        val b2 = b.subList(n1, b.size)
        return getPermBA(
            staggeredStickyMultiplication(
                semiLocalLCSRecursive(b1, a, resMatrix),
                semiLocalLCSRecursive(b2, a, resMatrix), a.size
            ), a.size, b.size
        )
    } else {
        val m1 = a.size / 2
        val a1 = a.subList(0, m1)
        val a2 = a.subList(m1, a.size)

        return staggeredStickyMultiplication(
            semiLocalLCSRecursive(a1, b, resMatrix),
            semiLocalLCSRecursive(a2, b, resMatrix),
            b.size
        )
    }

}

/**
 * iterative version of semilocalLCS (the second one with idea of unswepen unreduced sticky braid to reduced one
 * to get semi local lcs kernel).
 * See page 68.
 * @param a string of size m
 * @param b string of size n
 * @param resMatrix is for providing createZeroMatrix function of specified type (bad kotlin)
 * @return Permutation matrix of type resMatrix for the semilocalLCS problem (aka return semi-local lcs kernel)
 */
fun <Elem : Comparable<Elem>> semiLocalLCSByReducing(
    a: List<Elem>,
    b: List<Elem>,
    resMatrix: AbstractPermutationMatrix
): AbstractPermutationMatrix {

//    fun isCrossedPreviously(strandLeft: Int, strandTop: Int): Boolean =
//        //странд слева > странд сверху
//        strandLeft > strandTop


    val solution = resMatrix.createZeroMatrix(a.size + b.size, a.size + b.size)
    val strandMap = hashMapOf<Int, Int>()// what strand is now at left and top edge of current cell strand
    for (i in 0 until a.size + b.size) strandMap[i] = i

    for (i in a.indices) {
        for (j in b.indices) {
            val leftEdge = a.size - 1 - i
            val topEdge = a.size + j
            val leftStrand = strandMap[leftEdge]!!
            val rightStrand = strandMap[topEdge]!!

            if (a[i] == b[j] || (a[i] != b[j] && leftStrand > rightStrand)) {
                strandMap[leftEdge] = rightStrand
                strandMap[topEdge] = leftStrand
            }

            if (j == b.size - 1) {
                val strandEnd = leftEdge + b.size
                val strandStart = strandMap[leftEdge]!!
                solution[strandStart, strandEnd] = true
            }

            if (i == a.size - 1) {
                val strandEnd = topEdge - a.size
                val strandStart = strandMap[topEdge]!!
                solution[strandStart, strandEnd] = true
            }

        }
    }
    return solution
}


//    /**
//     * The iterative algorithm for semilocal lcs kernel.
//     * See page 67
//     *
//     */
//    fun semiLocalLCSIterative(a: String, b: String, resMatrix: AbstractPermutationMatrix): AbstractPermutationMatrix {
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
//                PStroke[P[j - 1, AbstractPermutationMatrix.GetType.COLUMN], 0] = true
//                PStroke[P[j, AbstractPermutationMatrix.GetType.COLUMN], 1] = true
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