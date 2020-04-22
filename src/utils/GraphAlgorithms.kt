package utils

import java.lang.Math.pow
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.random.Random


interface IVertex<V> {
    var index: Int
    var data: V
    fun copy(): IVertex<V>
}

interface IEdge<E> {
    var from: Int
    var to: Int
    var score: Double
    var data: E
    fun copy(): IEdge<E>
}


/**
 *
 */
data class Edge<E>(override var from: Int, override var to: Int, override var score: Double, override var data: E) :
    IEdge<E> {
    override fun copy(): IEdge<E> = Edge(from, to, score, data)

}

/**
 *
 */
data class Vertex<V>(override var index: Int, override var data: V) : IVertex<V> {
    override fun copy(): IVertex<V> = Vertex(index, data)
}


/**
 * not multigraph
 * */
interface IGraph<E, V> : Iterable<IVertex<V>> {

    fun numVertices(): Int

    fun createNewGraph(vertices: List<IVertex<V>>, edges: List<IEdge<E>>): IGraph<E, V>

    fun getAllEdgesFrom(index: Int): List<IEdge<E>>

    fun getAllEdgesTo(index: Int): List<IEdge<E>>


    fun getAllEdges(): List<IEdge<E>>

    //fun addVertex(index: Int, data: V): IVertex<V>

//    fun addEdge(from: Int, to: Int, data: E): IEdge<E>

//    fun getVertex(index: Int): IVertex<V>

//    fun getEdge(from: Int, to: Int): IEdge<E>


}


/**
 *
 */
interface IGraphBuilder<E, V> {
    fun build(vertices: List<Vertex<V>>, edges: List<Edge<E>>): IGraph<E, V>
}

class GraphMatrix<E, V> : IGraph<E, V> {


    private var matrixTo: HashMap<Int, Pair<IVertex<V>, MutableList<IEdge<E>>>> = hashMapOf()
    private var matrixFrom: HashMap<Int, Pair<IVertex<V>, MutableList<IEdge<E>>>> = hashMapOf()

    /**
     *
     */
    constructor(vertices: List<IVertex<V>>, edges: List<IEdge<E>>) {

        for (v in vertices) {
            matrixFrom[v.index] = Pair(v, mutableListOf())
            matrixTo[v.index] = Pair(v, mutableListOf())

        }

        for (edge in edges) {
            matrixFrom[edge.from]!!.second.add(edge)
            matrixTo[edge.to]!!.second.add(edge)
        }
    }

    override fun numVertices(): Int = matrixFrom.size


    override fun createNewGraph(vertices: List<IVertex<V>>, edges: List<IEdge<E>>): IGraph<E, V> =
        GraphMatrix(vertices, edges)


    override fun getAllEdgesFrom(index: Int): List<IEdge<E>> = matrixFrom[index]!!.second

    override fun getAllEdgesTo(index: Int): List<IEdge<E>> = matrixTo[index]!!.second

    override fun getAllEdges(): List<IEdge<E>> = matrixTo.toList().flatMap { it.second.second }

    override fun iterator(): Iterator<IVertex<V>> {
        val vertices = matrixTo.toList().map { it.second.first }

        return object : Iterator<IVertex<V>> {
            var ptr = 0

            override fun hasNext(): Boolean = ptr < vertices.size

            override fun next(): IVertex<V> {
                ptr++
                return vertices[ptr - 1]
            }
        }

    }


}


/**
 * shallow copy of data
 */
fun <E, V> inverseGraph(graph: IGraph<E, V>): IGraph<E, V> {
    val vertices = graph.asSequence().map { it.copy() }.toList()
    val edges = vertices.flatMap { it ->
        graph.getAllEdgesFrom(it.index).map {
            val reverseEdge = it.copy()
            reverseEdge.from = it.to
            reverseEdge.to = it.from
            reverseEdge
        }
    }.toList()
    return graph.createNewGraph(graph.asSequence().toList(), edges)
}


/**
 * Finds all strongly connected componnets in graph
 */
