package longestCommonSubsequence

import utils.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis


/**
 *
 */
interface IBraidMultiplication {
    fun multiply(P: Matrix, Q: Matrix): Matrix
}

/**
 *
 */
interface IStrategyKernelEvaluation {
    fun <T> evaluate(a: List<T>, b: List<T>): Matrix
}

/**
 * Naive braid multiplication via straight conversion:
 * 1) Convert to dominance matrices
 * 2) Apply tropical multiplication for them
 * 3) Take cross difference sum of the processed product
 *    See page ? in book
 */
class NaiveBraidMultiplication : IBraidMultiplication {
    override fun multiply(P: Matrix, Q: Matrix): Matrix {

        val aDominance = CountingQueryLCS.dominanceMatrix(P, CountingQueryLCS.topRightSummator)
        val bDominance = CountingQueryLCS.dominanceMatrix(Q, CountingQueryLCS.topRightSummator)
        val cDominance: Array<Array<Int>> = Array(P.height() + 1) { Array(Q.width() + 1) { 0 } }

        println("unmult")
        val time = measureTimeMillis {
            for (i in 0 until P.height() + 1) {
                for (k in 0 until Q.width() + 1) {
                    var tmp = Int.MAX_VALUE
                    for (j in 0 until P.width() + 1) {
                        tmp = Integer.min(aDominance[i][j] + bDominance[j][k], tmp)

                    }
                    cDominance[i][k] = tmp
                }
            }
            println(cDominance[0][0])
        }

        println("mu tyme beach${time}")


        for (i in 0 until P.height() + 1) {
            for (k in 0 until Q.width() + 1) {
                var tmp = Int.MAX_VALUE
                for (j in 0 until P.width() + 1) {
                    tmp = Integer.min(aDominance[i][j] + bDominance[j][k], tmp)

                }
                cDominance[i][k] = tmp
            }
        }

        val c = P.createZeroMatrix(P.height(), Q.width())

        for (i in 0 until P.height()) {
            for (j in 0 until Q.width()) {
                c[i, j] =
                    (cDominance[i][j + 1] + cDominance[i + 1][j] - cDominance[i][j] - cDominance[i + 1][j + 1]) == 1
            }
        }

        return c
    }
}

/**
 *
 */
class MongeBraidMultiplication:IBraidMultiplication{
    override fun multiply(P: Matrix, Q: Matrix): Matrix {
        val aDominance = CountingQueryLCS.dominanceMatrix(P, CountingQueryLCS.topRightSummator)
        val bDominance = CountingQueryLCS.dominanceMatrix(Q, CountingQueryLCS.topRightSummator)

        val a = MongeMatrix(aDominance.size,bDominance.size)
        val b = MongeMatrix(aDominance.size,bDominance.size)
        aDominance.forEachIndexed { i, it -> it.forEachIndexed { j,value -> a[i,j] = value.toDouble()   } }
        bDominance.forEachIndexed { i, it -> it.forEachIndexed { j,value -> b[i,j] = value.toDouble()   } }

        val c = P.createZeroMatrix(P.height(), Q.width())
        println("unmult")
        val time = measureTimeMillis {

            val c = a*b
            println(c[0,0])
        }

        println("mu tyme beach${time}")


        val cDominance = a * b

        println("mult")

        for (i in 0 until P.height()) {
            for (j in 0 until Q.width()) {
                c[i, j] =
                    (cDominance[i, j + 1] + cDominance[i + 1, j] - cDominance[i, j] - cDominance[i + 1, j + 1]).toInt() == 1
            }
        }

        return c
    }
}


/**
 * Fast multiplication for permutation and subpermutation matrices each with at most n nonzeros.
 * The product is obtained in O(nlogn) time.
 * The details see on page 30.
 */
class SteadyAntMultiplication : IBraidMultiplication {
    /**
     * Step for function steady and for path restoring
     */
    internal enum class Step {
        UP,
        RIGHT
    }

