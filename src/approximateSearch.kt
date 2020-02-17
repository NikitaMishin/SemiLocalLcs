import kotlin.math.ceil
import kotlin.math.max

/**z
 *
 * TODO file
 *
 *
 *
 *
 *
 */


data class Interval(val beg: Int, val end: Int, val id: Int)

/**
 * seqMatches are positions of matched things
 */
data class SearchResult(val intervals: Sequence<Interval>,val similarityScore: Double)


/**
 *
 */
interface PatternSearchStrategy {
    /**
     * TODO:
     * Sequence of match results present all occurences of pattern in text, they may overlap
     * T may be chars, numbers etc
     */
    //pattern: List<T>, text: List<T>
    fun execute(): Sequence<SearchResult>
}

class dummyPatterSearchStrategy<Elem>(var pattern: List<Elem>, var fragment: List<Elem>,threshold: Double) : PatternSearchStrategy {
    private val thersholdInt = ceil(pattern.size * threshold)
    override fun execute(): Sequence<SearchResult> {
        // O(n^2)
        val subseqs = sequence {
            val n = fragment.size
            for (i in 0 until n) {
                for (j in i + 1 until n + 1) {
                    yield(Pair(i, j))
                }
            }
        }
        TODO("Return postions beg and end")
        // O(n^2 * n*m)
//        return subseqs
//            .filter { (i, j) -> dummyLcs(fragment.subList(i, j), pattern).last().last() >= thersholdInt }

    }

}


/**
 *
 *
 *
 * @param a List of elements in a
 * @param n
 */
fun <Elem> dummyLcs(a: List<Elem>, b: List<Elem>): Array<IntArray> {
    val n = a.count() + 1
    val m = b.count() + 1

    val lcsMatrix = Array(n) { _ -> IntArray(m) { _ -> 0 } }
    for (rowNum in 1 until n) {
        for (colNum in 1 until m) {
            if (a[rowNum - 1] == b[colNum - 1]) lcsMatrix[rowNum][colNum] = lcsMatrix[rowNum - 1][colNum - 1] + 1
            else {
                lcsMatrix[rowNum][colNum] = max(lcsMatrix[rowNum - 1][colNum], lcsMatrix[rowNum][colNum - 1])
            }
        }
    }
    return lcsMatrix
}

