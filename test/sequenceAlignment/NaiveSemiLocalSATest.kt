package sequenceAlignment

import utils.PermutationMatrixTwoLists
import longestCommonSubsequence.ReducingKernelEvaluation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.FixedScoringScheme
import utils.Fraction
import utils.IScoringScheme
import utils.RegularScoringScheme
import kotlin.random.Random


internal class NaiveSemiLocalSATest() : SemiLocalSABaseTester(Random(17)) {


    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): Pair<ISemiLocalSA, IScoringScheme> {

        val denominator = random.nextInt(1, 17)
        val numerator = random.nextInt(0, denominator)

        val scoringScheme = RegularScoringScheme(numerator, denominator)
        return Pair(
            NaiveSemiLocalSA(
                A,
                B,
//                FixedScoringScheme(Fraction(5, 1), Fraction(-1, 1), Fraction(3, 1))
            RegularScoringScheme(numerator, denominator)
            ),
            scoringScheme
        )

    }

    @Test
    fun check() {
        fun <E : Comparable<E>> checkStringSubstringProblem(A: List<E>, B: List<E>, sol1: ISemiLocalSA, sol2: ISemiLocalSA) {
            for (j in 0..B.size) {
                for (i in 0 until j) {
                    if (!compareDouble(
                            sol1.stringSubstringSA(i, j), sol2.stringSubstringSA(i, j)
                        )
                    ) {
                        Assertions.assertEquals(
                            sol1.stringSubstringSA(i, j), sol2.stringSubstringSA(i, j)
                        )
                    }

                }
            }
        }

        val a = "sdfjnvnduisndsv".toList()
        val b = "jkgfngksdfmvdsvsvvsd".toList()
//        val scheme = FixedScoringScheme(Fraction(0, 1), Fraction(-2, 1), Fraction(-1, 1))

        val scheme = RegularScoringScheme(18,19)
        checkStringSubstringProblem(
            a, b,
            NaiveSemiLocalSA(a, b, scheme),
            ImplicitSemiLocalSA(a, b, scheme,
                ReducingKernelEvaluation({
                    PermutationMatrixTwoLists(
                        listOf(),
                        0,
                        0
                    )
                })
            )
        )


    }
}