    override fun multiply(P: Matrix, Q: Matrix): Matrix {

        fun getPReduced(): Pair<Boolean, Pair<Matrix, MutableMap<Int, Int>>?> {
            val newToOldRows = mutableMapOf<Int, Int>()
            val oldToNewRows = mutableMapOf<Int, Int>()
            val newRowPoints = mutableListOf<Int>()

            for (row in 0 until P.height()) {
                val col = P[row, AbstractPermutationMatrix.GetType.ROW]
                if (col != P.NOPOINT) {
                    newToOldRows[newRowPoints.size] = row
                    oldToNewRows[row] = newRowPoints.size
                    newRowPoints.add(col)
                }
            }

            //zero matrix get
            if (newRowPoints.size == 0) return Pair(true, null)

            val nextColPoints = mutableListOf<Int>()
            for (col in 0 until P.width()) {
                val oldRow = P[col, AbstractPermutationMatrix.GetType.COLUMN]
                nextColPoints.add(oldToNewRows.getOrDefault(oldRow, P.NOPOINT))
            }

            val matrix = P.createZeroMatrix(height = newRowPoints.size, width = nextColPoints.size)
            // for speedup
            if (matrix.height() < matrix.width()) {
                newRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
            } else {
                nextColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
            }

            return Pair(false, Pair(matrix, newToOldRows))
        }


        fun getQReduced(): Pair<Boolean, Pair<Matrix, MutableMap<Int, Int>>?> {
            val newToOldCol = mutableMapOf<Int, Int>()
            val oldToNewCol = mutableMapOf<Int, Int>()
            val newColPoints = mutableListOf<Int>()

            for (col in 0 until Q.width()) {
                val row = Q[col, AbstractPermutationMatrix.GetType.COLUMN]

                if (row != Q.NOPOINT) {
                    newToOldCol[newColPoints.size] = col
                    oldToNewCol[col] = newColPoints.size
                    newColPoints.add(row)
                }
            }
            if (newColPoints.size == 0) return Pair(true, null)

            val nexRowPoints = mutableListOf<Int>()
            for (row in 0 until Q.height()) {
                val oldCol = Q[row, AbstractPermutationMatrix.GetType.ROW]
                nexRowPoints.add(oldToNewCol.getOrDefault(oldCol, Q.NOPOINT))
            }

            val matrix = P.createZeroMatrix(height = nexRowPoints.size, width = newColPoints.size)
            // for speedup
            if (matrix.height() < matrix.width()) {
                nexRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
            } else {
                newColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
            }
            return Pair(false, Pair(matrix, newToOldCol))
        }


        if (P.width() != Q.height()) {
            throw  IllegalArgumentException("Wrong dimensions: P width is ${P.width()} and Q height is ${Q.height()}")
        }


        when {
            (P.isStochastic() && Q.isStochastic()) -> return steadyAnt(P, Q) // if both stochastic then both squared
            else -> {

                val (isPZero, PReduced) = getPReduced()
                val (isQZero, QReduced) = getQReduced()


                if (isPZero || isQZero) return P.createZeroMatrix(P.height(), Q.width())
                val PRed = PReduced!!.first
                val QRed = QReduced!!.first


                val rowIndicesP = mutableListOf<Int>()
                val colIndicesP = mutableListOf<Int>()
                val rowIndicesQ = mutableListOf<Int>()
                val colIndicesQ = mutableListOf<Int>()

                for (row in 0 until PRed.height()) {
                    if (PRed[row, AbstractPermutationMatrix.GetType.ROW] == PRed.NOPOINT) rowIndicesP.add(row)
                }

                for (pos in 0 until PRed.width()) {
                    if (PRed[pos, AbstractPermutationMatrix.GetType.COLUMN] == P.NOPOINT) colIndicesP.add(pos)
                }


                for (pos in 0 until QRed.height()) {
                    if (QRed[pos, AbstractPermutationMatrix.GetType.ROW] == P.NOPOINT) rowIndicesQ.add(pos)

                }


                for (col in 0 until QRed.width()) {
                    if (QRed[col, AbstractPermutationMatrix.GetType.COLUMN] == P.NOPOINT) colIndicesQ.add(col)
                }

                val requiredExtraRowsP = rowIndicesP.size
                val requiredExtraColsP = colIndicesP.size

                val requiredExtraRowsQ = rowIndicesQ.size
                val requiredExtraColsQ = colIndicesQ.size

                val n = max(
                    max(requiredExtraColsP + PRed.height(), requiredExtraColsQ + QRed.height()),
                    max(requiredExtraRowsP + PRed.width(), requiredExtraRowsQ + QRed.width())
                )

                //extra diagonal
                val extraDiagP = n - max(requiredExtraColsP + PRed.height(), requiredExtraRowsP + PRed.width())
                val extraDiagQ = n - max(requiredExtraColsQ + QRed.height(), requiredExtraRowsQ + QRed.width())


                val PCap = P.createZeroMatrix(n, n)

                for (i in 0 until extraDiagP) {
                    PCap[i, i] = true
                }

                //shifted on extraDiagP in both directions
                for ((shift, cols) in colIndicesP.withIndex())
                    PCap[extraDiagP + shift, cols + extraDiagP] = true

                for ((shift, rows) in rowIndicesP.withIndex())
                    PCap[rows + extraDiagP + colIndicesP.size, extraDiagP + PRed.width() + shift] = true

                for (pos in PRed)
                    PCap[pos.i + extraDiagP + colIndicesP.size, pos.j + extraDiagP] = true


                val QCap = Q.createZeroMatrix(n, n)


                //rows
                for ((shift, cols) in colIndicesQ.withIndex())
                    QCap[shift, cols] = true


                for (pos in QRed)
                    QCap[pos.i + colIndicesQ.size, pos.j] = true


                for ((shift, rows) in rowIndicesQ.withIndex())
                    QCap[rows + colIndicesQ.size, QRed.width() + shift] = true


                for (i in 0 until extraDiagQ) {
                    QCap[n - i - 1, n - i - 1] = true
                }


                val resCap = steadyAnt(PCap, QCap)

                val res = P.createZeroMatrix(P.height(), Q.width())


                for (p in resCap) {
                    if (p.j < QRed.width() && p.i >= n - PRed.height()) {
                        //kinda tricky
                        res[PReduced.second[p.i - n + PRed.height()]!!, QReduced.second[p.j]!!] = true
                    }
                }

                return res


            }


        }
    }

