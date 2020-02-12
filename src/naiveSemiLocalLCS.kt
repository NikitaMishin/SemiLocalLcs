import kotlin.math.max


data class NaiveSemiLocalLCS<Element>(val a: List<Element>, val b: List<Element>) :
    ISemiLocalLCS where Element : Comparable<Element> {
    // a of size m
    // b of size n
    private var m = a.size
    private var n = b.size
    private var aSymbolString: List<Symbol<Element>> =
        a.map { Symbol(it, SymbolType.AlphabetSymbol) }
    private var bSymbolString: List<Symbol<Element>> =
        a.map { Symbol(it, SymbolType.WildCardSymbol) } +
                b.map { Symbol(it, SymbolType.AlphabetSymbol) } +
                a.map { Symbol(it, SymbolType.WildCardSymbol) }

    /**
     * Explicit semiLocal LCS matrix H. THe full definition see on page 53
     *  H_{a,b}\[-m : n | 0 : m + n \] i.e i from -m to n and j from 0 to m + n
     */
    internal var semiLocalLCSMatrix: Array<Array<Int>> = Array(a.size + b.size + 1)
    { i ->
        Array(a.size + b.size + 1)
        { j ->
            if (j <= (i - m)) {
                j - i + m
            } else {
                lcs(i, j + m)
            }
        }
    }

    //IS this error TODO
    override fun stringSubstringLCS(i: Int, j: Int): Int {
        if (i < 0 || i > n || j < 0 || j > n) return -1
        return semiLocalLCSMatrix[i + m][j]
    }

    override fun prefixSuffixLCS(k: Int, j: Int): Int {
        if (k < 0 || k > m || j < 0 || j > n) return -1
        return semiLocalLCSMatrix[m - k][j] - k //?TPDP
    }

    override fun suffixPrefixLCS(l: Int, i: Int): Int {
        if (l < 0 || l > m || i < 0 || i > n) return -1
        return semiLocalLCSMatrix[i + m][m + n - l] - m + l

    }

    override fun substringStringLCS(k: Int, l: Int): Int {
        if (k < 0 || k > m || l < 0 || l > m) return -1
        return semiLocalLCSMatrix[m - k][m + n - l] - m - k + l
    }

    /**
     * Asks for lcs score between @aSymbolString and substring bSymbolString[i,j].
     * Note that WildCardSymbol matches any other symbol.
     *dummy implementation of lcs
     * @param i start index inclusive
     * @param j end index exclusive
     */
    private fun lcs(i: Int, j: Int): Int {
        val m = a.size + 1
        val bSub = bSymbolString.subList(i, j)
        val n = bSub.size + 1

        val lcsMatrix = Array(m) { _ -> IntArray(n) { _ -> 0 } }
        for (rowNum in 1 until m) {
            for (colNum in 1 until n) {
                if ((bSub[colNum - 1].type == SymbolType.WildCardSymbol) ||
                    (bSub[colNum - 1].type == SymbolType.AlphabetSymbol && aSymbolString[rowNum - 1].symbol == bSub[colNum - 1].symbol)
                )
                    lcsMatrix[rowNum][colNum] = lcsMatrix[rowNum - 1][colNum - 1] + 1
                else {
                    lcsMatrix[rowNum][colNum] = max(lcsMatrix[rowNum - 1][colNum], lcsMatrix[rowNum][colNum - 1])
                }
            }
        }
        return lcsMatrix[m - 1][n - 1]
    }

    /**
     * For testing purposes
     */
    internal fun print() {
        for (i in 0 until semiLocalLCSMatrix.size) {
            for (j in 0 until semiLocalLCSMatrix[0].size) {
                print("  ${semiLocalLCSMatrix[i][j]}  ")
            }
            println()
        }

    }


}

