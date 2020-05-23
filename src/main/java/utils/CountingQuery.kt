package utils


/**
 * Class for dominance sum counting queries with O(1) for permutation and subpermutation matrices.
 * Given the sum in position i,j each function returns sum in adjacent position for different type of dominance sum.
 * The prefix (SUM......) determines type of dominance sum whereas prefix (...Move)  determines adjacent position.
 */
class CountingQueryLCS {
    /**
     * see class definition
     */
    fun dominanceSumTopLeftLeftMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        var jCap = j
        if (jCap == 0) return sum
        jCap--

        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap >= i) 1 else 0
    }

    /**
     * see class definition
     */
    fun dominanceSumTopLeftDownMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        val iCap = i
        if (iCap >= permMatrix.height()) {
            return 0
        }
        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap >= j) -1 else 0
    }


    /**
     * see class definition
     */
    fun dominanceSumTopLeftUpMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        var iCap = i
        if (iCap == 0) {
            return sum
        }

        iCap -= 1

        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap >= j) 1 else 0
    }

    /**
     * see class definition
     */
    fun dominanceSumTopLeftRightMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {

        if (j >= permMatrix.width()) return 0

        val iCap = permMatrix[j, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) 0 else -1
    }

    /**
     * see class definition
     */
    fun dominanceSumBottomRightLeftMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        var jCap = j
        if (jCap == 0) return sum
        jCap--

        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) -1 else 0
    }

    /**
     * see class definition
     */
    fun dominanceSumBottomRightDownMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        if (i >= permMatrix.height()) {
            return 0
        }
        val jCap = permMatrix[i, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap < j) 1 else 0
    }

    /**
     * see class definition
     */
    fun dominanceSumBottomRightUpMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        var iCap = i

        if (iCap == 0) {
            return sum
        }
        iCap -= 1

        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum

        return sum + if (jCap >= j) 0 else -1
    }

    /**
     * see class definition
     */
    fun dominanceSumBottomRightRightMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        if (j >= permMatrix.width()) return 0

        val iCap = permMatrix[j, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) 1 else 0
    }


    /**
     * see class definition
     */
    fun dominanceSumTopRightLeftMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        var jCap = j
        if (jCap == 0) return sum
        jCap--

        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap >= i) -1 else 0
    }

    /**
     * see class definition
     */
    fun dominanceSumTopRightDownMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        val iCap = i
        if (iCap >= permMatrix.height()) {
            return 0
        }
        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap < j) -1 else 0
    }


    /**
     * see class definition
     */
    fun dominanceSumTopRightUpMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {
        var iCap = i
        if (iCap == 0) {
            return sum
        }

        iCap -= 1

        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap < j) 1 else 0
    }

    /**
     * see class definition
     */
    fun dominanceSumTopRightRightMove(i: Int, j: Int, sum: Int, permMatrix: Matrix): Int {

        if (j >= permMatrix.width()) return 0

        val iCap = permMatrix[j, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap >= i) 1 else 0
    }


    companion object {
        val topRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i >= row && pos.j < col }

        val topLeftSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i >= row && pos.j >= col } // below-right

        val bottomRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i < row && pos.j < col } //above-left


        fun dominanceMatrix(matrix: Matrix, func: (Position2D<Int>, Int, Int) -> Boolean)
                : Array<Array<Int>> {
            //half sizes for permutation matrix
            // and for croos is integer
            val dominanceMatrix = Array(matrix.height() + 1) { Array(matrix.width() + 1) { 0 } }
            for (row in dominanceMatrix.indices) {
                for (col in dominanceMatrix[0].indices) {
                    for (pos in matrix) {
                        if (func(pos, row, col)) dominanceMatrix[row][col]++
                    }
                }
            }
            return dominanceMatrix
        }

    }

}


/**
 * Class for dominance sum counting queries with O(v) for v-subbistochastic matrices.
 * Given the sum in position i,j each function returns sum in adjacent position for different type of dominance sum.
 * The prefix (SUM......) determines type of dominance sum whereas prefix (...Move)  determines adjacent position.
 */
class CountingQuerySA {

    /**
     * see class definition
     */
    fun dominanceSumTopRightLeftMove(i: Int, j: Int, sum: Double, matrix: IStochasticMatrix): Double {
        var jCap = j
        if (jCap == 0) return sum
        jCap--
        return sum - matrix.getAllInCol(jCap).filter { it.i >= i }.map { it.value }.sum() / matrix.v.toDouble()

    }

    /**
     * see class definition
     */
    fun dominanceSumTopRightDownMove(i: Int, j: Int, sum: Double, matrix: IStochasticMatrix): Double {
        val iCap = i
        if (iCap >= matrix.height()) return 0.0
        return sum - matrix.getAllInRow(iCap).filter { it.j < j }.map { it.value }.sum().toDouble() / matrix.v
    }

    /**
     * see class definition
     */
    fun dominanceSumTopRightUpMove(i: Int, j: Int, sum: Double, matrix: IStochasticMatrix): Double {
        var iCap = i
        if (iCap == 0) {
            return sum
        }
        iCap -= 1
        return sum + matrix.getAllInRow(iCap).filter { it.j < j }.map { it.value }.sum().toDouble() / matrix.v
    }

    /**
     * see class definition
     */
    fun dominanceSumTopRightRightMove(i: Int, j: Int, sum: Double, matrix: IStochasticMatrix): Double {
        if (j >= matrix.width()) return 0.0
        return sum + matrix.getAllInCol(j).filter { it.i >= i }.map { it.value }.sum().toDouble() / matrix.v


//        if (j >= permMatrix.width()) return 0
//
//        val iCap = permMatrix[j, utils.AbstractPermutationMatrix.GetType.COLUMN]
//        if (iCap == permMatrix.NOPOINT) return sum
//        return sum + if (iCap >= i) 1 else 0

    }

    companion object {
        val topRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i >= row && pos.j < col }

        fun dominanceMatrix(
            matrix: IStochasticMatrix,
            func: (Position2D<Int>, Int, Int) -> Boolean
        ): Array<Array<Double>> {
            val dominanceMatrix = Array(matrix.height() + 1) { Array(matrix.width() + 1) { 0.0 } }

            for (row in dominanceMatrix.indices) {
                for (col in dominanceMatrix[0].indices) {
                    for (i in 0 until matrix.height()) {
                        for (pos in matrix.getAllInRow(i)) {
                            if (func(pos, row, col)) dominanceMatrix[row][col] = dominanceMatrix[row][col] + pos.value
                        }
                    }
                }
            }

            for (row in dominanceMatrix.indices) {
                for (col in dominanceMatrix[0].indices) {
                    dominanceMatrix[row][col] = dominanceMatrix[row][col] / matrix.v
                }
            }
            return dominanceMatrix
        }
    }


}