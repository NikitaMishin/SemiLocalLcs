package sequenceAlignment

import longestCommonSubsequence.ReducingKernelEvaluation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.*
import kotlin.random.Random


//internal class NaiveSemiLocalSATest() : SemiLocalSABaseTester(Random(17)) {
//
//
//    override fun <E : Comparable<E>> getSemiLocalSolution(A: List<E>, B: List<E>): Pair<ISemiLocalSA, IScoringScheme> {
//
//        val denominator = random.nextInt(1, 17)
//        val numerator = random.nextInt(0, denominator)
//
//        val scoringScheme = RegularScoringScheme(numerator, denominator)
//        return Pair(
//            NaiveSemiLocalSA(
//                A,
//                B,
//                FixedScoringScheme(Fraction(20, 1), Fraction(-10, 1), Fraction(-300, 15))
////            RegularScoringScheme(numerator, denominator)
//            ),
//            FixedScoringScheme(Fraction(20, 1), Fraction(-10, 1), Fraction(-300, 15))
//        )
//
//    }
//
//    @Test
//    fun tmp(){
//
//        val sc = FixedScoringScheme(Fraction(1,1),
//            Fraction(-1,1),Fraction(-3,2)
//        )
//        println(prefixAlignment("AAAA".toList(),"A".toList(),2 ,sc))
//        println(
//        NaiveSemiLocalSA("AAAA".toList(),"AA".toList(),sc).substringString(0,1)
//        )
////        println(
////            prefixAlignment(
////                "CABBA".toList(),"СBBA".toList(),FixedScoringScheme(Fraction(2,1), Fraction(-1,1), Fraction(-1,1))
////            )
////        )
//    }
//
//    @Test
//    fun check() {
//        fun <E : Comparable<E>> checkStringSubstringProblem(A: List<E>, B: List<E>, sol1: ISemiLocalSA, sol2: ISemiLocalSA) {
//            for (j in 0..B.size) {
//                for (i in 0 until j) {
//                    if (!compareDouble(
//                            sol1.stringSubstring(i, j), sol2.stringSubstring(i, j)
//                        )
//                    ) {
//                        Assertions.assertEquals(
//                            sol1.stringSubstring(i, j), sol2.stringSubstring(i, j)
//                        )
//                    }
//
//                }
//            }
//        }
//
//        val a = "sdfjnvnduisndsv".toList()
//        val b = "jkgfngksdfmvdsvsvvsd".toList()
////        val scheme = FixedScoringScheme(Fraction(0, 1), Fraction(-2, 1), Fraction(-1, 1))
//
//        val scheme = RegularScoringScheme(18,19)
//        checkStringSubstringProblem(
//            a, b,
//            NaiveSemiLocalSA(a, b, scheme),
//            ImplicitSemiLocalSA(a, b, scheme,
//                ReducingKernelEvaluation({
//                    PermutationMatrixTwoLists(
//                        listOf(),
//                        0,
//                        0
//                    )
//                })
//            )
//        )
//
//
//    }
//}
//
