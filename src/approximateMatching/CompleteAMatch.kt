package approximateMatching

import sequenceAlignment.ISemiLocalSolution
import utils.IScoringScheme
import utils.round
import utils.rowMinima
import utils.times
import kotlin.math.max

/**
 *
 */
interface ICompleteAMatchProblem<T> {
    /**
     * For every prefix of text t<0 : j>, the problem asks for the maximum
     * alignment score of pattern p against all possible choices of a suffix t<i : j> from this prefix
     * @return IntArray of positions
     */
    fun solve(): Array<Pair<Int, Double>>
}

class SellersCompleteAMatch<T>(
    var pattern: List<T>,
    var text: List<T>,
    var scoringScheme: IScoringScheme
) : ICompleteAMatchProblem<T> {

    /**
     * Note for indexes: in result reffer to substring  text[i,j-1]
     */
    override fun solve(): Array<Pair<Int, Double>> {
        val match = scoringScheme.getMatchScore().toDouble()
        val mismatch = scoringScheme.getMismatchScore().toDouble()
        val gap = scoringScheme.getGapScore().toDouble()


        val scoreMatrix = Array(pattern.size + 1) { i -> Array(text.size + 1) { j -> if (j == 0) i * gap else 0.0 } }

        for (i in 1..pattern.size) {
            for (j in 1..text.size) {
                scoreMatrix[i][j] = max(
                    scoreMatrix[i - 1][j - 1] + (if (pattern[i - 1] == text[j - 1]) match else mismatch),
                    max(scoreMatrix[i - 1][j] + gap, scoreMatrix[i][j - 1] + gap)
                )
            }
        }

        //backtrace to restore start position
        return (0..text.size).toList().map { j ->
            var jCap = j
            var i = pattern.size
            while (jCap != 0 && i != 0) {
                val left = scoreMatrix[i][jCap - 1] + gap
                val top = scoreMatrix[i - 1][jCap] + gap
                val diagonal = scoreMatrix[i - 1][jCap - 1] + if (pattern[i - 1] == text[jCap - 1]) match else mismatch
                when {
                    top >= left && top >= diagonal -> {
                        i--
                    }
                    diagonal >= left && diagonal >= top -> {
                        jCap--
                        i--
                    }
                    else -> jCap--
                }
            }
            Pair(jCap, scoreMatrix[pattern.size][j])
        }.toTypedArray()
    }
}

/**
 *
 */
class CompleteAMatchViaSemiLocalTotallyMonotone<T>(var solution: ISemiLocalSolution<T>) :
    ICompleteAMatchProblem<T> {
    private var pattern = solution.pattern
    private var text = solution.text

    //TODO ask tiskin wrong formula? what about totally monotonne?
    private fun scoreTransformer(value: Double, i: Int, j: Int) =
        solution.getScoringScheme().getOriginalScoreFunc(value, solution.pattern.size, i, j)


    override fun solve(): Array<Pair<Int, Double>> {

        val n = text.size + 1
        val m = pattern.size
//TODO
        val smawk = rowMinima({ i, j ->
            -scoreTransformer(
                solution.getAtPosition(m + n - 1 - j, n - 1 - i).round(1),
                m + n - 1 - j,
                n - 1 - i
            )
        }, n, n)


        smawk.reverse()

        return smawk.mapIndexed { index, i ->
            Pair(
                n - 1 - i,
                scoreTransformer(solution.getAtPosition(m + n - 1 - i , index),  n - 1 - i, index).round(1)
            )
        }.toTypedArray()
    }
}