fun <E, V> stronglyConnectedComponents(graph: IGraph<E, V>): List<List<Int>> {

    //val transposed = transposedGraph(graph)
    var used = hashMapOf<Int, Boolean>()//(graph.numVertices()) { false }
    val byPassOrder = mutableListOf<Int>()
    val result = mutableListOf<List<Int>>()
    var component = mutableListOf<Int>()


    fun dfsForwardGraph(vertexIndex: Int) {
        used[vertexIndex] = true
        val edges = graph.getAllEdgesFrom(vertexIndex)
        for (edge in edges) {
            val to = edge.to
            if (!used.containsKey(to)) dfsForwardGraph(to)
        }
        byPassOrder.add(vertexIndex)
    }


    fun dfsTransposedGraph(vertexIndex: Int) {
        used[vertexIndex] = true
        component.add(vertexIndex)
        // aka reverse
        val edges = graph.getAllEdgesTo(vertexIndex)
        for (edge in edges) {
            val to = edge.from
            if (!used.containsKey(to)) {
                dfsTransposedGraph(to)
            }

        }
    }




    for (vertex in graph) {
        if (!used.containsKey(vertex.index)) dfsForwardGraph(vertex.index)
    }

    used = hashMapOf()
    for (vertexIndex in byPassOrder.reversed()) {
        if (!used.containsKey(vertexIndex)) {
            dfsTransposedGraph(vertexIndex)
            result.add(component)
            component = mutableListOf()
        }
    }
    return result

}


/**
 *
 */
fun <E, V> connectedComponents(graph: IGraph<E, V>): List<List<Int>> {

    val used = hashMapOf<Int, Boolean>()
    val components = mutableListOf<List<Int>>()
    var component = mutableListOf<Int>()

    fun dfs(vertexIndex: Int) {
        used[vertexIndex] = true
        component.add(vertexIndex)


        for (edge in graph.getAllEdgesFrom(vertexIndex)) {
            val to = edge.to
            if (!used.containsKey(to)) dfs(to)
        }
        for (edge in graph.getAllEdgesTo(vertexIndex)) {
            val from = edge.from
            if (!used.containsKey(from)) dfs(from)
        }
    }

    val vertices = graph.asSequence().map { it.index }.toList()
    for (vertex in vertices) {
        if (!used.containsKey(vertex)) {
            dfs(vertex)
            components.add(component)
            component = mutableListOf()
        }
    }

    return components


}


/**
 *
 */
fun <E, V> minimalSpanningTree(
    undirectedGraph: IGraph<E, V>,
    f: (Double) -> Double = { x -> x }
): Pair<List<IEdge<E>>, Double> {
//    val edges= undirectedGraph.getAllEdges().map {
//        val edge = it.copy()
//        edge.score = f(edge.score)
//        edge
//    }.sortedBy { it.score }

    val edges = undirectedGraph.getAllEdges().sortedBy { f(it.score) }

    val dsu = DSU()

    val result = mutableListOf<IEdge<E>>()

    //init dsu sets
    for (vertex in undirectedGraph.asSequence().toList()) {
        dsu.makeset(vertex.index)
    }
    var cost = 0.0

    for (edge in edges) {
        val from = edge.from
        val to = edge.to

        if (dsu.find(from) != dsu.find(to)) {
            cost += edge.score
            result.add(edge)

            dsu.union(from, to)
        }


    }

    return Pair(result, cost)
}


