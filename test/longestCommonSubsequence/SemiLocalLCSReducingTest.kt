package longestCommonSubsequence

import utils.PermutationMatrixTwoLists
import kotlin.random.Random


internal class SemiLocalLCSReducingTest : SemiLocalLCSBaseTester(random = Random(0)) {

    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>) =
        ImplicitSemiLocalLCS(
            A,
            B,
            ReducingKernelEvaluation {
                PermutationMatrixTwoLists(
                    mutableListOf(),
                    0,
                    0
                )
            })
}