package sequenceAlignment

import PermutationMatrixTwoLists
import RecursiveKernelEvaluation
import utils.RegularScoringScheme
import kotlin.random.Random


internal class ImplicitSemiLocalSAViaRecursiveTest() : SemiLocalSABaseTester(Random(17)) {


    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): ISemiLocalSA {


        val denominator = random.nextInt(1, 17)
        val numerator = random.nextInt(0, denominator)

        return ImplicitSemiLocalSA(
            A,
            B,
            RegularScoringScheme(numerator, denominator),
            RecursiveKernelEvaluation { PermutationMatrixTwoLists(listOf(), 0, 0) }
        )


    }
}