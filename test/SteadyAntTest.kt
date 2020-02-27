import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utils.Position2D
import kotlin.math.min

internal class SteadyAntTest {
    private val naiveBraidMultiplication = NaiveBraidMultiplication()
    private val steadyAntMultiplication = SteadyAntMultiplication()

    val bookP = listOf(
        Position2D(0, 4),
        Position2D(1, 3),
        Position2D(2, 10),
        Position2D(3, 2),
        Position2D(4, 11),
        Position2D(5, 15),
        Position2D(6, 6),
        Position2D(7, 9),
        Position2D(8, 19),
        Position2D(9, 13),
        Position2D(10, 12),
        Position2D(11, 1),
        Position2D(12, 0),
        Position2D(13, 17),
        Position2D(14, 18),
        Position2D(15, 7),
        Position2D(16, 16),
        Position2D(17, 5),
        Position2D(18, 14),
        Position2D(19, 8)
    )
    val bookQ = listOf(
        Position2D(0, 4),
        Position2D(1, 6),
        Position2D(2, 10),
        Position2D(3, 9),
        Position2D(4, 0),
        Position2D(5, 12),
        Position2D(6, 2),
        Position2D(7, 3),
        Position2D(8, 14),
        Position2D(9, 18),
        Position2D(10, 5),
        Position2D(11, 11),
        Position2D(12, 1),
        Position2D(13, 15),
        Position2D(14, 17),
        Position2D(15, 13),
        Position2D(16, 7),
        Position2D(17, 8),
        Position2D(18, 16),
        Position2D(19, 19)
    )
    val bookR = listOf(
        Position2D(0, 10),
        Position2D(1, 9),
        Position2D(2, 18),
        Position2D(3, 6),
        Position2D(4, 14),
        Position2D(5, 17),
        Position2D(6, 12),
        Position2D(7, 11),
        Position2D(8, 19),
        Position2D(9, 15),
        Position2D(10, 5),
        Position2D(11, 4),
        Position2D(12, 0),
        Position2D(13, 13),
        Position2D(14, 16),
        Position2D(15, 3),
        Position2D(16, 8),
        Position2D(17, 2),
        Position2D(18, 7),
        Position2D(19, 1)
    )


    @Test
    fun steadyAntRandomTest() {

        val widthsQ = 18
        val widths = 19
        val heightsP = 15

        for (heightP in 1 until heightsP) {
            for (widtdP in 1 until widths) {
                for (widthQ in 1 until widthsQ)
                    for (nonZeroesP in 1..min(heightP, widtdP)) {
                        for (nonzeroesQ in 0..min(widtdP, widthQ)) {
                            val P = AbstractPermutationMatrix.generatePermutationMatrix(heightP, widtdP, nonZeroesP, 99)
                            val Q = AbstractPermutationMatrix.generatePermutationMatrix(widtdP, widthQ, nonzeroesQ, 101)
                            val naiveRes = naiveBraidMultiplication.multiply(P, Q)
                            val res = steadyAntMultiplication.multiply(P, Q)
                            if (!res.isEquals(naiveRes)) {
                                println("debug ")
                                println("Expected")
                                naiveRes.print()
                                println("Actual")
                                res.print()
                                assertTrue(false)
                            }

                        }
                    }
            }
        }
    }


}