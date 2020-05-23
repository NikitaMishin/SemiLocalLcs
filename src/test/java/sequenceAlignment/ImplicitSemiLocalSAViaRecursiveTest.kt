package sequenceAlignment

import utils.PermutationMatrixTwoLists
import longestCommonSubsequence.RecursiveKernelEvaluation
import utils.IScoringScheme
import utils.RegularScoringScheme
import utils.dummyPermutationMatrixTwoLists
import kotlin.random.Random


internal class ImplicitSemiLocalSAViaRecursiveTest() : SemiLocalSABaseTester(Random(17)) {


    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): Pair<ISemiLocalSA, IScoringScheme> {


        val denominator = random.nextInt(1, 17)
        val numerator = random.nextInt(0, denominator)

        return Pair(ImplicitSemiLocalSA(
            A,
            B,
            RegularScoringScheme(numerator, denominator),
            RecursiveKernelEvaluation ({
                dummyPermutationMatrixTwoLists
            },RegularScoringScheme(numerator, denominator)))
        , RegularScoringScheme(numerator, denominator))


    }
}