    /**
     * returns squeezed submatrix from P to colExclusive  after deletion of zero rows
     */
    private fun getP1(P: Matrix, colExclusive: Int): Pair<Boolean, Pair<Matrix, MutableMap<Int, Int>>?> {
        val newToOldRows = mutableMapOf<Int, Int>()
        val oldToNewRows = mutableMapOf<Int, Int>()
        val newRowPoints = mutableListOf<Int>()

        for (row in 0 until P.height()) {
            val col = P[row, AbstractPermutationMatrix.GetType.ROW]
            if (col < colExclusive && col != P.NOPOINT) {
                newToOldRows[newRowPoints.size] = row
                oldToNewRows[row] = newRowPoints.size
                newRowPoints.add(col)
            }
        }

        //zero matrix get
        if (newRowPoints.size == 0) return Pair(true, null)

        val nextColPoints = mutableListOf<Int>()
        for (col in 0 until colExclusive) {
            val oldRow = P[col, AbstractPermutationMatrix.GetType.COLUMN]
            nextColPoints.add(oldToNewRows.getOrDefault(oldRow, P.NOPOINT))
        }

        val matrix = P.createZeroMatrix(height = newRowPoints.size, width = nextColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            newRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            nextColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldRows))
    }

    /**
     * returns squeezed submatrix from P from colExclusive after deletion of zero rows
     */
    private fun getP2(P: Matrix, colExclusive: Int): Pair<Boolean, Pair<Matrix, MutableMap<Int, Int>>?> {
        val newToOldRows = mutableMapOf<Int, Int>()
        val oldToNewRows = mutableMapOf<Int, Int>()
        val newRowPoints = mutableListOf<Int>()

        for (row in 0 until P.height()) {
            val col = P[row, AbstractPermutationMatrix.GetType.ROW]
            if (col >= colExclusive && col != P.NOPOINT) {
                newToOldRows[newRowPoints.size] = row
                oldToNewRows[row] = newRowPoints.size
                newRowPoints.add(col - colExclusive)
            }
        }

        //zero matrix get
        if (newRowPoints.size == 0) return Pair(true, null)

        val nextColPoints = mutableListOf<Int>()

        for (col in colExclusive until P.width()) {
            val oldRow = P[col, AbstractPermutationMatrix.GetType.COLUMN]
            nextColPoints.add(oldToNewRows.getOrDefault(oldRow, P.NOPOINT))
        }

        val matrix = P.createZeroMatrix(height = newRowPoints.size, width = nextColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            newRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            nextColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldRows))
    }

    /**
     * returns squeezed submatrix from Q to rowExclusive after deletion of zero cols
     */
    private fun getQ1(Q: Matrix, rowExclusive: Int): Pair<Boolean, Pair<Matrix, MutableMap<Int, Int>>?> {
        val newToOldCol = mutableMapOf<Int, Int>()
        val oldToNewCol = mutableMapOf<Int, Int>()
        val newColPoints = mutableListOf<Int>()

        for (col in 0 until Q.width()) {
            val row = Q[col, AbstractPermutationMatrix.GetType.COLUMN]

            if (row != Q.NOPOINT && row < rowExclusive) {
                newToOldCol[newColPoints.size] = col
                oldToNewCol[col] = newColPoints.size
                newColPoints.add(row)
            }
        }
        if (newColPoints.size == 0) return Pair(true, null)

        val nexRowPoints = mutableListOf<Int>()
        for (row in 0 until rowExclusive) {
            val oldCol = Q[row, AbstractPermutationMatrix.GetType.ROW]
            nexRowPoints.add(oldToNewCol.getOrDefault(oldCol, Q.NOPOINT))
        }

        val matrix = Q.createZeroMatrix(height = nexRowPoints.size, width = newColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            nexRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != Q.NOPOINT }
        } else {
            newColPoints.forEachIndexed { col, row -> matrix[row, col] = row != Q.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldCol))

    }

    /**
     * returns squeezed submatrix from Q from rowExclusive after deletion of zero cols
     */
    private fun getQ2(Q: Matrix, rowExclusive: Int): Pair<Boolean, Pair<Matrix, MutableMap<Int, Int>>?> {
        val newToOldCol = mutableMapOf<Int, Int>()
        val oldToNewCol = mutableMapOf<Int, Int>()
        val newColPoints = mutableListOf<Int>()

        for (col in 0 until Q.width()) {
            val row = Q[col, AbstractPermutationMatrix.GetType.COLUMN]

            if (row != Q.NOPOINT && row >= rowExclusive) {
                newToOldCol[newColPoints.size] = col
                oldToNewCol[col] = newColPoints.size
                newColPoints.add(row - rowExclusive)
            }
        }

        if (newColPoints.size == 0) return Pair(true, null)

        val nexRowPoints = mutableListOf<Int>()

        for (row in rowExclusive until Q.height()) {
            val oldCol = Q[row, AbstractPermutationMatrix.GetType.ROW]
            nexRowPoints.add(oldToNewCol.getOrDefault(oldCol, Q.NOPOINT))
        }

        val matrix = Q.createZeroMatrix(height = nexRowPoints.size, width = newColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            nexRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != Q.NOPOINT }
        } else {
            newColPoints.forEachIndexed { col, row -> matrix[row, col] = row != Q.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldCol))
    }

    /**
     * inverse remapping from squeezed matrix x to the new one
     */
    private fun inverseMapping(
        P: Matrix,
        newToOldX: MutableMap<Int, Int>, newToOldY: MutableMap<Int, Int>,
        height: Int, width: Int, shrinkMatrix: Matrix
    ): Matrix {

        val matrix = P.createZeroMatrix(height, width)

        for (row in 0 until shrinkMatrix.height()) {
            val col = shrinkMatrix[row, AbstractPermutationMatrix.GetType.ROW]
            if (col != shrinkMatrix.NOPOINT) matrix[newToOldX[row]!!, newToOldY[col]!!] = true
        }
        return matrix
    }


    /**
     * Actual steady ant multiplication.
     * Works only for squared permutation matrices
     */
    internal fun steadyAnt(P: Matrix, Q: Matrix): Matrix {


        //base case
        if (P.width() == 1) {
            val m = P.createZeroMatrix(P.height(), Q.width())
            val row = P[0, AbstractPermutationMatrix.GetType.COLUMN]
            val col = Q[0, AbstractPermutationMatrix.GetType.ROW]
            m[row, col] = row != P.NOPOINT && col != Q.NOPOINT
            return m
        }


        val widthP1 = P.width() / 2
        val (P1IsZero, P1) = getP1(P, widthP1)
        val (P2IsZero, P2) = getP2(P, widthP1)
        val (Q1IsZero, Q1) = getQ1(Q, widthP1)
        val (Q2IsZero, Q2) = getQ2(Q, widthP1)
        val R1: AbstractPermutationMatrix?
        val R2: AbstractPermutationMatrix?

        //CASE WHEN P1 OR Q1 IS ZERO
        when {
            (P1IsZero || Q1IsZero) && (P2IsZero || Q2IsZero) ->
                return P.createZeroMatrix(P.height(), Q.width())
            (P1IsZero || Q1IsZero) ->
                return inverseMapping(P, P2!!.second, Q2!!.second, P.height(), Q.width(), steadyAnt(P2.first, Q2.first))

            (P2IsZero || Q2IsZero) ->
                return inverseMapping(P, P1!!.second, Q1!!.second, P.height(), Q.width(), steadyAnt(P1.first, Q1.first))

            // all non zero products
            else -> {
                R1 = inverseMapping(P, P1!!.second, Q1!!.second, P.height(), Q.width(), steadyAnt(P1.first, Q1.first))
                R2 = inverseMapping(P, P2!!.second, Q2!!.second, P.height(), Q.width(), steadyAnt(P2.first, Q2.first))
            }
        }


        val endPos = Position2D(-1, R1.width() + 1)

        //-1 to n+1 and we already went from down
        val currentPos = Position2D(R1.height() + 1 - 1, -1)

        var RHi = 0 // now at point <n^+,0^->
        var RLo = 0 // now at point <n^+,0^->

        // queries goes to extended matrix i.e (m+1)x(n+1)
        val countingQuery = CountingQueryLCS()
        val goodPoints = mutableListOf<Position2D<Int>>()


        var step = Step.UP
        while (currentPos != endPos) {

            if (currentPos.i == 0) {
                currentPos.j += 1
                break
            }

            // go
            val posDominanceMatrix = Position2D(currentPos.i - 1, currentPos.j + 1)

            //prev step
            if (step == Step.RIGHT) {

                RHi =
                    countingQuery.dominanceSumBottomRightRightMove(
                        posDominanceMatrix.i,
                        posDominanceMatrix.j - 1,
                        RHi,
                        R2
                    )
                RLo =
                    countingQuery.dominanceSumTopLeftRightMove(posDominanceMatrix.i, posDominanceMatrix.j - 1, RLo, R1)
            } else {
                RHi =
                    countingQuery.dominanceSumBottomRightUpMove(posDominanceMatrix.i + 1, posDominanceMatrix.j, RHi, R2)
                RLo = countingQuery.dominanceSumTopLeftUpMove(posDominanceMatrix.i + 1, posDominanceMatrix.j, RLo, R1)
            }

            when {
                RHi - RLo < 0 -> {
                    // go right
                    step = Step.RIGHT
                    currentPos.j++
                }
                RHi - RLo == 0 -> {
                    step = Step.UP
                    currentPos.i--;
                }
                else -> {
                    throw Exception("Impossible case:${R1.width()} ${R1.height()}")
                }
            }


            if (posDominanceMatrix.j > 0) {
                val deltaAboveLeft =
                    countingQuery.dominanceSumBottomRightLeftMove(
                        posDominanceMatrix.i,
                        posDominanceMatrix.j,
                        RHi,
                        R2
                    ) - countingQuery.dominanceSumTopLeftLeftMove(posDominanceMatrix.i, posDominanceMatrix.j, RLo, R1)
                val deltaBelowRight =
                    countingQuery.dominanceSumBottomRightDownMove(
                        posDominanceMatrix.i,
                        posDominanceMatrix.j,
                        RHi,
                        R2
                    ) - countingQuery.dominanceSumTopLeftDownMove(posDominanceMatrix.i, posDominanceMatrix.j, RLo, R1)


                if (deltaAboveLeft < 0 && deltaBelowRight > 0) {
                    goodPoints.add(Position2D(posDominanceMatrix.i, posDominanceMatrix.j - 1))
                }
            }

        }


        //filter in R1 and R2
        goodPoints.forEach { p ->
            R1.resetInColumn(p.j)
            R1.resetInRow(p.i)
            R2.resetInColumn(p.j)
            R2.resetInRow(p.i)
        }


        // make better
        for (nonzeroPointsPosR1 in R2) {
            R1[nonzeroPointsPosR1.i, nonzeroPointsPosR1.j] = true
        }
        goodPoints.forEach {
            R1[it.i, it.j] = true
        }

        return R1

    }
}


