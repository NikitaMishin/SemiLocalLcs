package utils

import java.util.*
import kotlin.collections.HashMap


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