//TODO remapping
fun <E, V> tarjanOptimalBranchingBranching(
    graph: IGraph<E, V>,
    rootsBegin: List<IVertex<V>>,
    shouldCoverAllVertices: Boolean
): List<IEdge<E>> {


    data class EdgeNode(
        var edge: IEdge<E>, var from: Int, var to: Int, var weight: Double, var parent: EdgeNode?,
        var children: MutableList<EdgeNode>
    ) {
        var removedFromF = false
    }

    // list of vertices of graph
    val vertices = graph.asSequence().toList()

    // TODO descri[tio
    val edgeWeightChange = hashMapOf<Int, Double>()

    //TODO true??
    //is the vertices that has been identified as the final root in the critical
    //graph. Note that this may be a supervertex, in which case min[root]
    //is the vertex of G that is chosen as the root of the MSA.
    val finalRoots = mutableListOf<Int>()

    //S,W disjoint sets
    // S --- contains all super vertices (aka strongly connected components)
    val S = DSU() // all super vertices aka strongly connected components
    // W --- contains all weakly connected components
    val W = DSU()

    // keeps track of the possible roots of the MSA. If a supervertex repre-sented by v has been chosen as root at the
    // end of the algorithm, then min[v] is the vertex of G that will be the root of the MSA.
    val min = hashMapOf<Int, Int>()

    // contains the strongly connected components that are roots in the
    // current critical graph, i.e., no incoming edge has been chosen for these
    // vertices/supervertices.
    val stronglyConnected: MutableList<Int> = mutableListOf()


    // list of edges pointed to specified vertex
    val edgesTo = hashMapOf<Int, MutableList<EdgeNode>>()


    // contains the edges comprising the cycles of the strongly connected components of the critical graph.
    val cycle = hashMapOf<Int, MutableList<EdgeNode>>()


    //    We use a rooted forrest of edges of G to keep track of the edges currently
    //    in the critical graph. Each time an edge is conceptually chosen as an edge
    //    in the critical graph, it is also added to F.
    val fVertex = mutableListOf<EdgeNode>()


    //    is an array of pointers, pointing to leaves of F. More specifically, if v ∈ V ,
    //    then λ[v] is the leaf of F (u->v) whose head is v. If no such leaf exists, then λ[v] is ∅.
    val lambda = hashMapOf<Int, EdgeNode>()


    //    keeps track of the incoming edges of the strongly connected components in the critical graph.
    //    If v is the representative of the strongly connected component Sv, then enter[v] is the edge of the critical
    //    graph that points into Sv (or ∅ if no such edge exists).
    val enter = hashMapOf<Int, EdgeNode>()


    // if vertex is specified as root then it would be in final answer
    val asRootSpecified = hashMapOf<Int, Boolean>()


    //add vertices of rootsBegin to finalRoots -> they would be roots in final
    for (root in rootsBegin) {
        asRootSpecified[root.index] = true
        finalRoots.add(root.index)
    }

    //fill
    for (vertex in vertices) {
        //TODO add fast sort and delete multi edges
        val edges = graph.getAllEdgesTo(vertex.index)
            .sortedBy { it.from }.map { EdgeNode(it, it.from, it.to, it.score, null, mutableListOf()) }
            .toMutableList()
        edgesTo[vertex.index] = edges
        S.makeset(vertex.index)
        W.makeset(vertex.index)
        edgeWeightChange[vertex.index] = 0.0

        cycle[vertex.index] = mutableListOf()

        min[vertex.index] = vertex.index
        if (!asRootSpecified.containsKey(vertex.index)) {
            stronglyConnected.add(vertex.index)
        }
    }


    while (stronglyConnected.isNotEmpty()) {

        val curRoot = stronglyConnected.last()

        stronglyConnected.removeAt(stronglyConnected.size - 1)

        // if no incoming edges to curRoot exists then it should be root
        if (edgesTo[curRoot]!!.isEmpty()) {
            finalRoots.add(min[curRoot]!!)
            continue
        }

        // criticalEdge:= (c->curRoot) optimal by max that point to curRoot
        val criticalEdge = edgesTo[curRoot]!!.maxBy { it.weight }!!

        // Do not add critical_edge if it worsens the total
        // weight and we are not attempting to span.
        if (!shouldCoverAllVertices && criticalEdge.weight < 0) {
            finalRoots.add(min[curRoot]!!)
            continue
        }

        fVertex.add(criticalEdge)
        // and foreach  edges in cycle[curRoot]  become parent
        for (edge in cycle[curRoot]!!) {
            edge.parent = criticalEdge
            criticalEdge.children.add(edge)
        }

        // If critical_edge is a leaf in "F", then add a
        // pointer to it.
        // no cycle exists -> is leaf
        if (cycle[curRoot]!!.isEmpty()) {
            lambda[curRoot] = criticalEdge
        }


        // If adding critical_edge didn't create a cycle
        if (W.find(criticalEdge.from) != W.find(criticalEdge.to)) {
            W.union(criticalEdge.from, criticalEdge.to)
            enter[curRoot] = criticalEdge
        } else {
            // Find the edges of the cycle, the
            // representatives of the strong components in the
            // cycle, and the least costly edge of the cycle.

            enter.remove(curRoot)
//            enter[curRoot] = null

            val cycleEdges = mutableListOf(criticalEdge)

            val cycleRepr = mutableListOf(S.find(criticalEdge.to))


            var minWeightedEdge = criticalEdge
            var vertex = S.find(criticalEdge.from)
            while (enter[vertex] != null) {
                cycleEdges.add(enter[vertex]!!)
                cycleRepr.add(vertex)
                if (enter[vertex]!!.weight < minWeightedEdge.weight) minWeightedEdge = enter[vertex]!!
                vertex = S.find(enter[vertex]!!.from)
            }

            // change the weight of the edges entering
            // vertices of the cycle.
            //substract

            for (edge in cycleEdges) {
                edgeWeightChange[S.find(edge.to)] = minWeightedEdge.weight - edge.weight
            }

            // Save the vertex that would be root if the newly
            // created strong component would be a root.
            val cycleRoot = min[S.find(minWeightedEdge.to)]!!


            // Union all components of the cycle into one component.
            var newRepresentative = cycleRepr.first()
            for (vertexRepr in cycleRepr) {
                //TODO link vs union
                S.union(vertexRepr, newRepresentative)
                newRepresentative = S.find(newRepresentative)
            }



            min[newRepresentative] = cycleRoot
            stronglyConnected.add(newRepresentative)
            cycle[newRepresentative] = cycleEdges

            for (v in cycleRepr) {
                for (edge in edgesTo[v]!!) {
                    edge.weight += edgeWeightChange[v]!!
                }
            }

            // Merge all in_edges of the cycle into one list.
            // merge in last
            for (i in 1 until cycleRepr.size) {
                val newEdges = mutableListOf<EdgeNode>()
                val l2 = edgesTo[cycleRepr[i]]!!
                val l1 = edgesTo[cycleRepr[i - 1]]!!
                var l1Index = 0
                var l2Index = 0

                //
                while (l1Index < l1.size || l2Index < l2.size) {
                    //while edges point to the same strongly connected component
                    while (l1Index < l1.size && S.find(l1[l1Index].from) == newRepresentative) l1Index++
                    while (l2Index < l2.size && S.find(l2[l2Index].from) == newRepresentative) l2Index++

                    //if reached end then break
                    if (l1Index == l1.size && l2Index == l2.size) break
                    when {
                        l1Index == l1.size -> {
                            newEdges.add(l2[l2Index])
                            l2Index++
                        }
                        l2Index == l2.size -> {
                            newEdges.add(l1[l1Index])
                            l1Index++
                        }
                        l1[l1Index].from < l2[l2Index].from -> {
                            newEdges.add(l1[l1Index])
                            l1Index++
                        }
                        l1[l1Index].from > l2[l2Index].from -> {
                            newEdges.add(l2[l2Index])
                            l2Index++
                        }
                        else -> {
                            val l1Elem = l1[l1Index]
                            val l2Elem = l2[l2Index]
                            if (l1Elem.weight > l2Elem.weight) {
                                newEdges.add(l1Elem)
                            } else {
                                newEdges.add(l2Elem)
                            }

                            l1Index++
                            l2Index++
                        }
                    }
                }

                //TODO is there need to clear
                edgesTo[cycleRepr[i]]!!.clear()
                edgesTo[cycleRepr[i]] = newEdges
            }

            edgesTo[newRepresentative] = edgesTo[cycleRepr.last()]!!
            edgeWeightChange[newRepresentative] = 0.0
            //edge_weight_change[new_repr] = weight_t(0);
        }

    }

    fun removeFromF(edgeNode: EdgeNode, fRoots: MutableList<EdgeNode>) {
        var edge: EdgeNode? = edgeNode
        while (edge != null) {
            edge.removedFromF = true
            for (child in edge.children) {
                fRoots.add(child)
                child.parent = null
            }
            //free
            edge.children = mutableListOf()
            //
            edge = edge.parent
        }
    }

    // Extract the optimum branching

    // Find all roots of F.
    val fRoots = fVertex.filter { it.parent == null }.toMutableList()

    // Remove edges entering the root nodes.
    for (v in finalRoots) {
        if (lambda[v] != null) {
            removeFromF(lambda[v]!!, fRoots)
        }
    }

    val result = mutableListOf<IEdge<E>>()

    while (fRoots.isNotEmpty()) {
        val edgeNode = fRoots.last()
        fRoots.removeAt(fRoots.size - 1)
        if (edgeNode.removedFromF) {
            continue
        }
        result.add(edgeNode.edge)
//        TODO
        removeFromF(lambda[edgeNode.to]!!, fRoots)

    }

    return result


}