/**
 * Braid multiplication for intersected (of size k) braids. see page 60
 * @param  (sub)permutation matrix P
 * @param  (sub)permutation matrix Q
 * @param k amount of strands that intersected (a common part of two braids)
 * @return  (sub)permutation matrix of dimension  (P.height +   Q.height - k) X (P.width +  Q.width - k)
 */
fun staggeredStickyMultiplication(P: Matrix, Q: Matrix, k: Int): Matrix {
    if (k < 0 || k > min(P.width(), Q.height())) throw IllegalArgumentException("0<=k<=${P.width()},${Q.height()}")

    val braidMultiplication = SteadyAntMultiplication()

    when {
        k == 0 -> {
            val res = P.createZeroMatrix(P.height() + Q.height(), P.width() + Q.width())
            for (p in P) res[p.i + Q.height(), p.j + Q.width()] = true
            for (q in Q) res[q.i, q.j] = true
            return res
        }
        k == P.width() && k == Q.height() -> return braidMultiplication.multiply(P, Q)
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
            val reducedRes = braidMultiplication.multiply(reducedP, reducedQ)


            for (p in reducedRes) res[Q.height() - k + p.i, p.j] = true

            for (q in Q)
                if (q.i < Q.height() - k) res[q.i, q.j] = true


            for (p in P)
                if (p.j >= k) res[p.i + Q.height() - k, p.j + Q.width() - k] = true

            return res
        }
    }
}


