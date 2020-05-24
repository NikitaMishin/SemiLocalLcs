package application

import com.fasterxml.jackson.databind.ObjectMapper
import duplicateDetection.IApproximateMatching
import duplicateDetection.IMeasureFunction
import utils.IScoringScheme
import utils.Interval
import kotlin.math.exp


/**
 * Duplicate detection task that will be processed and formed
 */
interface ITaskDuplicateDetection {

    /**
     * Launches this task
     */
    fun processTask()

    /**
     * build json report for completed task
     */
    fun buildJSONReport(): String

}


/**
 * Task for approximate matching via analysis of semi-local matrix
 */
class TaskApproximateMatchingViaSemiLocal<T>(
        val approximMatchingAlgo: IApproximateMatching<Element<T, Int>>,
        val pattern: List<Element<T, Int>>,
        val text: List<Element<T, Int>>,
        val textRaw: String,
        val patternRaw: String,
        val scheme: IScoringScheme
) : ITaskDuplicateDetection {
    var clones = listOf<Interval>()

    override fun processTask() {
        val aMatch = approximMatchingAlgo
        clones = aMatch.find(pattern, text).map {
            Interval(text[it.startInclusive].startPos, text[it.endExclusive - 1].endPos)
        }

    }

    private data class ApproximateMatching(val text: String, val pattern: String, val clones: List<Interval>)

    override fun buildJSONReport(): String {
        val mapper = ObjectMapper()
        val res = ApproximateMatching(textRaw, patternRaw, clones)
        return mapper.writeValueAsString(res)

    }
}


/**
 * Task for group duplicate detection
 * via IGroupDuplicateTree algorithm for group construction as a tree
 * and IMeasureFunction for building similarity graph
 */
class TreeGroupDuplicate<T>(
        private val comments: List<List<Element<T, UnifiedComment>>>,
        private val func: IMeasureFunction<Element<T, UnifiedComment>>,
        private val groupAlgo: IGroupDuplicateTree<T>,
        private val percent: Double
) : ITaskDuplicateDetection {

    private var cloneGroups: MutableList<Pair<List<IEdge<Interval>>, List<IVertex<List<Element<T, UnifiedComment>>>>>> =
            mutableListOf()

    val totalCommentsInProcjet = comments.size

    override fun processTask() {
        val graph = SimilarityGraphBuilder<T>(MatrixGraphBuilder()).buildGraph(comments, func, percent)
        // Convert to raw text position
        graph.getAllEdges().forEach {
            it.data.startInclusive = graph.getVertex(it.to).data[it.data.startInclusive].startPos
            it.data.endExclusive = graph.getVertex(it.to).data[it.data.endExclusive - 1].endPos
        }
        cloneGroups = groupAlgo.solve(graph)
    }

    private data class Vertex(val signature: String, val id: Int, val body: String)
    private data class Edge(val from: Int, val to: Int, val cloneInTo: Interval)
    private data class Group(val vertices: List<Vertex>, val edges: List<Edge>)

    override fun buildJSONReport(): String {
        val mapper = ObjectMapper()
        val groups = cloneGroups.sortedByDescending { it.second.size }.map {
            Group(
                    it.second.map {
                        Vertex(it.data.first().ptrData.parentSignature, it.index, it.data.first().ptrData.text.toText())
                    },
                    it.first.map { Edge(it.from, it.to, it.data) })
        }
        println("Total comments in project:${totalCommentsInProcjet}")
        println("Clones: ${groups.sumBy { it.vertices.size }}")

        return mapper.writeValueAsString(groups)
    }

}


/**
 * Build forest of trees from given graph builded on comments
 */
interface IGroupDuplicateTree<T> {
    fun solve(graph: IGraph<Interval, List<Element<T, UnifiedComment>>>)
            : MutableList<Pair<List<IEdge<Interval>>, List<IVertex<List<Element<T, UnifiedComment>>>>>>
}


/**
 * Markov clustering algorithm with maximum spanning tree
 */
class MCLWithSpanningTree<T> : IGroupDuplicateTree<T> {


    private val cloneGroups: MutableList<Pair<List<IEdge<Interval>>, List<IVertex<List<Element<T, UnifiedComment>>>>>> =
            mutableListOf()