/**
 *
 */
class DSU(seed: Int = 42) {

    private val parent = hashMapOf<Int, Int>()
    private val random = Random(seed)

    fun makeset(v: Int) {
        parent[v] = v
    }


    fun find(vertex: Int): Int {
        if (vertex == parent[vertex]!!) return vertex
        else {
            parent[vertex] = find(parent[vertex]!!)
            return parent[vertex]!!
        }
    }

    fun union(v: Int, u: Int) {
        var vParent = parent[v]!!
        var uParent = parent[u]!!
        if (random.nextBoolean()) {
            val tmp = vParent
            vParent = uParent
            uParent = tmp
        }
        if (vParent != uParent) parent[vParent] = uParent

    }
}


interface HierarhicalNode {

}


//TODO make pretty print
interface INode<V> {
    var head: V
    var children: MutableList<INode<V>>
    override fun toString(): String
}


/**
 * Reprsenet cluster that contains
 */
data class Cluster<V>(override var head: V, override var children: MutableList<INode<V>>) : INode<V> {
    override fun toString(): String {
        return "[ cluster:" + head.toString() + "\n" +
                "childrens: " + if (children.isEmpty()) " None" else children.map { it.toString() }.joinToString(
            separator = ","
        ) +
                "]"

    }

}

/**
 * Reprsent leafs
 */
