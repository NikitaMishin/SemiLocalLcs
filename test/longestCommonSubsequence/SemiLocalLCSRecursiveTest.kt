package longestCommonSubsequence

import utils.PermutationMatrixTwoLists

import kotlin.random.Random


internal class SemiLocalLCSRecursiveTest : SemiLocalLCSBaseTester(random = Random(0)) {

    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>) =
        ImplicitSemiLocalLCS(
            A,
            B,
            RecursiveKernelEvaluation {
                PermutationMatrixTwoLists(
                    mutableListOf(),
                    0,
                    0
                )
            })
}