abstract class AbstractMongeMatrix() {

    abstract fun width(): Int
    abstract fun height(): Int

    /**
     * from - to plus?
     */
    abstract operator fun set(i: Int, j: Int, value: Double)

    /**
     *
     */
    abstract operator fun get(i: Int, j: Int): Double

    /**
     *
     */
    abstract fun getImplicitMatrix(): IStochasticMatrix

    abstract fun createNewMatrix(height: Int, width: Int): AbstractMongeMatrix


    /**
     * Checking that monge property is satisfied.
     * An m-by-n matrix is said to be a Monge array if, for all  i, j ,k , l such that 1<=i<k<=m and 1<=j<l<=n
     * A[i,j] + A[k,l] <= A[i,l] + A[k,j]
     */
    fun isMongePropertySatisified(): Boolean {
        for (rowNum1 in 0 until height()) {
            for (rowNum2 in rowNum1 + 1 until height()) {
                for (colNum1 in 0 until width()) {
                    for (colNum2 in colNum1 + 1 until width()) {
                        val diagL = this[rowNum1, colNum1]
                        val diagR = this[rowNum2, colNum2]
                        val antiDiagL = this[rowNum2, colNum1]
                        val antiDiagR = this[rowNum1, colNum2]
                        if (antiDiagL + antiDiagR < diagL + diagR) {
                            println("$antiDiagL + $antiDiagR < $diagL + $diagR")
                            return false
                        }
                    }
                }
            }
        }
        return true
    }


