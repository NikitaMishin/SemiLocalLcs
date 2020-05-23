package approximateMatching

import sequenceAlignment.ISemiLocalCombined
import sequenceAlignment.ISemiLocalFastAccess
import sequenceAlignment.ISemiLocalProvider
import utils.Interval
import utils.Position1D
import utils.Position2D
import utils.RangeTree2D

import java.util.*


/**
 *
 */
interface IThresholdAMatch {
    /**
     *
     */
    fun solve(threshold: Double): List<Interval>
}


class ThresholdAMathViaSemiLocal<T>(private var aMatchProblem: ICompleteAMatchProblem<T>) :
    IThresholdAMatch {
    override fun solve(threshold: Double): List<Interval> {
        val colMax = aMatchProblem.solve()
        val res = LinkedList<Interval>()

        println(colMax.mapIndexed { index,f-> Pair(Pair(f.first,index ),f.second)}.maxBy { it.second })
//        println(colMax.find { it.second }.f)


        var j = colMax.size - 1
        //will go backwards with filtering
        while (j > 0) {
            if (colMax[j].second >= threshold) {
                res.add(Interval(colMax[j].first, j, colMax[j].second))
                break
            }
            j--
        }

        //actual filtration
        while (j > 0) {
            // interrsect and
            val left = colMax[j].first
            val right = j
            val value = colMax[j].second
            //TODO or this better approach?
//            if (value >= threshold && (right > res.last().startInclusive && value >= res.last().score) && left >= res.last.startInclusive ) {
//                //find better
//                res.removeLast()
//                res.add(Interval(left, right, value))
//            } else
            if (value >= threshold && right <= res.last().startInclusive) res.add(Interval(left, right, value))
            j--
        }

        return res.reversed()
    }
}



//
//
//class ThresholdMax<T>(private val semilocal:ISemiLocalCombined<T>):IThresholdAMatch {
//    private fun getDoubleMatrixStringSubstring() {
//        val m = semilocal.pattern.size
//        val n = semilocal.pattern.size
//        val curValue = semilocal.getAtPosition(m, 0)
//        val points = mutableListOf<Position2D<Double>>()
//        val zeroColumn = DoubleArray(n + 1)
//        RangeTree2D(points)
////        semilocal.getAtPosition()
//        for (i in 0 until n) {
//            val curRaw = zeroColumn[i]
//            points.add((i,))
//            for (j in 0 until n ) {
//                semilocal.nextInRow(i,j,curRaw,ISemiLocalFastAccess.Direction.Forward)
//            }
//        }
//
//             // goes diagonal via incremental queries
//        while (curRow + 1 <= n + m && curCol + 1 <= n) {
//            curValue = semilocal.nextInRow(
//                curRow, curCol, curValue,
//                ISemiLocalFastAccess.Direction.Forward
//            )
//            curCol++
//            curValue = semilocal.nextInCol(
//                curRow, curCol, curValue,
//                ISemiLocalFastAccess.Direction.Forward
//            )
//            curRow++
//            println(curRow)
//            println(curCol)
//
//            if (curValue >= threshold || (colMax[curCol].first + m > curRow && colMax[curCol].second >= threshold)) {
//                res.add(Interval(curRow - m, curCol, curValue))
//            }
//
//        }
//        return res
//
//    }
//
//    override fun solve(threshold: Double): List<Interval> {
//
//    }
//}