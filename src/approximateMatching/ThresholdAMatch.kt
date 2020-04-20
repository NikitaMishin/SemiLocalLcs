package approximateMatching

import utils.Interval
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


//TODO some words aboyt normalization?
class ThresholdAMathViaSemiLocal<T>(private var aMatchProblem: ICompleteAMatchProblem<T>) :
    IThresholdAMatch {
    override fun solve(threshold: Double): List<Interval> {
        val colMax = aMatchProblem.solve()
        val res = LinkedList<Interval>()

        var j = colMax.size - 1
        //will go backwards with filtering
        while (j > 0) {
            if (colMax[j].second >= threshold) {
                res.add(Interval(colMax[j].first, j, colMax[j].second))
//                j--
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
            //TODO
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