    /**
     * utils.Matrix-vector multiplication A*b = c
     * Note that c could be obtained by following:
     * <code>
     *     vectorIndices =
     *     val indices = A.matrixVectorMult(vector)
     *     val c = indices.forEachIndexed{row,col ->
     *        A[row,col] + vector[col]
     *     }
     *     c[row] = A[row, indices]  + vector[j]
     * </code>
     * smawk algorithm implementation. O(n*queryTimeForMatrixElementAccess)
     * @return Indexes of position in a row for each row in matrix
     */
    fun matrixVectorMult(vector: MutableList<Double>): MutableList<Int> {
        val result = MutableList(this.height()) { -1 }

        fun smawk(rows: MutableList<Int>, cols: MutableList<Int>) {
            if (rows.isEmpty()) return

            val stack = Stack<Int>()
            for (col in cols) {
                while (true) {
                    if (stack.size == 0) break
                    val row = rows[stack.size - 1]
                    if (get(row, col) + vector[col] >= get(row, stack.peek()) + vector[stack.peek()]) break
                    stack.pop()
                }

                if (stack.size < rows.size) stack.push(col)
            }


            val oddRows = rows.filterIndexed { i, _ -> i % 2 == 1 }.toMutableList()
            smawk(oddRows, stack)

            val colToIndex = HashMap<Int, Int>()
            stack.forEachIndexed { index, value -> colToIndex[value] = index }

            var begin = 0
            val optimizedAccess = stack.toIntArray()
            for (i in 0 until rows.size step 2) {
                val row = rows[i]
                var stop = optimizedAccess.size - 1
                if (i < rows.size - 1) stop = colToIndex[result[rows[i + 1]]]!!
                var argmin = optimizedAccess[begin]
                var min = get(row, argmin) + vector[argmin]
                for (c in begin + 1..stop) {
                    val value = get(row, optimizedAccess[c]) + vector[optimizedAccess[c]]
                    if (c == begin || value < min) {
                        argmin = optimizedAccess[c]
                        min = value
                    }
                }

                result[row] = argmin
                begin = stop
            }
        }

        smawk((0 until height()).toMutableList(), (0 until width()).toMutableList())
        return result
    }


