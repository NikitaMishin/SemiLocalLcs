package sequenceAlignment

import PermutationMatrixTwoLists
import ReducingKernelEvaluation
import utils.RegularScoringScheme
import kotlin.math.round
import kotlin.random.Random


fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

internal class NaiveSemiLocalSATest() : SemiLocalSABaseTester(Random(17)) {


    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): ISemiLocalSA {


        val denominator = random.nextInt(1, 17)
        val numerator = random.nextInt(0, denominator)

        return NaiveSemiLocalSA(
            A,
            B,
            RegularScoringScheme(numerator, denominator)
        )


    }
}

