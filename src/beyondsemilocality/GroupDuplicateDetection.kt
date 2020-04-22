package beyondsemilocality

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.ThresholdAMathViaSemiLocal
import longestCommonSubsequence.ReducingKernelEvaluation
import sequenceAlignment.ExplicitMongeSemiLocalProvider
import sequenceAlignment.ISemiLocalProvider
import sequenceAlignment.ImplicitSemiLocalSA
import utils.*


interface IDuplicateGroup<T> {
    /**
     * List<T>  is a reference to text where is specified interval
     */
    val duplicates: List<Pair<Interval, List<T>>>


    /**
     * list of groups that splited on components /
     */
    fun getComponents(): List<List<List<Interval>>>
}

//
interface IGraphBuilder<E, V> {
    fun build(vertices: List<Vertex<V>>, edges: List<Edge<E>>): IGraph<E, V>
}

//
//interface ISimilarityGraphBuilder<E,V>{
//    fun build():IGraph<E,V>
//}
//
//// G
//class SemiLocalSimilarityGraphBuilder<E,V>(var graphBuilder: IGraphBuilder<E,V>,var semiLocalProvider: ISemiLocalProvider):ISimilarityGraphBuilder<E,V> {
//    override fun build(): IGraph<E, V> {
//
//    }
//
//
//
//}
//
//


interface IMeasureFunction<T> {
    fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>>
}


//local
class BoundedLengthSWMeasureFunction<T>(
    var provider: IFragmentSubstringProvider<T>,
    var scheme: IScoringScheme,
    var w: Int
) : IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val bslw = BoundedLengthSmithWatermanAlignment(provider).solve(a, b, scheme, w)
        return Pair(bslw.first.score, b.subList(bslw.second.startInclusive, bslw.second.endExclusive))
    }
}
//TODO BoundendLengthSWAccumulated


//TODO normalized blsw measure in article

//TODO semi-local also prefix-suffix and reverse

//string- substring
class StringSubstringMeasureFunction<T>(var provider: ISemiLocalProvider, var scheme: IScoringScheme) :
    IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(provider.buildSolution(a, b, scheme))
            .solve().mapIndexed { index: Int, pair: Pair<Int, Double> -> Pair(index, pair) }.maxBy { it.second.second }
        return Pair(aMatch!!.second.second, b.subList(aMatch.second.first, aMatch.first))
    }


}

//TODO what is this ?
class StringSubstringMeasureFunctionAccumulated<T>(
    var provider: ISemiLocalProvider,
    var scheme: IScoringScheme,
    var threshold: Double
) : IMeasureFunction<T> {
    override fun computeSimilarity(a: List<T>, b: List<T>): Pair<Double, List<T>> {
        val clones =
            ThresholdAMathViaSemiLocal(CompleteAMatchViaSemiLocalTotallyMonotone(provider.buildSolution(a, b, scheme)))
                .solve(threshold)
        val len = clones.size
        return Pair(clones.sumByDouble { it.score } / len,
            clones.flatMap { b.subList(it.startInclusive, it.endExclusive) })
    }
}

///**
// *TODO add backtrace
// */
//class GlobalAlignmentMeasureFunction<T>(var scheme: IScoringScheme) : IMeasureFunction<T> {
//    private fun <T> prefixAlignment(a: List<T>, b: List<T>, scoringScheme: IScoringScheme): Double {
//        val scoreMatrix = Array(a.size + 1) { Array(b.size + 1) { 0.0 } }
//        val match = scoringScheme.getMatchScore().toDouble()
//        val mismatch = scoringScheme.getMismatchScore().toDouble()
//        val gap = scoringScheme.getGapScore().toDouble()
//
//        for (i in 1 until scoreMatrix.size) {
//            for (j in 1 until scoreMatrix[0].size) {
//                scoreMatrix[i][j] = java.lang.Double.max(
//                    scoreMatrix[i - 1][j - 1] + (if (a[i - 1] == b[j - 1]) match
//                    else mismatch),
//                    java.lang.Double.max(
//                        scoreMatrix[i - 1][j] + gap,
//                        scoreMatrix[i][j - 1] + gap
//                    )
//                )
//            }
//        }
//        //TODO add backtrace
//
//        return scoreMatrix[a.size][b.size]
//    }
//
////    override fun computeSimilarity(a: List<T>, b: List<T>): Double = prefixAlignment(a, b, scheme)
//}


fun <T> buildMatrix(
    fragments: List<List<T>>,
    graphBuilder: IGraphBuilder<List<T>, List<T>>,
    func: IMeasureFunction<T>
): IGraph<List<T>, List<T>> {
    val vertices = fragments.mapIndexed { index: Int, list: List<T> -> Vertex(index, list) }
    val edges = mutableListOf<Edge<List<T>>>()

    for (i in fragments.indices) {
        for (j in i + 1..fragments.size) {
            val solutionIJ = func.computeSimilarity(fragments[i], fragments[j])
            val solutionJI = func.computeSimilarity(fragments[j], fragments[i])
            edges.add(Edge(i, j, solutionIJ.first, solutionIJ.second))
            edges.add(Edge(j, i, solutionJI.first, solutionJI.second))
        }
    }
    return graphBuilder.build(vertices, edges)
}

//
//class GroupDuplicateDetection(val similarityMeasure: (Interval, ) -> Int) {
//
//    fun <T> fragments(fragments: List<List<T>>, scoringScheme: IScoringScheme, threshold: Double): {
//        //complete graph
//        val similarityMatrix = Array(fragments.size) { Array(fragments.size) { Interval(0, 0, 0.0) } }
//
//        // O(r^2) *O(mn)
//        for (i in fragments.indices) {
//            for (j in i + 1..fragments.size) {
//                val frd = ImplicitSemiLocalSA(
//                    fragments[i],
//                    fragments[j],
//                    scoringScheme,
//                    ReducingKernelEvaluation { dummyPermutationMatrixTwoLists })
//                val frdScore = CompleteAMatchViaSemiLocalTotallyMonotone(frd).solve()
//                    .mapIndexed { j, pair -> Interval(pair.first, j, pair.second) }.maxBy { it.score }!!
//                similarityMatrix[i][j] = frdScore
//
//                val backward = ImplicitSemiLocalSA(
//                    fragments[j],
//                    fragments[i],
//                    scoringScheme,
//                    ReducingKernelEvaluation { dummyPermutationMatrixTwoLists })
//                val backwardScore = CompleteAMatchViaSemiLocalTotallyMonotone(backward).solve()
//                    .mapIndexed { j, pair -> Interval(pair.first, j, pair.second) }.maxBy { it.score }!!
//                similarityMatrix[j][i] = backwardScore
//            }
//        }
//
//
//        // find all strongly-connected components
//    }
//
//    private fun dfs(v: Int, used: BooleanArray, threshold: Double, graph: Array<List<Interval>>) {
//        if (used[v]) return
//        for (edge in graph[v]) {
//            if
//        }
//
//    }
//
//
//}
//
//
//