data class Leaf<V>(override var head: V) : INode<V> {
    override var children: MutableList<INode<V>> = mutableListOf()
    override fun toString(): String = "[ Leaf" + head.toString() + " ]"
}

/**
 * Represent clique
 */
data class CliqueNode<V>(override var head: V, override var children: MutableList<INode<V>>) : INode<V> {
    override fun toString(): String {
        return "[ clique:" + head.toString() +
                " childrens: " + if (children.isEmpty()) " None" else children.map { it.toString() }.joinToString(
            separator = ","
        ) +
                "]"

    }
}


abstract class AbstractHierarchicalClustering<E, V>(internal var initialGraph: IGraph<E, V>) {

    internal abstract fun init()

    /**
     *
     */
    abstract fun isCliqueConnected(cluster1: Int, cluster2: Int, graph: IGraph<E, V>): Boolean

    /**
     *
     */
    abstract fun isClusterConnected(cluster1: Int, cluster2: Int, graph: IGraph<E, V>): Boolean

    /**
     *
     */
    abstract fun buildNewMatrix(oldGraph: IGraph<E, V>, cluster1: Int, cluster2: Int): Pair<Int, IGraph<E, V>>


    /**
     *
     */
    abstract fun clusterHead(cluster1: Int, cluster2: Int): IVertex<V>