    override fun solve(graph: IGraph<Interval, List<Element<T, UnifiedComment>>>):
            MutableList<Pair<List<IEdge<Interval>>, List<IVertex<List<Element<T, UnifiedComment>>>>>> {

        val retrievedDoubleArray = Array(graph.numVertices()) { DoubleArray(graph.numVertices()) }
        for (edge in graph.getAllEdges()) {
            //add symmetricy
            val s = exp(edge.score)
            if (s >= retrievedDoubleArray[edge.from][edge.to]) {
                retrievedDoubleArray[edge.from][edge.to] = s
                retrievedDoubleArray[edge.to][edge.from] = s
            }
        }


//        val s =mclClustering(retrievedDoubleArray)
//        println(s)
//val s2 = mclClustering(retrievedDoubleArray,3,3,100,false)
//        println(s2)
//
//        val se  = mclClustering(retrievedDoubleArray,3,3,100,false)
//        println(se)
//        val s3  = mclClustering(retrievedDoubleArray,addSelfLoops = false)
//        println(s3)
//        val s5  = mclClustering(retrievedDoubleArray,r=5,addSelfLoops = false)
//        println(s5)
//        val s6  = mclClustering(retrievedDoubleArray,p=5,addSelfLoops = false)
//        println(s6)
//
//
//
//        val s7 = mclClustering(retrievedDoubleArray,3,3,100,true)
//        println(s2)
//
//        val s8  = mclClustering(retrievedDoubleArray,3,3,100,true)
//        println(se)
//        val s9  = mclClustering(retrievedDoubleArray,addSelfLoops = true)
//        println(s3)
//        val s10  = mclClustering(retrievedDoubleArray,r=5,addSelfLoops = true)
//        println(s5)
//        val s11  = mclClustering(retrievedDoubleArray,p=5,addSelfLoops = true)

        val groups = mclClustering(retrievedDoubleArray, r = 5)
                .mapIndexed { vertexNumber: Int, clusterName: Int -> Pair(vertexNumber, clusterName) }
                .groupBy { it.second }.filter { it.key != 0 }.map { it.value.map { it.first } }

        for (group in groups) {

            if (group.size == 1) continue
            println(group)
            //            build mispanning tree
            val subgraph = graph.getSubgraph(group)
            //            maximum
            val spanningEdges = minimalSpanningTree(subgraph) { x -> -x }
            val edgesRes = spanningEdges.first

            val verticesRes =
                    (edgesRes.map { subgraph.getVertex(it.to) } + edgesRes.map { subgraph.getVertex(it.from) }).distinct()
            cloneGroups.add(Pair(edgesRes, verticesRes))
        }
        println(cloneGroups.size)

        return cloneGroups
    }
}


/**
 * Orient tree via tarjan algorithm for building maxium branching
 */
class TarjanTree<T> : IGroupDuplicateTree<T> {


    private val cloneGroups: MutableList<Pair<List<IEdge<Interval>>, List<IVertex<List<Element<T, UnifiedComment>>>>>> =
            mutableListOf()


    override fun solve(graph: IGraph<Interval, List<Element<T, UnifiedComment>>>): MutableList<Pair<List<IEdge<Interval>>, List<IVertex<List<Element<T, UnifiedComment>>>>>> {
        val edges = tarjanOptimalBranchingBranching(graph, mutableListOf(), false)
        val vertex = (edges.map { graph.getVertex(it.to) } + edges.map { graph.getVertex(it.from) }).distinct()
        val newGraph = graph.createNewGraph(vertex, edges)
        val connComponents = connectedComponents(newGraph)

        for (component in connComponents) {
            val orientTree = newGraph.getSubgraph(component)
            cloneGroups.add(Pair(orientTree.getAllEdges(), orientTree.toList()))
        }

        return cloneGroups
    }


}


/**
 *  Build similarity  matrix for given comments with edges via  measure function func
 *  edge (from,to) exists if and only if it score>=thershold
 */
interface ISimilarityGraphBuilder<T> {
    fun buildGraph(
            fragments: List<List<Element<T, UnifiedComment>>>,
            func: IMeasureFunction<Element<T, UnifiedComment>>,
            thresholdPercent: Double
    ): IGraph<Interval, List<Element<T, UnifiedComment>>>
}


class SimilarityGraphBuilder<T>(private var graphBuilder: IGraphBuilder<Interval, List<Element<T, UnifiedComment>>>) :
        ISimilarityGraphBuilder<T> {

    override fun buildGraph(
            fragments: List<List<Element<T, UnifiedComment>>>,
            func: IMeasureFunction<Element<T, UnifiedComment>>,
            thresholdPercent: Double
    ): IGraph<Interval, List<Element<T, UnifiedComment>>> {
        val vertices: List<Vertex<List<Element<T, UnifiedComment>>>> =
                fragments.mapIndexed { index: Int, list -> Vertex(index, list) }
        val edges: MutableList<Edge<Interval>> = mutableListOf()
        for (i in 0 until fragments.size) {
            println("$i from ${fragments.size}")
            for (j in i + 1 until fragments.size) {
                val solutionIJ = func.computeSimilarity(fragments[i], fragments[j], thresholdPercent)
                val solutionJI = func.computeSimilarity(fragments[j], fragments[i], thresholdPercent)
                if (solutionIJ.second) edges.add(Edge(i, j, solutionIJ.first.score, solutionIJ.first))
                if (solutionJI.second) edges.add(Edge(j, i, solutionJI.first.score, solutionJI.first))
            }
        }
        return graphBuilder.build(vertices, edges)
    }
}

