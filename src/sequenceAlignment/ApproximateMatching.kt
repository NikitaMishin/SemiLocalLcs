package sequenceAlignment

import utils.FixedScoringScheme
import utils.IScoringScheme
import kotlin.math.max


interface CompleteAMatchProblem {
    fun getScoringScheme(): IScoringScheme
    fun solve(): Array<Double>
}

class SellersCompleteAMatchProblem<T : Comparable<T>>(val pattern: List<T>, val text: List<T>, private var scoringScheme: IScoringScheme) : CompleteAMatchProblem {
    override fun getScoringScheme(): IScoringScheme = scoringScheme

    override fun solve(): Array<Double> {
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

        for (i in 0..pattern.size) {
            for (j in 0..text.size) {
                print("${scoreMatrix[i][j]}  ")
            }
            println()
        }

        return scoreMatrix[pattern.size].clone()
    }
}

//class ApproixmateMathcingViaSemiLocal<T:Comparable<T>>(val pattern: List<T>, val text: List<T>, private var scoringScheme: IScoringScheme,
//                                                       ): CompleteAMatchProblem{
//
//    override fun getScoringScheme() = scoringScheme
//
//    override fun solve(): Array<Double> {
//
//    }
//
//}