    operator fun times(b: AbstractMongeMatrix): AbstractMongeMatrix {
        val res = this.createNewMatrix(height(), b.width())

        //O(n)* smawk
        for (col in 0 until b.width()) {
            val bVector = (0 until b.height()).map { row -> b[row, col] }.toMutableList()
            val c = matrixVectorMult(bVector)
            c.forEachIndexed { row, j ->
                res[row, col] = get(row, j) + bVector[j]
            }
        }
        return res
    }


}


class MongeMatrix : AbstractMongeMatrix {
    private val matrix: Array<DoubleArray>

    constructor(height: Int, width: Int) {
        matrix = Array(height) { DoubleArray(width) { Double.POSITIVE_INFINITY } }
    }

    override fun width(): Int = matrix[0].size

    override fun height(): Int = matrix.size

    override fun set(i: Int, j: Int, value: Double) {
        matrix[i][j] = value
    }

    override fun get(i: Int, j: Int): Double = matrix[i][j]

    override fun getImplicitMatrix(): IStochasticMatrix {
        TODO()
    }

    override fun createNewMatrix(height: Int, width: Int): AbstractMongeMatrix = MongeMatrix(height, width)


}


/**
 * Monge multiplication for intersected (of size k) braids. see page ?
 * @param  mongeMatrix P
 * @param  mongeMatrix Q
 * @param k amount of strands that intersected (a common part of two braids)
 * @return  matrix of dimension  (P.height +   Q.height - k -1) X (P.width +  Q.width - k-1)
 */
