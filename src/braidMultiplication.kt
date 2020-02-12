import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.math.max

fun naiveMultiplicationBraids(a: AbstractPermutationMatrix, b: AbstractPermutationMatrix)
        : AbstractPermutationMatrix {
    val aDominance = CountingQuery.dominanceMatrix(a, CountingQuery.topRightSummator)
    val bDominance = CountingQuery.dominanceMatrix(b, CountingQuery.topRightSummator)
    val cDominance: Array<Array<Int>> = Array(a.height() + 1) { Array(b.width() + 1) { 0 } }
    for (i in 0 until a.height() + 1) {
        for (k in 0 until b.width() + 1) {
            var tmp = Int.MAX_VALUE
            for (j in 0 until a.width() + 1) {
                tmp = Integer.min(aDominance[i][j] + bDominance[j][k], tmp)

            }
            cDominance[i][k] = tmp
        }
    }

    val c = a.createZeroMatrix(a.height(), b.width())

    for (i in 0 until a.height()) {
        for (j in 0 until b.width()) {
            c[i, j] =
                (cDominance[i][j + 1] + cDominance[i + 1][j] - cDominance[i][j] - cDominance[i + 1][j + 1]) == 1
        }
    }

    return c
}


/**
 * Step for function steady and for path restoring
 */
internal enum class Step {
    UP,
    RIGHT
}

/**
 * Fast multiplication for permutation and subpermutation matrices each with at most n nonzeros.
 * The product is obtained in O(nlogn) time.
 * The details see on page 30.
 */
fun steadyAntWrapper(P: AbstractPermutationMatrix, Q: AbstractPermutationMatrix): AbstractPermutationMatrix {

    fun getPReduced(): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
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


    fun getQReduced(): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
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
        (P.IsStochastic() && Q.IsStochastic()) -> return steadyAnt(P, Q) // if both stochastic then both squared
        else -> {

            var (isPZero, PReduced) = getPReduced()
            var (isQZero, QReduced) = getQReduced()


            if (isPZero || isQZero) return P.createZeroMatrix(P.height(), Q.width())
            var PRed = PReduced!!.first
            var QRed = QReduced!!.first


            var rowIndicesP = mutableListOf<Int>()
            val colIndicesP = mutableListOf<Int>()
            var rowIndicesQ = mutableListOf<Int>()
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

            var requiredExtraRowsP = rowIndicesP.size
            var requiredExtraColsP = colIndicesP.size

            var requiredExtraRowsQ = rowIndicesQ.size
            var requiredExtraColsQ = colIndicesQ.size

            var n = max(
                max(requiredExtraColsP + PRed.height(), requiredExtraColsQ + QRed.height()),
                max(requiredExtraRowsP + PRed.width(), requiredExtraRowsQ + QRed.width())
            )

            //extra diagonal
            var extraDiagP = n - max(requiredExtraColsP + PRed.height(), requiredExtraRowsP + PRed.width())
            var extraDiagQ = n - max(requiredExtraColsQ + QRed.height(), requiredExtraRowsQ + QRed.width())


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
                    //todo kinda tricky
                    res[PReduced.second[p.i - n + PRed.height()]!!, QReduced.second[p.j]!!] = true
                }
            }

            return res


        }


    }


}

/**
 * Fast multiplication for permutation and subpermutation matrices each with at most n nonzeros.
 * The product is obtained in O(nlogn) time.
 * The details see on page 30.
 */
fun steadyAnt(P: AbstractPermutationMatrix, Q: AbstractPermutationMatrix): AbstractPermutationMatrix {

    /**
     *
     */
    fun getP1(colExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
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
     *
     */
    fun getP2(colExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
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
     *
     */
    fun getQ1(rowExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
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

        val matrix = P.createZeroMatrix(height = nexRowPoints.size, width = newColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            nexRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            newColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldCol))

    }

    /**
     *
     */
    fun getQ2(rowExclusive: Int): Pair<Boolean, Pair<AbstractPermutationMatrix, MutableMap<Int, Int>>?> {
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

        val matrix = P.createZeroMatrix(height = nexRowPoints.size, width = newColPoints.size)
        // for speedup
        if (matrix.height() < matrix.width()) {
            nexRowPoints.forEachIndexed { row, col -> matrix[row, col] = col != P.NOPOINT }
        } else {
            newColPoints.forEachIndexed { col, row -> matrix[row, col] = row != P.NOPOINT }
        }

        return Pair(false, Pair(matrix, newToOldCol))
    }

    /**
     *
     */
    fun inverseMapping(
        newToOldX: MutableMap<Int, Int>, newToOldY: MutableMap<Int, Int>, height: Int, width: Int,
        shrinkMatrix: AbstractPermutationMatrix
    ): AbstractPermutationMatrix {

        val matrix = P.createZeroMatrix(height, width)

        for (row in 0 until shrinkMatrix.height()) {
            val col = shrinkMatrix[row, AbstractPermutationMatrix.GetType.ROW]
            if (col != shrinkMatrix.NOPOINT) matrix[newToOldX[row]!!, newToOldY[col]!!] = true
        }
        return matrix
    }


    //base case
    if (P.width() == 1) {
        val m = P.createZeroMatrix(P.height(), Q.width())
        val row = P[0, AbstractPermutationMatrix.GetType.COLUMN]
        val col = Q[0, AbstractPermutationMatrix.GetType.ROW]
        m[row, col] = row != P.NOPOINT && col != Q.NOPOINT
        return m
    }


    val widthP1 = P.width() / 2
    val (P1IsZero, P1) = getP1(widthP1)
    val (P2IsZero, P2) = getP2(widthP1)
    val (Q1IsZero, Q1) = getQ1(widthP1)
    val (Q2IsZero, Q2) = getQ2(widthP1)
    val R1: AbstractPermutationMatrix?
    var R2: AbstractPermutationMatrix?

    //CASE WHEN P1 OR Q1 IS ZERO
    when {
        (P1IsZero || Q1IsZero) && (P2IsZero || Q2IsZero) ->
            return P.createZeroMatrix(P.height(), Q.width())
        (P1IsZero || Q1IsZero) ->
            return inverseMapping(P2!!.second, Q2!!.second, P.height(), Q.width(), steadyAnt(P2.first, Q2.first))

        (P2IsZero || Q2IsZero) ->
            return inverseMapping(P1!!.second, Q1!!.second, P.height(), Q.width(), steadyAnt(P1.first, Q1.first))

        // all non zero products
        else -> {
            R1 = inverseMapping(P1!!.second, Q1!!.second, P.height(), Q.width(), steadyAnt(P1.first, Q1.first))
            R2 = inverseMapping(P2!!.second, Q2!!.second, P.height(), Q.width(), steadyAnt(P2.first, Q2.first))
        }
    }


    val endPos = Position2D(-1, R1.width() + 1)

    //-1 to n+1 and we alredy went from down
    val currentPos = Position2D(R1.height() + 1 - 1, -1)

    var RHi = 0 // now at point <n^+,0^->
    var RLo = 0 // now at point <n^+,0^->

    // queries goes to extende matrix i.e (m+1)x(n+1)
    val countingQuery = CountingQuery()
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
                countingQuery.dominanceSumBottomRightRightMove(posDominanceMatrix.i, posDominanceMatrix.j - 1, RHi, R2)
            RLo = countingQuery.dominanceSumTopLeftRightMove(posDominanceMatrix.i, posDominanceMatrix.j - 1, RLo, R1)
        } else {
            RHi = countingQuery.dominanceSumBottomRightUpMove(posDominanceMatrix.i + 1, posDominanceMatrix.j, RHi, R2)
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