import beyondsemilocality.ExplicitFragmentSubstringProvider
import beyondsemilocality.ImplicitFragmentSubstringProvider
import beyondsemilocality.WindowSubstringSA
import beyondsemilocality.WindowSubstringSANaiveImplicit
import longestCommonSubsequence.*
import sequenceAlignment.ExplicitSemiLocalSA
import sequenceAlignment.ImplicitSemiLocalSA
import utils.*
import kotlin.math.max
import kotlin.random.Random
import kotlin.system.measureTimeMillis


/**
 * Runner for speed test of algorithm implementations
 * Run options:
 *  mu v m n option alphabetSize taskId
 *  mu/v: 0<=mismatch score<1
 *  n,m - sequences lengths
 *  taskId --- which problem should be run
 *  Example:
 *  /usr/lib/jvm/jdk-12.0.2/bin/java -jar  0 1 10000 10000 200 30 2
 * TaskIds:
 * 0 -  simple sa
 * 1 -  implicit semi-local lcs reducing kernel
 * 2 -  implicit semi-local lcs steadyAnt kernel
 * 3 -  explicit semi-local lcs/sa
 * 4 -  semiLocalSA implicit reducing
 * 5 -  semiLocalSA implicit steadyAnt
 * 6 -  windowSubstring LCS/SA implicit
 * 7 -  windowSubstring LCS/SA explicit
 * 8 -  windowSubstring naive Implicit
 * 9 - light prefix alignment
 */
object SpeedRunner {


    fun <T : Comparable<T>> prefixAlignment(a: List<T>, b: List<T>, scoringScheme: IScoringScheme): Double {
        val scoreMatrix = Array(a.size + 1) { Array(b.size + 1) { 0.0 } }
        val match = scoringScheme.getMatchScore().toDouble()
        val mismatch = scoringScheme.getMismatchScore().toDouble()
        val gap = scoringScheme.getGapScore().toDouble()

        for (i in 1 until scoreMatrix.size) {
            for (j in 1 until scoreMatrix[0].size) {
                scoreMatrix[i][j] = java.lang.Double.max(
                    scoreMatrix[i - 1][j - 1] + (if (a[i - 1] == b[j - 1]) match
                    else mismatch),
                    java.lang.Double.max(
                        scoreMatrix[i - 1][j] + gap,
                        scoreMatrix[i][j - 1] + gap
                    )
                )
            }
        }

        return scoreMatrix[a.size][b.size]
    }

    fun <T : Comparable<T>> lightPrefixAlignment(a: List<T>, b: List<T>, scoringScheme: IScoringScheme): Double {
        val row = DoubleArray(b.size + 1) { 0.0 }

        val match = scoringScheme.getMatchScore().toDouble()
        val mismatch = scoringScheme.getMismatchScore().toDouble()
        val gap = scoringScheme.getGapScore().toDouble()
        var left = 0.0
        var newLeft = 0.0
        for (i in 1 until a.size + 1) {
            left = max(if (a[i - 1] == b[0]) match else mismatch, max(gap + row[0], row[1] + gap))

            for (j in 2 until b.size) {
                newLeft = max(left + gap, max(row[j - 1] + if (a[i - 1] == b[j - 1]) match else mismatch, row[j] + gap))
                row[j - 1] = left
                left = newLeft
            }
            //j==b.size
            row[b.size] = max(
                row[b.size - 1] + if (a[i - 1] == b[b.size - 1]) match else mismatch,
                max(left + gap, row[b.size] + gap)
            )
            row[b.size - 1] = left
        }
        return row.last()
    }


    val random = Random(42)

    /**
     * mu v runs n m alphabetSize taskName
     * Format scoring scheme 3 numbers
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val mu = args[0].toInt()
        val v = args[1].toInt()
        val m = args[2].toInt()
        val n = args[3].toInt()
        val option = args[4].toInt()
        val alphabetSize = args[5].toInt() + 1
        val taskId = args[6].toInt()

        val scoringScheme = if (mu == 0) LCSScoringScheme() else RegularScoringScheme(mu, v)
        val sequenceA =
            (0 until m).toList().map { kotlin.math.abs(random.nextInt()) % alphabetSize }.toMutableList()
        val sequenceB =
            (0 until n).toList().map { kotlin.math.abs(random.nextInt()) % alphabetSize }.toMutableList()
        val mills = measureTimeMillis {
            when (taskId) {
//                1second 450
//                13seconds
//                350
                //67x
//                25seconds
                /**
                 * 0 -  simple sa
                 * 1 -  implicit semi-local lcs reducing kernel
                 * 2 -  implicit semi-local lcs steadyAnt kernel
                 * 3 -  explicit semi-local lcs/sa
                 * 4 -  semiLocalSA implicit reducing
                 * 5 -  semiLocalSA implicit steadyAnt
                 * 6 -  windowSubstring LCS/SA implicit
                 * 7 -  windowSubstring LCS/SA explicit
                 * 8 -  windowSubstring naive Implicit
                 * 9 - light prefix alignment
                 */
                0 -> {
                    prefixAlignment(sequenceA, sequenceB, scoringScheme)
                }
                1 -> {
                    ImplicitSemiLocalLCS(
                        sequenceA,
                        sequenceB,
                        ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists })
                    )
                }
                2 -> {
                    ImplicitSemiLocalLCS(
                        sequenceA,
                        sequenceB,
                        RecursiveKernelEvaluation({ dummyPermutationMatrixTwoLists })
                    )
                }
                3 -> {
                    ExplicitSemiLocalSA(sequenceA, sequenceB, scoringScheme, ExplicitKernelEvaluation(scoringScheme))
                }
                4 -> {
                    ImplicitSemiLocalSA(
                        sequenceA,
                        sequenceB,
                        scoringScheme,
                        ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists }, scoringScheme)
                    )
                }
                5 -> {
                    ImplicitSemiLocalSA(
                        sequenceA,
                        sequenceB,
                        scoringScheme,
                        RecursiveKernelEvaluationVSubs({ dummyPermutationMatrixTwoLists }, scoringScheme)
                    )
                }
                6 -> {
                    WindowSubstringSA(ImplicitFragmentSubstringProvider(sequenceA, sequenceB, scoringScheme))
                        .solve(sequenceA, sequenceB, option, scoringScheme)
                }
                7 -> {
                    WindowSubstringSA(ExplicitFragmentSubstringProvider(sequenceA, sequenceB, scoringScheme))
                        .solve(sequenceA, sequenceB, option, scoringScheme)
                }
                8 -> {
                    WindowSubstringSANaiveImplicit<Int>(
                        RecursiveKernelEvaluationVSubs(
                            { dummyPermutationMatrixTwoLists },
                            scoringScheme
                        )
                    )
                        .solve(sequenceA, sequenceB, option, scoringScheme)
                }
                9 -> {
                    lightPrefixAlignment(sequenceA, sequenceB, scoringScheme)

                }
                else -> throw NotImplementedError("Wrong ID")
            }
        }

        println(mills)
    }
}