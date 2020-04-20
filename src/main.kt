import beyondsemilocality.*
import longestCommonSubsequence.*
import sequenceAlignment.ExplicitSemiLocalSA
import utils.*

//import sequenceAlignment.SellersCompleteAMatchProblem

//import sequenceAlignment.ScoringScheme


////Ukkonen, E. (1985). Finding approximate patterns in strings. Journal of Algorithms, 6(1), 132–137. doi:10.1016/0196-6774(85)90023-9 
//// a column ,b row
//
//
//
///**
// *
// * also check Ukkonen, E. (1985). Finding approximate patterns in strings where builds automaton
// * O(n * m) DP APPROACH
// */
//fun <Elem> dpApproximateSubseqMatching(fragment: List<Elem>, pattern: List<Elem>): Array<IntArray> {
//    // build edit dsit n * m
//    val n = fragment.count() + 1
//    val m = pattern.count() + 1
//    val editDstMatrix = Array(n) { i -> IntArray(m) { j -> if (i == 0) j else 0 } }
//    for (rowNum in 1 until n) {
//        for (colNum in 1 until m) {
//            if (fragment[rowNum - 1] == pattern[colNum - 1]) editDstMatrix[rowNum][colNum] =
//                editDstMatrix[rowNum - 1][colNum - 1]
//            else editDstMatrix[rowNum][colNum] =
//                minOf(
//                    editDstMatrix[rowNum - 1][colNum] + 1,
//                    editDstMatrix[rowNum][colNum - 1] + 1,
//                    editDstMatrix[rowNum - 1][colNum - 1] + 1
//                )
//        }
//    }
//    return editDstMatrix
//}
//
//
//fun <Elem> LSHApproximateSubseqMatching(fragment: List<Elem>, pattern: List<Elem>) {
//    TODO()
//}
//
//fun <Elem> printLcsMatrix(arr: Array<IntArray>, rowSeq: List<Elem>, colSeq: List<Elem>) {
//    print("    ")
//    for (elem in colSeq) {
//        print("$elem ")
//    }
//    println()
//
//    var pos = -1
//
//    // query last col to find approximate pattern matching
//    for (row in arr) {
//        if (pos == -1) print("  ") else print("${rowSeq[pos]} ")
//        for (elem in row) {
//            print("$elem ")
//        }
//        println()
//        pos++
//    }
//}
//
//fun isEqual(arr1: Array<IntArray>, arr2: Array<IntArray>): Boolean {
//    if (arr1.size != arr2.size || arr1[0].size != arr2[0].size) return false
//    val n = arr1.size
//    val m = arr1[0].size
//
//    for (rowNum in 0 until n) {
//        for (colNum in 0 until m) {
//            if (arr1[rowNum][colNum] != arr2[rowNum][colNum]) return false
//        }
//    }
//
//    return true
//}
//
//

