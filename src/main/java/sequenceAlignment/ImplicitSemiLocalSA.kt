package sequenceAlignment

import longestCommonSubsequence.*
import utils.CountingQuerySA
import utils.IStochasticMatrix
import utils.VSubBistochasticMatrix

import utils.*


/**
 *TODO make a note about round,wokrs only with ratiobal scheme (ask tiskin)
 */
class ImplicitSemiLocalSA<T> : ISemiLocalCombined<T> {

    override val pattern: List<T>
    override val text: List<T>

    private var v = 0
    private var mu = 0
    private var m = 0
    private var n = 0
    private val scoringScheme: IScoringScheme

    private val rangeTree2D: RangeTree2D<Int>
    private val countingQuerySA = CountingQuerySA()
    private val stochasticMatrix: IStochasticMatrix


    constructor(a: List<T>, b: List<T>, scoringScheme: IScoringScheme, kernelEvaluator: IStrategyKernelEvaluation) {

        v = scoringScheme.getNormalizedMismatchScore().denominator
        mu = scoringScheme.getNormalizedMismatchScore().numerator
        m = a.size
        n = b.size
        pattern = a
        text = b
        this.scoringScheme = scoringScheme

        val permMatrixExtended = kernelEvaluator.evaluate(pattern,text)


        val size = (pattern.size + text.size)

        val points = Array(size){ hashMapOf<Int,Position2D<Int>>()}
        for(p in permMatrixExtended){
            val i = (p.i / v)
            val j = (p.j / v)
            val row = points[i]
            if (row.containsKey(j)) row[j]!!.value++
            else row[j] = Position2D(i,j,1)
        }
        val p = points.flatMap { it.values }
        stochasticMatrix = VSubBistochasticMatrix(points.flatMap { it.values },size,size,v)
        rangeTree2D = RangeTree2D(p)
    }


    constructor(a: List<T>, b: List<T>, scoringScheme: IScoringScheme, lcsKernel: Matrix) {
        mu = scoringScheme.getNormalizedMismatchScore().numerator
        v = scoringScheme.getNormalizedMismatchScore().denominator
        m = a.size
        n = b.size
        pattern = a
        text = b
        this.scoringScheme = scoringScheme


        val size = (pattern.size + text.size)
        //Note ν-(sub)bistochastic matrix, there can be at most ν nonzeros in every row and every column
        val points = IntArray(size * size) { 0 } // i*n + j

        for (p in lcsKernel) {
            val posInInitialI = p.i / v;
            val posInInitialJ = p.j / v;
            points[posInInitialI * size + posInInitialJ]++
        }

        //first create actual points, then filter all zero points, and pass them to range tree
        val p =
            points.mapIndexed { index, value -> Position2D(index / size, index % size, value) }.filter { it.value != 0 }

        stochasticMatrix = VSubBistochasticMatrix(p, size, size, v)
        rangeTree2D = RangeTree2D(p)

    }


    private fun reverseRegularizationScore(value: Double, i: Int, j: Int): Double =
        scoringScheme.getOriginalScoreFunc(value, m, i, j)

    /**
     * i from 0 to m + n
     */
    private fun canonicalDecomposition(i: Int, j: Int): Double {
        return j - (i - m) - rangeTree2D.ortoghonalQuery(
            IntervalQuery(i, m + n + 1),
            IntervalQuery(0, j - 1)
        ).toDouble() / v
    }

    //Implementation of ISemiLocalSA interface

    override fun prefixSuffix(k: Int, j: Int): Double {
        if (k < 0 || k > m || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, j), k, j) - k
    }

    override fun stringSubstring(i: Int, j: Int): Double {
        if (i < 0 || i > n || j < 0 || j > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(i + m, j), i, j)
    }

    override fun substringString(k: Int, l: Int): Double {
        if (k < 0 || k > m || l < 0 || l > m) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(m - k, m + n - l), k, l) - m - k + l
    }

    override fun suffixPrefix(l: Int, i: Int): Double {
        if (l < 0 || l > m || i < 0 || i > n) return Double.NaN
        return reverseRegularizationScore(canonicalDecomposition(i + m, m + n - l), i, l) - m + l
    }

    override fun print() {
        for (i in 0 until m + n + 1) {
            for (j in 0 until m + n + 1) {
                print("  ${getAtPosition(i, j).round(1)}  ")
            }
            println()
        }
    }

    //Implementation of ISemiLocalSolution interface


    override fun getScoringScheme() = scoringScheme

    override fun getAtPosition(i: Int, j: Int): Double = canonicalDecomposition(i, j)

    override fun nextInCol(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> j - (i + 1 - m) - countingQuerySA.dominanceSumTopRightDownMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
            ISemiLocalFastAccess.Direction.BackWard -> j - (i - 1 - m) - countingQuerySA.dominanceSumTopRightUpMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
        }


    override fun nextInRow(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> j + 1 - (i - m) - countingQuerySA.dominanceSumTopRightRightMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
            ISemiLocalFastAccess.Direction.BackWard -> j - 1 - (i - m) - countingQuerySA.dominanceSumTopRightLeftMove(
                i,
                j,
                j - i + m - rawValue,
                stochasticMatrix
            )
        }


    override fun getMatrix(): AbstractMongeMatrix {

        val mongeMatrix = MongeMatrix(m + n + 1, m + n + 1)
//        TODO make fast through incremental queries


        for (i in 0 until m + n + 1) {
            for (j in 1 until m + n + 1) {
                mongeMatrix[i, j] = getAtPosition(i, j)
            }
        }
        return mongeMatrix

    }

}

