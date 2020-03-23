import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.SellersCompleteAMatch
import approximateMatching.ThresholdAMathViaSemiLocal
import approximateMatching.ThresholdWindowSemiLocal
import beyondsemilocality.WindowSubstringLCSImplicit
import beyondsemilocality.WindowSubstringSANaiveImplicit
import beyondsemilocality.canonicalSWindows
import longestCommonSubsequence.ImplicitSemiLocalLCS
import longestCommonSubsequence.NaiveSemiLocalLCS
import longestCommonSubsequence.ReducingKernelEvaluation
import sequenceAlignment.*
import utils.*
import java.lang.Math.pow
import kotlin.math.log2
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
//
////    println(Fraction(4,5)*
////    Fraction(0,6))
////TODO reverse tjat u do in naive
//    val scoringSсheme =
//        RegularScoringScheme(0,10)
////        FixedScoringScheme(Fraction(2,1),Fraction(-1,1), Fraction(-2,1))
//  val a = "ab".toList()
//    val b = "aba".toList()
//
//   var res = SellersCompleteAMatch(a, b, scoringSсheme)
//
//    var sol = ImplicitSemiLocalSA(a,b,scoringSсheme,
//        ReducingKernelEvaluation {
//            PermutationMatrixTwoLists(
//                listOf(),
//                0,
//                0
//            )
//        })
//    var res2 = CompleteAMatchViaSemiLocalTotallyMonotone(sol)
//    println("JAJAJAJ")
//    NaiveSemiLocalLCS(a,b).print()
////    println()
////    sol.print()
//    val s = ThresholdWindowSemiLocal(
//        sol,
//        CompleteAMatchViaSemiLocalTotallyMonotone(sol)
//    )
//    println("hat")
//    val k = ThresholdAMathViaSemiLocal(
//        NaiveSemiLocalSA(a,b,scoringSсheme),
//        CompleteAMatchViaSemiLocalTotallyMonotone( NaiveSemiLocalSA(a,b,scoringSсheme))
//    ).solve(5.0)
//    k.forEach { print(it) }
//    s.solve(7.0,12).forEach { print("${it}") }
////
//    println()
////    res.solve()
//    for( i in res.solve() ){
//        print("$i  ")
//    }
////    for( i in res.solve().withIndex() ){
//////        println("${i.value}-${i.index}")
//////        if(i.value==i.index){
//////            print("[empty]")
//////        }
////        print(b.subList(i.value,i.index))
////    }
//    println()
//    for(i in res2.solve()){
//        print("$i  ")
//    }
////    println()
////    for( i in res2.solve().withIndex() ){
//////        println("${i.value}-${i.index}")
//////        if(i.value==i.index){
//////            print("[empty]")
//////        }
////        print(b.subList(i.value,i.index))
////    }
//    //for(it in SellersCompleteAMatch().solve(a,b,scoringSheme).withIndex()){
//
//      //  print("${it.value}-${it.index},") }
//
//
////println(res.solve().forEach  (::println))
//    println()
//    val kernel = ReducingKernelEvaluation<Char, PermutationMatrixTwoLists>({
//        PermutationMatrixTwoLists(
//            listOf(),
//            0,
//            0
//        )
//    })
////
////    print( ImplicitSemiLocalSA(a,b,scoringSheme,kernel).stringSubstringSA(0,0))
//
//
//    println()
//   var  t = NaiveSemiLocalSA(a,b,scoringSсheme)
//       t.print()
//    println("FF")
//    println(t.stringSubstringSA(0,3))
////    println(
////    ImplicitSemiLocalSA(a,b,scoringSheme,kernel).stringSubstringSA(0,2)
////    )
////    CountingQuery.dominanceMatrix(
////    longestCommonSubsequence.staggeredStickyMultiplication(
////        utils.PermutationMatrixTwoLists(listOf(Position2D(0,1),Position2D(1,0)),2,2),
////        utils.PermutationMatrixTwoLists(listOf(Position2D(0,0),Position2D(1,1)),2,2),1),CountingQuery.topRightSummator).forEach {
////        it.forEach { print("$it  ") }
////        println()
////    }
////    kernel.evaluate("a".toList().map { longestCommonSubsequence.Symbol(it,longestCommonSubsequence.SymbolType.AlphabetSymbol) },"b".toList().map { longestCommonSubsequence.Symbol(it,longestCommonSubsequence.SymbolType.AlphabetSymbol) }).print()
////    val m = prefixAlignment("aaaaa".toList(),"".toList(), ScoringScheme(144.0,24.0,12.0))
////    println(m)
////    val exp = NaiveSemiLocalSA("aaaaa".toList(),"aaaaa".toList(), ScoringScheme(144.0,24.0,12.0))
////    exp.print()
////println(exp.prefixSuffixSA(1,0))
////    println(exp.stringSubstringSA(0,4))
//
////
////    val n = longestCommonSubsequence.NaiveSemiLocalLCS("aba".toList(),"acaa".toList())
////    n.print()
////    println(n.prefixSuffixLCS(1,3))
////    val points = listOf(utils.Position2D(1,2),utils.Position2D(2,3),utils.Position2D(3,5),utils.Position2D(4,7),utils.Position2D(5,4),utils.Position2D(6,1))
////    val tree = utils.RangeTree2D(points)
////    print(tree.ortoghonalQuery(utils.IntervalQuery(1,50),utils.IntervalQuery(1,80)))
//
//    //an anti monge
//    val  matrix = listOf(
//        listOf(-25, -21, -13, - 10),
//        listOf(-25, -21, -13, - 10)
////        listOf(-42, - 35, - 26, - 20),
////        listOf(-57, - 48, - 35, - 28),
////        listOf(-78, - 65, - 51, - 42),
////        listOf(-90, - 76, - 58 ,- 48)
//    )
//    //transpose ,
//
//
//    //TODO ask tiskin wrong formula? what about totally monotonne?
//    fun scoreTransformer(value:Double,i:Int,j:Int):Double {
//        return value * (scoringSсheme.getMatchScore() - (2 * scoringSсheme.getGapScore())).toDouble() +
//                (a.size + j - i) * scoringSсheme.getGapScore().toDouble()
//    }
//
//    val n = t.b.size+1
//    val m = t.a.size
//    var c =
////        rowMinima({i,j-> -matrix[1-j][3-i].toDouble()},4,2)
//        rowMinima({i,j->-scoreTransformer(t.matrix[ m+n-1-j][n-1-i],m+n-1-j,n-1-i)},n,n)
////    c.reverse()
//    c.reverse()
//
//
//
//
//
//    println()
//    for (i in 0 until n){
//        for(j in 0 until n){
//            print("${scoreTransformer(t.matrix[i+m][j],i+m,j)}  ")
//        }
//        println()
//    }
//
//    println(m)
//    println(n)
//    c.forEachIndexed {index, i ->
//        println("${scoreTransformer(t.matrix[m+n-1-i][index],m+n-1-i,index)}, ${n-1-i} ${index}")
////        println("${matrix[2-1-i][index]}, ${2-1-i}")
////        println(i)
//    }
//
//
//    val windowSemiLocal = WindowSubstringSANaiveImplicit(kernel).solve(a,b,4,scoringSсheme).constructAlignmentPlot()
//    for (i in 0 until  windowSemiLocal.size){
//        for(j in 0 until windowSemiLocal[0].size)
//        {
//            print("${windowSemiLocal[i][j].round(1).toInt()  } ")
//        }
//        println()
//    }
//
//    val mm  = 3
//    val ss = pow(2.0,log2(mm.toDouble()).toInt().toDouble()).toInt()
//    println(ss)
//    println()

    val mmmmm = 10.0
    val dummy = PermutationMatrixTwoLists(listOf(),0,0)

    val arr: Array<Array<PermutationMatrixTwoLists>> = Array(4){ i->Array((mmmmm / 2.0.pow(i.toDouble()).toInt()).toInt()){dummy} }
    canonicalSWindows("145239sa5a".toList(),"14dsa5".toList(), arr as Array<Array<Matrix>>,8,3)
    arr.forEach { it.forEach { println(it.print()) } }

    println(
        ImplicitSemiLocalLCS("39sa".toList(),"14dsa5".toList(),arr[2][1]).stringSubstringLCS(0,4))


    println(
    ImplicitSemiLocalLCS("45239sa5".toList(),"14dsa5".toList(),WindowSubstringLCSImplicit<Char>().secondPhrase(1,9,arr,1,0,8,6)).stringSubstringLCS(0,6))
    }
