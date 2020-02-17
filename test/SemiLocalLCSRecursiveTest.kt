import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random


internal class SemiLocalLCSRecursiveTest : SemiLocalLCSBaseTester(random = Random(0)) {

    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>) =
        ImplicitSemiLocalLCS(A, B, RecursiveKernelEvaluation { PermutationMatrixTwoLists(mutableListOf(), 0, 0) })
}