import beyondsemilocality.*
import longestCommonSubsequence.ImplicitSemiLocalLCS
import longestCommonSubsequence.ReducingKernelEvaluation
import longestCommonSubsequence.Symbol
import utils.*
import kotlin.math.pow

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
//    val p = "AAAAAAAA".toMutableList()
//    val patten = Fragment(p,0,p.size)
//    val t1 = "AAAAAAAA!!AAAAAAAAA!!!!AAAAAAAA".toMutableList()
//    val fr1 = Fragment(t1,0,t1.size)
//    val t2 = "thAedarAAAwinAAA gAame".toMutableList()
//    val fr2 = Fragment(t2,0,t2.size)
//    val sol = findAllGroupsViaApproximateMatching(patten, mutableListOf(fr1,fr2,fr1),7.0,3)
//    sol.first.duplicates.forEach { println(it.text.subList(it.startInclusive,it.endExclusive)) }
//
//    println()
//    sol.second.forEach { println(it.text.subList(it.startInclusive,it.endExclusive)) }

}

