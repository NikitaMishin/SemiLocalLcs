package utils

import duplicateDetection.ApproximateMatchingViaRangeQuery
import kotlin.math.*
import kotlin.random.Random


/**
 * provides aceess to fast range maximum query
 */
interface IRMQ2D {
    fun query(x1: Int, x2: Int, y1: Int, y2: Int): Interval
}

class NaiveRMQ2D : IRMQ2D {
    private val matrix: Array<DoubleArray>

    constructor(array: Array<DoubleArray>, rows: Int, cols: Int) {
        matrix = array
    }

    override fun query(x1: Int, x2: Int, y1: Int, y2: Int): Interval {
        var max = Double.NEGATIVE_INFINITY
        var posI = 0
        var posJ = 0
        for (i in x1..x2) {
            for (j in y1..y2) {
                if (matrix[i][j] >= max) {
                    max = matrix[i][j]
                    posI = i
                    posJ = j
                }

            }
        }
        return Interval(posI, posJ, max)
    }
}


/**
 * Taken from  https://codeforces.com/blog/entry/45485?locale=ru
 * Stored [ n][ m][ 1+logn][ 1+logm]
 */
class SparseTableRMQ2D : IRMQ2D {

    private lateinit var sparseTable: Array<Array<Array<Array<Interval>>>>
    private lateinit var logs: IntArray
    private lateinit var pows: IntArray


    private constructor()

    private fun fill(accessor: (Int, Int) -> Double) {

    }


    constructor(array: (Int, Int) -> Double, rows: Int, cols: Int) {
        val logN = round(log2(rows.toDouble())).toInt()
        val logM = round(log2(cols.toDouble())).toInt()

        logs = IntArray(max(rows, cols) + 2)
        logs[1] = 0
        for (i in 2..max(rows, cols)) {
            logs[i] = logs[i / 2] + 1
        }

        pows = IntArray(max(rows, cols) + 2)
        pows[0] = 1
        pows[1] = 2
        for (i in 2..max(rows, cols)) {
            pows[i] = pows[i - 1] * 2
        }


        sparseTable = Array(rows) { i ->
            Array(cols) { j ->
                Array(logN + 1) { k1 ->
                    Array(logM + 1) { k2 -> if (k2 == 0 && k1 == 0) Interval(i, j, array(i, j)) else Interval(i, j, Double.NEGATIVE_INFINITY) }
                }
            }
        }

        for (i in 0 until rows) {
            for (log in 1..logM) {
                for (j in 0 until cols) {
                    val pow = pows[log - 1]
                    if (j + pow >= cols) break
                    sparseTable[i][j][0][log] = if (sparseTable[i][j][0][log - 1].score >= sparseTable[i][j + pow][0][log - 1].score)
                        sparseTable[i][j][0][log - 1]
                    else
                        sparseTable[i][j + pow][0][log - 1]
                }
            }
        }

        for (k1 in 1..logN) {
            for (i in 0 until rows) {
                val pow = pows[k1 - 1]
                if (i + pow >= rows) break
                for (k2 in 0..logM) {
                    for (j in 0 until cols) {
                        sparseTable[i][j][k1][k2] = if (sparseTable[i][j][k1 - 1][k2].score >= sparseTable[i + pow][j][k1 - 1][k2].score)
                            sparseTable[i][j][k1 - 1][k2]
                        else sparseTable[i + pow][j][k1 - 1][k2]
                    }
                }
            }
        }


    }


    override fun query(x1: Int, x2: Int, y1: Int, y2: Int): Interval {
        val k1 = logs[x2 - x1 + 1]
        val k2 = logs[y2 - y1 + 1]
        val powk1 = pows[k1]
        val powk2 = pows[k2]
        val q1 = sparseTable[x1][y1][k1][k2]
        val q2 = sparseTable[x2 - powk1 + 1][y1][k1][k2]
        val q3 = sparseTable[x1][y2 - powk2 + 1][k1][k2]
        val q4 = sparseTable[x2 - powk1 + 1][y2 - powk2 + 1][k1][k2]
        val max1 = if (q1.score >= q2.score) q1 else q2
        val max2 = if (q3.score >= q4.score) q3 else q4
        return if (max1.score >= max2.score) max1 else max2
    }
}

//class SparseTableRMQ2DLogsFirst : IRMQ2D {
//
//    private lateinit var sparseTable: Array<Array<Array<DoubleArray>>>
//    private lateinit var logs: IntArray
//    private lateinit var pows: IntArray
//
//
//    private constructor()
//
//
//    constructor(array: (Int, Int) -> Double, rows: Int, cols: Int) {
//        val logN = round(log2(rows.toDouble())).toInt()
//        val logM = round(log2(cols.toDouble())).toInt()
//
//        logs = IntArray(max(rows, cols) + 2)
//        logs[1] = 0
//        for (i in 2..max(rows, cols)) {
//            logs[i] = logs[i / 2] + 1
//        }
//
//        pows = IntArray(max(rows, cols) + 2)
//        pows[0] = 1
//        pows[1] = 2
//        for (i in 2..max(rows, cols)) {
//            pows[i] = pows[i - 1] * 2
//        }
//
//
//        sparseTable = Array(logN + 1) { k1 ->
//            Array(logM + 1) { k2 ->
//                Array(rows) { i ->
//                    DoubleArray(cols) { j -> if (k2 == 0 && k1 == 0) array(i, j) else Double.NEGATIVE_INFINITY }
//                }
//            }
//        }
//
//        for (i in 0 until rows) {
//            for (log in 1..logM) {
//                for (j in 0 until cols) {
//                    val pow = pows[log - 1]
//                    if (j + pow >= cols) break
//                    sparseTable[0][log][i][j] = max(sparseTable[0][log - 1][i][j], sparseTable[0][log - 1][i][j + pow])
//                }
//            }
//        }
//
//        for (k1 in 1..logN) {
//            for (i in 0 until rows) {
//                val pow = pows[k1 - 1]
//                if (i + pow >= rows) break
//                for (k2 in 0..logM) {
//                    for (j in 0 until cols) {
//                        sparseTable[k1][k2][i][j] = max(sparseTable[k1 - 1][k2][i][j], sparseTable[k1 - 1][k2][i + pow][j])
//                    }
//                }
//            }
//        }
//
//
//    }
//
//
//    override fun query(x1: Int, x2: Int, y1: Int, y2: Int): Double {
//        val k1 = logs[x2 - x1 + 1]
//        val k2 = logs[y2 - y1 + 1]
//        val powk1 = pows[k1]
//        val powk2 = pows[k2]
//        val q1 = sparseTable[k1][k2][x1][y1]
//        val q2 = sparseTable[k1][k2][x2 - powk1 + 1][y1]
//        val q3 = sparseTable[k1][k2][x1][y2 - powk2 + 1]
//        val q4 = sparseTable[k1][k2][x2 - powk1 + 1][y2 - powk2 + 1]
//        return max(max(q1, q2), max(q3, q4))
//    }
//
//}
//

