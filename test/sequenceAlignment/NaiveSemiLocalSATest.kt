package sequenceAlignment

import kotlin.random.Random

internal class NaiveSemiLocalSATest():SemiLocalSABaseTester(Random(17)) {
    val rightBound = 100000.0
    val leftBound = -10.0
    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): ISemiLocalSA {

        val match = random.nextDouble(0.0,rightBound)
        val mismatch = random.nextDouble(leftBound,match)
        val gap = random.nextDouble(leftBound,match/2)

        return  NaiveSemiLocalSA(A,B, ScoringScheme(2.0,-1.0,-1.5))
    }
}