fun main() {


//    val a = "hat".toList()
//     val a = mutableListOf("Lorem", "Ipsum").toList()
////    val b = "baa baaa a a aaa".toList()
//    val b:List<String> =
//        ("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's st" +
//                "andard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specim" +
//                "en book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially un" +
//                "changed. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recentl" +
//                "y with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
//                ).split(",", " ",".")
////    val scoringScheme = RegularScoringScheme(3,1252)
//
//    println(b.size)
//    println(scoringScheme.getNormalizedMismatchScore().numerator)
//    println(scoringScheme.getNormalizedMismatchScore().denominator)
//    val res = ExplicitKernelEvaluation(scoringScheme).evaluate(a, b)
//
//
//
//    for (i in 0 until res.height()) {
//        for (j in 0 until res.width()) {
//            print("  ${res[i, j].round(1)}  ")
//        }
//        println()
//    }
//
//
//
//    println("Should")
//    val resImplicit =
//        ImplicitSemiLocalSA(a, b, scoringScheme, ReducingKernelEvaluation { dummyPermutationMatrixTwoLists })
////    resImplicit.print()
//
//    for (i in 0 until res.height()) {
//        for (j in 0 until res.width()) {
//            if (!resImplicit.getMatrix()[i, j].isEquals(res[i, j])) {
//                println("FUUUX: ${resImplicit.getMatrix()[i, j]}!${res[i, j]}")
//            }
//        }
////        println()
//    }
//
//    println()
//    val explitic = ExplicitSemiLocalSA(a, b, scoringScheme, ExplicitKernelEvaluation(scoringScheme))
////    println(explitic is ISemiLocalCombined<*>)
////    CompleteAMatchViaSemiLocalTotallyMonotone(explitic).solve().forEach { print(it) }
////    println()
//
//    val monge = resImplicit.getMatrix()
//
//    println("monge")
//    for (i in 0 until monge.height()) {
//        for (j in 0 until b.size + 1) {
//            scoringScheme.getOriginalScoreFunc(monge[i, j].round(2), a.size, i - a.size, j)
//            print("  ${scoringScheme.getOriginalScoreFunc(monge[i, j].round(2), a.size, i - a.size, j).round(2)}  ")
////            print("${monge[i,j]} ")
//        }
//        println()
//    }
//
//    ThresholdAMathViaSemiLocal(CompleteAMatchViaSemiLocalTotallyMonotone(explitic)).solve(1.0)
//        .forEach { println(b.subList(it.startInclusive, it.endExclusive) + it.score) }
//    ThresholdAMathViaSemiLocal(CompleteAMatchViaSemiLocalTotallyMonotone(explitic)).solve(1.0).forEach { println(it) }
//
//    ThresholdWindowSemiLocal(
//        explitic,
//        SellersCompleteAMatch(a.toList(), b.toList(), scoringScheme)
//    ).solve(1.0,4).forEach { println(b.subList(it.startInclusive, it.endExclusive) + it.score) }
//    ThresholdAMathViaSemiLocal(
//        SellersCompleteAMatch(a.toList(), b.toList(), scoringScheme)
//    ).solve(1.0).forEach { println(it) }


       val scoringScheme = FixedScoringScheme(Fraction(3, 1), Fraction(-5, 1), Fraction(-6, 1))
//val scoringScheme =  LCSScoringScheme()
    val a = "aaa a a aaab aab".toList()
//     val a = mutableListOf("Lorem", "Ipsum").toList()
    val b = "baa baaa a a aaa".toList()
    val explicit = ExplicitSemiLocalSA(a,b,scoringScheme,ExplicitKernelEvaluation(scoringScheme))

//    ImplicitFragmentSubstringProviderN(a,b,scoringScheme).getSolutionFor(0,5).print()

    val rs=   WindowSubstringProvider(ExplicitFragmentSubstringProvider(a,b,scoringScheme)).solve(a,b,3,scoringScheme).constructAlignmentPlot()
    for (i in 0 until  rs.size){
        for(j in 0 until rs[0].size){
            print("  ${rs[i][j].round(2)}  ")
        }
        println()

    }
    println()
println(a)
//    println(b)
    val impl=   WindowSubstringProvider(ImplicitFragmentSubstringProviderN(a,b,scoringScheme)).solve(a,b,3,scoringScheme).constructAlignmentPlot()
    for (i in 0 until  impl.size) {
        for (j in 0 until impl[0].size) {
            print("  ${impl[i][j].round(2)}  ")
        }
        println()
///

    }
}
//
//
//    println()
//    WindowSubstringProvider(ImplicitFragmentSubstringProviderN(a,b,scoringScheme)).solve(a,b,2,scoringScheme).getSolution(0).print()
//    println()
//
//
//
//
//
//    WindowSubstringProvider(ExplicitFragmentSubstringProvider(a,b,scoringScheme)).solve(a,b,2,scoringScheme).getSolution(0).print()
//    println()


//    ExplicitSemiLocalSA("aa".toList(),b,scoringScheme,arrMonge[1][0]).print()
//    println()
//    ImplicitSemiLocalLCS("aa".toList(),b,arrM[1][0]).print()


//    println()
//
//  val c =   ExplicitSemiLocalSA("a".toList(),b,scoringScheme,ExplicitKernelEvaluation(scoringScheme)).getMatrix()
//    for (i in 0 until c.height()){
//        for (j in 0 until c.width()){
//            c[i,j] = -c[i,j] +  (j - (i - 1))
//            //            print("  ${c[i,j]}  ")
//
//        }
//
//        println()
//    }
//
//
//
//    var pp=   staggeredExplicitMultiplication(c,c,16)
//
////    if (pp[pp.height() - 1, 0] != 0.0) pp = getMongeMatrixBA(pp, 2, b.size)
//    for (i in 0 until pp.height()){
//        for (j in 0 until pp.width()){
//            pp[i,j] = j - (i - 2) - pp[i, j]
//            print("  ${pp[i,j]}  ")
//        }
//
//        println()
//    }




