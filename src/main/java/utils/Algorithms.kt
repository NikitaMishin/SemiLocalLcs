package utils

import java.util.*
import kotlin.collections.HashMap
import org.ejml.data.Matrix
import sequenceAlignment.ISemiLocalCombined


/**
 * smawk algorithm implementation. O(n*queryTimeForMatrixElementAccess)
 * @return Indexes of position in a row for each row in matrix
 * Note to get colMinima transpose matrix i/e get[i,j] should return get[j,i] and swap width with height
 * For antimonge use - sign when return get query
 */
fun rowMinima(get: (i: Int, j: Int) -> Double, height: Int, width: Int): IntArray {

    val result = IntArray(height) { -1 }

    fun smawk(rows: IntArray, cols: IntArray) {
        if (rows.size == 0) return

        val stack = Stack<Int>()

        for (col in cols) {
            while (true) {
                if (stack.size == 0) break
                val row = rows[stack.size - 1]
                if (get(row, col) >= get(row, stack.peek())) break
                stack.pop()
            }

            if (stack.size < rows.size) stack.push(col)
        }


        val oddRows = rows.filterIndexed { i, _ -> i % 2 == 1 }.toIntArray()
        smawk(oddRows, stack.toIntArray())

        val colToIndex = HashMap<Int, Int>()
        stack.forEachIndexed { index, value -> colToIndex[value] = index }

        var begin = 0
        val optimizedAccess = stack.toIntArray()
        for (i in 0 until rows.size step 2) {
            val row = rows[i]
            var stop = optimizedAccess.size - 1
            if (i < rows.size - 1) stop = colToIndex[result[rows[i + 1]]]!!
            var argmin = optimizedAccess[begin]
            var min = get(row, argmin)
            for (c in begin + 1..stop) {
                val value = get(row, optimizedAccess[c])
                if (c == begin || value < min) {
                    argmin = optimizedAccess[c]
                    min = value
                }
            }

            result[row] = argmin
            begin = stop
        }
    }

    smawk(IntArray(height) { i: Int -> i }, IntArray(width) { i -> i })
    return result
}


/**
 * smawk algorithm implementation. O(n*queryTimeForMatrixElementAccess)
 * @return Indexes of position in a row for each row in matrix
 * Note to get colMinima transpose matrix i/e get[i,j] should return get[j,i] and swap width with height
 * For antimonge use - sign when return get query
 */
fun colMax(get: (i: Int, j: Int) -> Double, height: Int, width: Int): IntArray {

    val result = IntArray(height) { -1 }

    fun smawk(rows: IntArray, cols: IntArray) {
        if (rows.size == 0) return

        val stack = Stack<Int>()

        for (col in cols) {
            while (true) {
                if (stack.size == 0) break
                val row = rows[stack.size - 1]
                if (get(row, col) >= get(row, stack.peek())) break
                stack.pop()
            }

            if (stack.size < rows.size) stack.push(col)
        }


        val oddRows = rows.filterIndexed { i, _ -> i % 2 == 1 }.toIntArray()
        smawk(oddRows, stack.toIntArray())

        val colToIndex = HashMap<Int, Int>()
        stack.forEachIndexed { index, value -> colToIndex[value] = index }

        var begin = 0
        val optimizedAccess = stack.toIntArray()
        for (i in 0 until rows.size step 2) {
            val row = rows[i]
            var stop = optimizedAccess.size - 1
            if (i < rows.size - 1) stop = colToIndex[result[rows[i + 1]]]!!
            var argmin = optimizedAccess[begin]
            var min = get(row, argmin)
            for (c in begin + 1..stop) {
                val value = get(row, optimizedAccess[c])
                if (c == begin || value < min) {
                    argmin = optimizedAccess[c]
                    min = value
                }
            }

            result[row] = argmin
            begin = stop
        }
    }

    smawk(IntArray(height) { i: Int -> i }, IntArray(width) { i -> i })
    return result
}



