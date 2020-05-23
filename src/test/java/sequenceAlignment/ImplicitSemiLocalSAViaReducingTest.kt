package sequenceAlignment

import utils.PermutationMatrixTwoLists
import longestCommonSubsequence.ReducingKernelEvaluation
import utils.IScoringScheme
import utils.RegularScoringScheme
import utils.dummyPermutationMatrixTwoLists
import kotlin.random.Random


internal class ImplicitSemiLocalSAViaReducingTest() : SemiLocalSABaseTester(Random(17)) {


    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): Pair<ISemiLocalSA, IScoringScheme> {


        val denominator = random.nextInt(1, 17)
        val numerator = random.nextInt(0, denominator)

        return Pair(ImplicitSemiLocalSA(
            A,
            B,
//            FixedScoringScheme(Fraction(2,1), Fraction(-1,1),Fraction(-4,2)),
            RegularScoringScheme(numerator, denominator),
            ReducingKernelEvaluation ({ dummyPermutationMatrixTwoLists}, RegularScoringScheme(numerator, denominator))
        ), RegularScoringScheme(numerator, denominator))


    }
}