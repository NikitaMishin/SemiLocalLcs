package beyondsemilocality

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import utils.IScoringScheme
import utils.Interval
import utils.isEquals
import kotlin.math.max

// BLSW-alignment
//TODO possible look up for all pairs of non intersected clones
/**
 * Computes bounded-length smith waterman alignemnt
 */
class BoundedLengthSmithWatermanAlignment<T>(val provider: IFragmentSubstringProvider<T>) {

    enum class TraceBackPointer {
        RightGap,
        BottomGap,
        DiagonalMatch,
        DiagonalMismatch,
        Window,
        Dummy
    }

    data class SWSell(var type: TraceBackPointer, var score: Double)

    fun solve(a: List<T>, b: List<T>, scheme: IScoringScheme, minWindow: Int): Pair<Interval, Interval> {
        val w = minWindow

        if (a.size < minWindow) return Pair(
            Interval(0, 0, Double.NEGATIVE_INFINITY),
            Interval(0, 0, Double.NEGATIVE_INFINITY)
        )

        if (b.isEmpty()) {
            return Pair(Interval(0, w, (scheme.getGapScore() * w).toDouble()), Interval(0, 0))
        }

        val windows = WindowSubstringSA(provider).solve(a, b, minWindow, scheme).getSolutions()

        val h: Array<Array<SWSell>> =
            Array(a.size + 1) {
                Array(b.size + 1) {
                    SWSell(
                        TraceBackPointer.Dummy,
                        (scheme.getGapScore() * w).toDouble()
                    )
                }
            }

        //just want array of arrays...
        val completeAMatchSolution: Array<Array<Pair<Int, Double>>> =
            Array(a.size + 1 - w) { Array(b.size + 1) { Pair(-99, 0.0) } }

//        Only one window
        if (a.size == w) {
            val bIntetrval = CompleteAMatchViaSemiLocalTotallyMonotone(windows[0]).solve()
                .mapIndexed { col, (row, score) -> Interval(row, col, score) }.maxBy { it.score }!!
            return Pair(Interval(0, w, bIntetrval.score), bIntetrval)
        }


        //TODo assign also i to traceback
        for ((l, window) in windows.withIndex()) {
            CompleteAMatchViaSemiLocalTotallyMonotone(window).solve().forEachIndexed { col, p ->
                completeAMatchSolution[l][col] = p
            }
        }



        for (j in 0 until b.size + 1) {
            h[w][j] = SWSell(TraceBackPointer.Window, completeAMatchSolution[w - w][j].second)
        }

        var localMax = Double.NEGATIVE_INFINITY
        var startB = 0
        var startA = 0
        for (l in w + 1..a.size) {
            for (j in 1..b.size) {
                val up = h[l - 1][j].score + scheme.getGapScore().toDouble()
                val left = h[l][j - 1].score + scheme.getGapScore().toDouble()
                val diagonal = h[l - 1][j - 1].score + if (a[l - 1] == b[j - 1]) scheme.getMatchScore()
                    .toDouble() else scheme.getMismatchScore().toDouble()
                val window = completeAMatchSolution[l - w][j].second
                val maximum = max(max(left, up), max(diagonal, window))
                h[l][j].score = maximum
                h[l][j].type = when {
                    //preffers window over others
                    maximum.isEquals(window) -> TraceBackPointer.Window
                    // preffer matches
                    maximum.isEquals(diagonal) && a[l - 1] == b[j - 1] -> TraceBackPointer.DiagonalMatch
                    //preffer mismathes
                    maximum.isEquals(diagonal) && a[l - 1] != b[j - 1] -> TraceBackPointer.DiagonalMismatch
                    //preffer
                    maximum.isEquals(up) -> TraceBackPointer.BottomGap
                    maximum.isEquals(left) -> TraceBackPointer.RightGap
                    //                    else-> NotImplementedError
                    else -> TraceBackPointer.Dummy

                }

                if (maximum > localMax) {
                    startB = j
                    startA = l
                    localMax = maximum

                }
            }
        }

        val aEnd = startA
        val bEnd = startB
        loop@ while (true) {
            when (h[startA][startB].type) {
                TraceBackPointer.RightGap -> startB--

                TraceBackPointer.BottomGap -> startA--

                TraceBackPointer.DiagonalMatch -> {
                    startA--
                    startB--
                }
                TraceBackPointer.DiagonalMismatch -> {
                    startA--
                    startB--
                }
                TraceBackPointer.Window -> {
                    val start = completeAMatchSolution[startA - w][startB].first
                    startA = startA - 1 - (w - 1)
                    startB = startB - 1 - (startB - start - 1)
                    break@loop
                }
                TraceBackPointer.Dummy -> break@loop
            }
        }


        return Pair(Interval(startA, aEnd, localMax), Interval(startB, bEnd, localMax))


    }
}