//colmax smawk(...).
class ColMaxSmawk<T>(private var numRows:Int,private var numColumns:Int,val semilocal:ISemiLocalCombined<T>,val offset:Int,
                     val size:Int) {

    
    protected lateinit var matrix: Matrix

    protected var numrows: Int = 0

    protected lateinit var row: IntArray

    protected lateinit var row_position: IntArray

    protected var numcols: Int = 0

    protected lateinit var col: IntArray
    
    fun computeColumnMaxima(col_maxima: IntArray) {
        var i: Int


        // create an array of column indexes
        numcols = numColumns
        col = IntArray(numcols)
        i = 0
        while (i < numcols) {
            col[i] = i
            i++
        }

        // create an array of row indexes
        numrows = numRows
        row = IntArray(numrows)
        i = 0
        while (i < numrows) {
            row[i] = i
            i++
        }

        // instantiate an helper array for
        // backward reference of rows
        row_position = IntArray(numrows)

        columnMaxima(col_maxima)
    }

    /**
     * This method implements the SMAWK algorithm to compute the column maxima of a given
     * matrix. It uses the arrays of row and column indexes to performs all operations on
     * this 'fake' copy of the original matrix.
     *
     * <P>The first step is to reduce the matrix to a quadratic size (if necessary). It
     * then delete all odd columns and recursively computes column maxima for this matrix.
     * Finally, using the information computed for the odd columns, it searches for
     * column maxima of the even columns. The column maxima are progressively stored in
     * the <CODE>col_maxima</CODE> array (each recursive call will compute a set of
     * column maxima).</P>
     *
     * @param col_maxima the array of column maxima (the computation result)
     */
    protected fun columnMaxima(col_maxima: IntArray) {
        val original_numrows: Int
        val original_numcols: Int
        var c: Int
        var r: Int
        var max: Int
        var end: Int

        original_numrows = numrows

        if (numrows > numcols) {
            // reduce to a quadratic size by deleting
            // rows that contain no maximum of any column
            reduce()
        }

        // base case: matrix has only one row (and one column)
        if (numrows == 1) {
            // so the first column's maximum is the only row left
            col_maxima[col[0]] = row[0]

            if (original_numrows > numrows) {
                // restore rows of original matrix (deleted on reduction)
                restoreRows(original_numrows)
            }

            return
        }

        // save the number of columns before deleting the odd ones
        original_numcols = numcols

        deleteOddColumns()

        // recursively computes max rows for the remaining even columns
        columnMaxima(col_maxima)

        restoreOddColumns(original_numcols)

        // set up pointers to the original index for all rows
        r = 0
        while (r < numrows) {
            row_position[row[r]] = r
            r++
        }

        // compute max rows for odd columns based on the result of even columns
        c = 1
        while (c < numcols) {
            if (c < numcols - 1)
            // if not last column, search ends
            // at next columns' max row
                end = row_position[col_maxima[col[c + 1]]]
            else
            // if last columnm, search ends
            // at last row
                end = numrows - 1

            // search starts at previous columns' max row
            max = row_position[col_maxima[col[c - 1]]]

            // check all values until the end
            r = max + 1
            while (r <= end) {
                if (valueAt(r, c) > valueAt(max, c))
                    max = r
                r++
            }

            col_maxima[col[c]] = row[max]
            c = c + 2
        }

        if (original_numrows > numrows) {
            // restore rows of original matrix (deleted on reduction)
            restoreRows(original_numrows)
        }
    }

    /**
     * This is a helper method to simplify the call to the <CODE>valueAt</CODE> method
     * of the matrix. It returns the value at row <CODE>r</CODE>, column <CODE>c</CODE>.
     *
     * @param r the row number of the value being retrieved
     * @param c the column number of the value being retrieved
     * @return the value at row <CODE>r</CODE>, column <CODE>c</CODE>
     * @see Matrix.valueAt
     */
    protected fun valueAt(r: Int, c: Int): Double {

        return semilocal.stringSubstring(offset + row[r],  offset +  col[c]- numColumns)
    }

    /**
     * This method simulates a deletion of odd rows by manipulating the <CODE>col</CODE>
     * array of indexes. In fact, nothing is deleted, but the indexes are moved to the end
     * of the array in a way that they can be easily restored by the
     * <CODE>restoreOddColumns</CODE> method using a reverse approach.
     *
     * @see .restoreOddColumns
     */
    protected fun deleteOddColumns() {
        var tmp: Int

        var c = 2
        while (c < numcols) {
            // swap column c with c/2
            tmp = col[c / 2]
            col[c / 2] = col[c]
            col[c] = tmp
            c = c + 2
        }

        numcols = (numcols - 1) / 2 + 1
    }

    /**
     * Restores the <CODE>col</CODE> array of indexes to the state it was before the
     * <CODE>deleteOddColumns</CODE> method was called. It only needs to know how many
     * columns there was originally. The indexes that were moved to the end of the array
     * are restored to their original position.
     *
     * @param original_numcols the number of columns before the odd ones were deleted
     * @see .deleteOddColumns
     */
    protected fun restoreOddColumns(original_numcols: Int) {
        var tmp: Int

        var c = 2 * ((original_numcols - 1) / 2)
        while (c > 0) {
            // swap back column c with c/2
            tmp = col[c / 2]
            col[c / 2] = col[c]
            col[c] = tmp
            c = c - 2
        }

        numcols = original_numcols
    }

    /**
     * This method is the key component of the SMAWK algorithm. It reduces an n x m matrix
     * (n rows and m columns), where n >= m, to an n x n matrix by deleting m - n rows
     * that are guaranteed to have no maximum value for any column. The result is an
     * squared submatrix matrix that contains, for each column c, the row that has the
     * maximum value of c in the original matrix. The rows are deleted with the
     * <CODE>deleteRow</CODE>method.
     *
     * <P>It uses the total monotonicity property of the matrix to identify which rows can
     * safely be deleted.
     *
     * @see .deleteRow
    </P> */
    protected fun reduce() {
        var k = 0
        var reduced_numrows = numrows

        // until there is more rows than columns
        while (reduced_numrows > numcols) {
            if (valueAt(k, k) < valueAt(k + 1, k)) {
                // delete row k
                deleteRow(reduced_numrows, k)
                reduced_numrows--
                k--
            } else {
                if (k < numcols - 1) {
                    k++
                } else {
                    // delete row k+1
                    deleteRow(reduced_numrows, k + 1)
                    reduced_numrows--
                }
            }
        }

        numrows = reduced_numrows
    }

    /**
     * This method simulates a deletion of a row in the matrix during the
     * <CODE>reduce</CODE> operation. It just moves the index to the end of the array in a
     * way that it can be restored afterwards by the <CODE>restoreRows</CODE> method
     * (nothing is actually deleted). Deleted indexes are kept in ascending order.
     *
     * @param reduced_rows the current number of rows in the reducing matrix
     * @param k the index of the row to be deleted
     * @see .restoreRows
     */
    protected fun deleteRow(reduced_rows: Int, k: Int) {
        var r: Int
        val saved_row = row[k]

        r = k + 1
        while (r < reduced_rows) {
            row[r - 1] = row[r]
            r++
        }

        r = reduced_rows - 1
        while (r < numrows - 1 && row[r + 1] < saved_row) {
            row[r] = row[r + 1]
            r++
        }

        row[r] = saved_row
    }

    /**
     * Restores the <CODE>row</CODE> array of indexes to the state it was before the
     * <CODE>reduce</CODE> method was called. It only needs to know how many rows there
     * was originally. The indexes that were moved to the end of the array are restored to
     * their original position.
     *
     * @param original_numrows the number of rows before the reduction was performed
     * @see .deleteRow
     *
     * @see .reduce
     */
    protected fun restoreRows(original_numrows: Int) {
        var r: Int
        var r2: Int
        var s: Int
        var d = numrows

        r = 0
        while (r < d) {
            if (row[r] > row[d]) {
                s = row[d]
                r2 = d
                while (r2 > r) {
                    row[r2] = row[r2 - 1]
                    r2--
                }
                row[r] = s
                d++
                if (d > original_numrows - 1) break
            }
            r++
        }

        numrows = original_numrows
    }
    
}