package sequenceAlignment

import PermutationMatrixTwoLists
import ReducingKernelEvaluation
import utils.FixedScoringScheme
import utils.Fraction
import utils.RegularScoringScheme
import kotlin.random.Random


internal class ImplicitSemiLocalSAViaReducingTest() : SemiLocalSABaseTester(Random(17)) {


    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): ISemiLocalSA {


        val denominator = random.nextInt(1, 17)
        val numerator = random.nextInt(0, denominator)

        return ImplicitSemiLocalSA(
            A,
            B,
//            FixedScoringScheme(Fraction(2,1), Fraction(-1,1),Fraction(-3,2)),
            RegularScoringScheme(numerator, denominator),
            ReducingKernelEvaluation { PermutationMatrixTwoLists(listOf(), 0, 0) }
        )


    }
}