    /**
     *
     */
    fun buildHierarchy(): List<INode<IVertex<V>>> {


        val roots: MutableList<Pair<Int, INode<IVertex<V>>>> =
            initialGraph.map { Pair(it.index, Leaf(it)) }.toMutableList()

        //initalizes
        init()

        var curGraph = initialGraph

        loop@ while (roots.size > 1) {

            val maxEdge = curGraph.getAllEdges().maxBy { it.score } ?: break

            val from = maxEdge.from
            val to = maxEdge.to
            val root1 = roots.find { it.first == from }!!
            val root2 = roots.find { it.first == to }!!

            val clusterHead = clusterHead(root1.first, root2.first)

            val isCliqueConnected = isCliqueConnected(from, to, curGraph)
            val isClusterConnected = isClusterConnected(from, to, curGraph)
            val newRoot: INode<IVertex<V>> = when {
                root1.second is Leaf<*> && root2.second is Leaf<*> && isCliqueConnected ->
                    CliqueNode(
                        clusterHead,
                        mutableListOf(root1.second, root2.second)
                    )
                root1.second is Leaf<*> && root2.second is CliqueNode<*> && isCliqueConnected -> {
                    val tmp = root2.second
                    tmp.children.add(root1.second)
                    tmp
                }
                root2.second is Leaf<*> && root1.second is CliqueNode<*> && isCliqueConnected -> {
                    val tmp = root1.second
                    tmp.children.add(root2.second)
                    tmp
                }
                root2.second is CliqueNode<*> && root1.second is CliqueNode<*> && isCliqueConnected -> {
                    CliqueNode(
                        clusterHead,
                        (root1.second.children + root2.second.children).toMutableList()
                    )
                }
                (!isCliqueConnected && !isClusterConnected) -> break@loop
                else -> Cluster(clusterHead, mutableListOf(root1.second, root2.second))
            }


            val res = buildNewMatrix(curGraph, root1.first, root2.first)
            val clusterIndex = res.first

            //update matrix
            curGraph = res.second

            //remove old clusters from roots
            roots.remove(root1)
            roots.remove(root2)


            roots.add(Pair(clusterIndex, newRoot))
        }

        return roots.map { it.second }

    }

}


class HierarhicalClusteringMinDistance<E, V>(
    var thresholdClique: Double,
    var thresholdCluster: Double,
    initialGraph: IGraph<E, V>
) : AbstractHierarchicalClustering<E, V>(initialGraph) {

    // which nodes in one disjoint cluster
    private val clusters = DSU()

    override fun init() {
        // which nodes in one disjoint cluster
        for (v in initialGraph) {
            clusters.makeset(v.index)
        }

    }


    private fun minEdge(cluster1: Int, cluster2: Int, graph: IGraph<E, V>) = graph
        .getAllEdgesFrom(cluster1).plus(graph.getAllEdgesFrom(cluster2))
        .filter { (it.from == cluster1 && it.to == cluster2) || (it.from == cluster2 && it.to == cluster1) }
        .minBy { it.score }

    override fun isCliqueConnected(cluster1: Int, cluster2: Int, graph: IGraph<E, V>): Boolean {
        val res = minEdge(cluster1, cluster2, graph)
        return res != null && res.score > thresholdClique
    }

    override fun isClusterConnected(cluster1: Int, cluster2: Int, graph: IGraph<E, V>): Boolean {
        val res = minEdge(cluster1, cluster2, graph)
        return res != null && res.score > thresholdCluster
    }

    override fun buildNewMatrix(oldGraph: IGraph<E, V>, cluster1: Int, cluster2: Int): Pair<Int, IGraph<E, V>> {
        //all merged to cluster 2

        val newVertices = oldGraph.filter { it.index != cluster1 }
        val newEdges = oldGraph.getAllEdges().filterNot {
            (it.from == cluster1 && it.to == cluster2) || (it.to == cluster1 && it.from == cluster2)
        }.map {
            val edge = it.copy()
            when {
                edge.to == cluster1 -> edge.to = cluster2
                edge.from == cluster1 -> edge.from = cluster2
            }
            edge
        }

        val mapa = hashMapOf<Pair<Int, Int>, IEdge<E>>()

        for (edge in newEdges) {
            val key = Pair(edge.from, edge.to)
            if (mapa.containsKey(key) && mapa[key]!!.score > edge.score) {
                mapa[key] = edge
            } else if (!mapa.containsKey(key)) mapa[key] = edge
        }
        //merge clusters
        clusters.union(cluster1, cluster2)

        return Pair(cluster2, oldGraph.createNewGraph(newVertices, mapa.map { it.value }))

    }

    override fun clusterHead(cluster1: Int, cluster2: Int): IVertex<V> {
        var set1 = initialGraph.filter { clusters.find(it.index) == clusters.find(cluster1) }
        val set2 = initialGraph.filter { clusters.find(it.index) == clusters.find(cluster2) }
        if (set1.size < set2.size) set1 = set2
        return set1.first()
    }
}