fun staggeredExplicitMultiplication(p: AbstractMongeMatrix, q: AbstractMongeMatrix, k: Int): AbstractMongeMatrix {

    val pI = q.height() - k -1
    val qI = p.width() - k -1


    val pExtended = p.createNewMatrix(p.height() + pI, p.width() + pI)
    val qExtended = q.createNewMatrix(q.height() + qI, q.width() + qI)


    // fill left
    // for that we know that it id matrix
    // 1 0 0 ...
    // 0 1 ...
    // .......
    // 0 0 ... 1
    // then ->
    for (row in 0 until pExtended.height()) {
        for (col in 0 until pI + 1) {
            pExtended[row, col] = max(col - row, 0).toDouble()
        }
    }



//    for k = 2
    // pI+1 =3
    // since we know that both matrices are monge then
    // top right jusst be sometehin + top of p top
    for (row in 0 until pI) {
        for (col in pI + 1 until pExtended.width()) {
            pExtended[row, col] = pExtended[row,pI] + p[0, col - pI]
        }
    }


    //its just as it is
    for (row in pI until pExtended.height()) {
        for (col in pI until pExtended.width()) {
            pExtended[row, col] = p[row - pI, col - pI]
        }
    }





    //fill top left: just copu
    for (row in 0 until q.height()) {
        for (col in 0 until q.width()) {
            qExtended[row, col] = q[row, col]
        }
    }

    //fill bottom left - just zeroes
    for (row in q.height() until qExtended.height()) {
        for (col in 0 until q.width() - 1) {
            qExtended[row, col] = 0.0
        }
    }

    //fill bottom right
    //qI=2
    for (row in 0 until qI + 1) {
        for (col in 0 until qI + 1) {
            qExtended[q.height() - 1 + row, q.width() - 1 + col] = max(col - row, 0).toDouble()
        }
    }

    //fill top right in permutation its just all zeroes + edge left and bottom
    for (row in 0 until q.height() - 1) {
        for (col in q.width() until qExtended.width()) {
            qExtended[row, col] = qExtended[row, q.width() - 1] + qExtended[q.height() - 1, col]
        }
    }

//    println("P")
//    for(i in 0 until  p.height()){
//        for (j in 0 until  p.width()){
//            print("${p[i,j]}  ")
//        }
//        println()
//    }
//
//    println()
//    for(i in 0 until  pExtended.height()){
//        for (j in 0 until  pExtended.width()){
//            print("${pExtended[i,j]}  ")
//        }
//        println()
//    }
//
//    println()
//
//    for(i in 0 until  qExtended.height()){
//        for (j in 0 until  qExtended.width()){
//            print("${qExtended[i,j]}  ")
//        }
//        println()
//    }
//
//    println()
    return pExtended * qExtended


}