class ExplicitSemiLocalSA<T> : ISemiLocalCombined<T> {

    override val pattern: List<T>
    override val text: List<T>

    private val scoringScheme: IScoringScheme

    //storing matrix H
    private val matrix: AbstractMongeMatrix

    private val m: Int
    private var n: Int

    constructor(
        a: List<T>,
        b: List<T>,
        scoringScheme: IScoringScheme,
        matrixBuilder: IStrategyExplicitMatrixEvaluation
    ) {
        pattern = a
        text = b
        this.scoringScheme = scoringScheme
        matrix = matrixBuilder.evaluate(a, b)

        for (i in 0 until matrix.height()) {
            for (j in 0 until matrix.width()) {
                matrix[i, j] = j - (i - a.size) - matrix[i, j]
            }
        }
        m = a.size
        n = b.size
    }

    constructor(a: List<T>, b: List<T>, scoringScheme: IScoringScheme, matrix: AbstractMongeMatrix) {
        pattern = a
        text = b
        this.scoringScheme = scoringScheme
        this.matrix = matrix
        m = a.size
        n = b.size
    }


    private fun reverseScore(value: Double, i: Int, j: Int): Double =
        scoringScheme.getOriginalScoreFunc(value, m, i, j)


    override fun stringSubstring(i: Int, j: Int): Double {
        if (i < 0 || i > n || j < 0 || j > n) return Double.NaN
        return reverseScore(matrix[i + m, j], i + m, j)
    }

    override fun prefixSuffix(k: Int, j: Int): Double {
        if (k < 0 || k > m || j < 0 || j > n) return Double.NaN
        return reverseScore(matrix[m - k, j], m - k, j) - k
    }


    override fun suffixPrefix(l: Int, i: Int): Double {
        if (l < 0 || l > m || i < 0 || i > n) return Double.NaN
        return reverseScore(matrix[i + m, m + n - l], i + m, m + n - l) - m + l
    }


    //    TODO
    override fun substringString(k: Int, l: Int): Double {
        if (k < 0 || k > m || l < 0 || l > m) return Double.NaN
        return reverseScore(matrix[m - k, m + n - l], m - k, m + n - l) - m - k + l
    }


    override fun nextInRow(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> matrix[i, j + 1]
            ISemiLocalFastAccess.Direction.BackWard -> matrix[i, j - 1]
        }


    override fun nextInCol(i: Int, j: Int, rawValue: Double, direction: ISemiLocalFastAccess.Direction): Double =
        when (direction) {
            ISemiLocalFastAccess.Direction.Forward -> matrix[i + 1, j]
            ISemiLocalFastAccess.Direction.BackWard -> matrix[i - 1, j]
        }

    override fun getAtPosition(i: Int, j: Int): Double = matrix[i, j]

    override fun getScoringScheme(): IScoringScheme = scoringScheme

    override fun print() {
        for (i in 0 until matrix.height()) {
            for (j in 0 until matrix.width()) {
                print("  ${matrix[i, j]}  ")
            }
            println()
        }
    }
    override fun getMatrix() = matrix
}


//TODO переделать


interface ISemiLocalProvider{
    fun <T>buildSolution(a:List<T>,b:List<T>,scheme: IScoringScheme):ISemiLocalCombined<T>
}

class  ExplicitSemiLocalProvider(var explicitKernelEvaluation: IStrategyExplicitMatrixEvaluation): ISemiLocalProvider {
    override fun <T> buildSolution(a: List<T>, b: List<T>,scheme: IScoringScheme): ISemiLocalCombined<T> =
        ExplicitSemiLocalSA(a,b,scheme,explicitKernelEvaluation)
}

class  ImplicitSemiLocalProvider(var implicitKernel: IStrategyKernelEvaluation): ISemiLocalProvider {
    override fun <T> buildSolution(a: List<T>, b: List<T>,scheme: IScoringScheme): ISemiLocalCombined<T> =
        ImplicitSemiLocalSA(a,b,scheme,implicitKernel)
}



