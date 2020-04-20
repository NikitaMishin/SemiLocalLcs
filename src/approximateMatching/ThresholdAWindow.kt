package approximateMatching

import sequenceAlignment.ISemiLocalFastAccess
import sequenceAlignment.ISemiLocalSolution
import utils.Interval


/**
 *
 */
interface IThresholdAWindowProblem {
    /**
     *
     */
    fun solve(threshold: Double, windowLen: Int): List<Interval>
}

class ThresholdWindowSemiLocal<T>(var semilocal: ISemiLocalSolution<T>, var aMatchProblem: ICompleteAMatchProblem<T>) :
    IThresholdAWindowProblem {

    //TODO add value transofre
    override fun solve(threshold: Double, windowLen: Int): List<Interval> {
        if (windowLen > semilocal.text.size) return listOf()

        val colMax = aMatchProblem.solve()
        val m = semilocal.pattern.size
        val n = semilocal.text.size

        var curCol = windowLen
        var curRow = m

        var curValue = semilocal.getAtPosition(curRow, curCol)  // Log(n)
        val res = mutableListOf<Interval>()

        if (curValue >= threshold || (colMax[curCol].first + m > curRow && colMax[curCol].second >= threshold)) {
            res.add(Interval(curRow - m, curCol, threshold))
        }

        // goes diagonal via incremental queries
        while (curRow + 1 <= n + m && curCol + 1 <= n) {
            curValue = semilocal.nextInRow(
                curRow, curCol, curValue,
                ISemiLocalFastAccess.Direction.Forward
            )
            curCol++
            curValue = semilocal.nextInCol(
                curRow, curCol, curValue,
                ISemiLocalFastAccess.Direction.Forward
            )
            curRow++
            println(curRow)
            println(curCol)

            if (curValue >= threshold || (colMax[curCol].first + m > curRow && colMax[curCol].second >= threshold)) {
                res.add(Interval(curRow - m, curCol, curValue))
            }

        }
        return res
    }
}