/**
 * finds cluster via mcl cluster
 * @return cluster assignment. Note that nodes not clustered will be in group 0;
 */
fun mclClustering(
    initialGraph: Array<DoubleArray>,
    r: Int = 2,
    p: Int = 2,
    iterationNum: Int = 100,
    addSelfLoops: Boolean = true
): Array<Int> {


    val algoEpsilon = epsilon
    val roundN = 8


    fun matrixMult(matrix1: Array<DoubleArray>, matrix2: Array<DoubleArray>): Array<DoubleArray> {
        val newMatrix = Array(matrix1.size) { DoubleArray(matrix1.size) { 0.0 } }
        for (i in matrix1.indices) {
            for (j in matrix1.indices) {
                var tmp = 0.0
                for (k in matrix1.indices) {
                    tmp += (matrix1[i][k] * matrix2[k][j]).round(roundN)
                }
                if (tmp < algoEpsilon) newMatrix[i][j] = 0.0
                else newMatrix[i][j] = tmp
            }
        }
        return newMatrix
    }

    fun columnRaiseAndNormalize(matrix: Array<DoubleArray>, j: Int, degree: Int): Boolean {
        val columnElems = matrix.indices.map { matrix[it][j].pow(degree).round(roundN) }
        val sum = columnElems.sum()
        val sumSq = columnElems.map { it * it }.sum()

        columnElems.forEachIndexed { index, it ->
            val n = (it / sum).round(roundN)
            if (n < algoEpsilon) matrix[index][j] = 0.0
            else matrix[index][j] = n
        }

        //check variance
//        println(sumSq  - sum * sum/matrix.size )
        return sumSq - (sum * sum) / matrix.size > algoEpsilon   // variance in column too high
    }

    fun normalizeMatrix(matrix: Array<DoubleArray>, degree: Int): Boolean {
        var lowVariance = true
        for (j in matrix.indices) {
            if (columnRaiseAndNormalize(matrix, j, degree)) {
                lowVariance = false
            }
        }
        return lowVariance
    }

    fun isSteadyState(a: Array<DoubleArray>, b: Array<DoubleArray>): Boolean {
        for (i in a.indices) {
            for (j in a.indices) {
                if (!a[i][j].isEquals(b[i][j])) return false
            }
        }

        return true
    }

    fun binPowN(matrix: Array<DoubleArray>, n: Int): Array<DoubleArray> {
        if (n == 1) return matrix
        if (n % 2 == 1) return matrixMult(binPowN(matrix, n - 1), matrix)
        else {
            val b = binPowN(matrix, n / 2)
            return matrixMult(b, b)
        }

    }


    val normalizedMatrix = Array(initialGraph.size) { i -> DoubleArray(initialGraph.size) { j -> initialGraph[i][j] } }


    // add self loops
    if (addSelfLoops) {
        normalizedMatrix.indices.forEach { normalizedMatrix[it][it] = 1.0 }
    }

    //normalize matrix
    normalizeMatrix(normalizedMatrix, 1)

    var prevMatrix: Array<DoubleArray>
    var curMatrix = normalizedMatrix

    for (iter in 0 until iterationNum) {
        prevMatrix = curMatrix
        curMatrix = binPowN(curMatrix, r)

        normalizeMatrix(curMatrix, p)
//        if (normalizeMatrix(curMatrix, p)) break

        //works better
        if (isSteadyState(curMatrix, prevMatrix)) break
    }


    val maxJ = DoubleArray(curMatrix.size) { 0.0 }
    //cluster as
    val clusterAss = Array(curMatrix.size) { 0 }


    var group = 1
    for (i in curMatrix.indices) {
        var isGroupDetected = false

        for (j in curMatrix.indices) {
            if (curMatrix[i][j] > maxJ[j]) {
                clusterAss[j] = group
                maxJ[j] = curMatrix[i][j]
                isGroupDetected = true
            }
            print("${curMatrix[i][j].round(3)}   ")
        }
        println()
        if (isGroupDetected) group++
    }

    return clusterAss
}




