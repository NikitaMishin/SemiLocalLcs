/**
 * Class for dominance sum counting queries with O(1) for permutation and subpermutation matrices.
 * Given the sum in position i,j each function returns sum in adjacent position for differnet type of dominance sum.
 * The prefix (SUM......) determines type of dominance sum whereas prefix (...Move)  determines adjacent posititon
 */
class CountingQuery {
    /**
     * see class definition
     */
    fun dominanceSumTopLeftLeftMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
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
    fun dominanceSumTopLeftDownMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
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
    fun dominanceSumTopLeftUpMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
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
    fun dominanceSumTopLeftRightMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {

        val jCap = j
        if (jCap >= permMatrix.width()) return 0

        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) 0 else -1
    }

    /**
     * see class definition
     */
    fun dominanceSumBottomRightLeftMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
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
    fun dominanceSumBottomRightDownMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        val iCap = i
        if (iCap >= permMatrix.height()) {
            return 0
        }
        val jCap = permMatrix[iCap, AbstractPermutationMatrix.GetType.ROW]
        if (jCap == permMatrix.NOPOINT) return sum
        return sum + if (jCap < j) 1 else 0
    }

    /**
     * see class definition
     */
    fun dominanceSumBottomRightUpMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
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
    fun dominanceSumBottomRightRightMove(i: Int, j: Int, sum: Int, permMatrix: AbstractPermutationMatrix): Int {
        val jCap = j
        if (jCap >= permMatrix.width()) return 0

        val iCap = permMatrix[jCap, AbstractPermutationMatrix.GetType.COLUMN]
        if (iCap == permMatrix.NOPOINT) return sum
        return sum + if (iCap < i) 1 else 0
    }

    companion object {
        val topRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i >= row && pos.j < col }

        val topLeftSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i >= row && pos.j >= col } // below-right

        val bottomRightSummator: (Position2D<Int>, row: Int, col: Int) -> Boolean =
            { pos, row, col -> pos.i < row && pos.j < col } //above-left


        fun dominanceMatrix(matrix: AbstractPermutationMatrix, func: (Position2D<Int>, Int, Int) -> Boolean)
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
