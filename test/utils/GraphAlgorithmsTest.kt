package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.assertTrue

internal class GraphAlgorithmsTest {

    val randomizer = Random(42)

    fun randomGraphSize() = randomizer.nextInt(2, 50)

    /**
     *
     */
    private val emptyGraph = "emptyGraph"

    /**
     *
     */
    private val completeGraph3Unweighted = "completeGraph3Unweighted"

    /**
     *
     */
    private val graph6NoEdges = "graph6NoEdges"

    /**
     *
     */
    private val graphComplete10Unweighted = "graphComplete10Unweighted"

    /**
     *
     */
    private val randomGraphOriented1 = "randomGraph1"

    /**
     *
     */
    private val randomGraphOriented2 = "randomGraph2"

    /**
     *
     */
    private val randomGraphOriented3 = "randomGraph2"


    /**
     *
     */
    private val treeGraphOriented5 = "treeGraphOriented"


    /**
     *
     */
    private val noStronglyConnectedComponents = "noStronglyConnectedComponents"

    /**
     *
     */
    private val stronglyConnectedComponents3 = "StronglyConnectedComponents"


    val graphSet: HashMap<String, GraphMatrix<String, Int>> = hashMapOf(
        Pair(
            graph6NoEdges, GraphMatrix(
                mutableListOf(
                    Vertex(1, 1), Vertex(2, 2),
                    Vertex(3, 3), Vertex(4, 4),
                    Vertex(5, 5), Vertex(6, 6)
                ), mutableListOf<Edge<String>>()
            )
        ),
        Pair(emptyGraph, GraphMatrix<String, Int>(mutableListOf(), mutableListOf())),
        Pair(
            completeGraph3Unweighted, GraphMatrix(
                mutableListOf(
                    Vertex(1, 1),
                    Vertex(2, 2),
                    Vertex(3, 3)
                ),
                mutableListOf(
                    Edge(1, 2, 1.0, "1->2"),
                    Edge(1, 3, 1.0, "1->3"),

                    Edge(2, 1, 1.0, "2->1"),
                    Edge(2, 3, 1.0, "2->3"),

                    Edge(3, 1, 1.0, "3->1"),
                    Edge(3, 2, 1.0, "3->2")
                )
            )
        ),
        Pair(graphComplete10Unweighted,
            GraphMatrix(
                (0 until 10).map { Vertex(it, it) },
                (0 until 10).flatMap { i -> (0 until 10).map { j -> Pair(i, j) } }
                    .filter { it.first != it.second }
                    .map { Edge(it.first, it.second, 1.0, "${it.first}->${it.second}") }
            )),

        Pair(randomGraphOriented1, getRandomGraph()),
        Pair(randomGraphOriented2, getRandomGraph()),
        Pair(randomGraphOriented3, getRandomGraph()),
        Pair(
            treeGraphOriented5, GraphMatrix(
                (0 until 6).map { Vertex(it, it) },
                mutableListOf(
                    Edge(0, 1, 5.0, "0->1"),
                    Edge(1, 0, 5.0, "1->0"),

                    Edge(3, 1, 4.0, "3->1"),
                    Edge(1, 3, 4.0, "1->3"),

                    Edge(2, 4, 8.0, "2->4"),
                    Edge(4, 2, 8.0, "4->2"),

                    Edge(5, 3, 1.0, "5->3"),
                    Edge(3, 5, 1.0, "3->5")

                )
            )
        ),

        Pair(
            stronglyConnectedComponents3, GraphMatrix(
                (0 until 6).map { Vertex(it, it) },
                mutableListOf(
                    Edge(0, 1, 5.0, "0->1"),
                    Edge(1, 0, 6.0, "1->0"),

                    Edge(3, 2, 4.0, "3->2"),
                    Edge(2, 3, 4.0, "2->3"),


                    Edge(0, 5, -4.0, "0->5"),


                    Edge(4, 5, 3.0, "4->5"),
                    Edge(5, 4, 8.0, "5->4"),

                    Edge(3, 0, 8.0, "3->0")


                )
            )
        ),


        Pair(
            noStronglyConnectedComponents, GraphMatrix(
                (0 until 6).map { Vertex(it, it) },
                mutableListOf(
                    Edge(0, 1, 5.0, "0->1"),

                    Edge(3, 2, 4.0, "3->2"),


                    Edge(0, 5, -4.0, "0->5"),


                    Edge(5, 4, 8.0, "5->4"),

                    Edge(3, 0, 8.0, "3->0")


                )
            )
        )


    )

    private fun getRandomGraph(): GraphMatrix<String, Int> {
        val size = randomGraphSize()

        return GraphMatrix(
            (0 until size).map { Vertex(it, it) },
            (0 until size).flatMap { i -> (0 until size).map { j -> Pair(i, j) } }
                .filter { it.first != it.second }
                .map { Edge(it.first, it.second, randomizer.nextDouble(-20.0, 20.0), "${it.first}->${it.second}") }
        )

    }

    private var exampleGraph1 = GraphMatrix(
        mutableListOf(
            Vertex(1, 1),
            Vertex(2, 2),
            Vertex(3, 3),
            Vertex(4, 4),
            Vertex(5, 5)
        ),
        mutableListOf(
            Edge(1, 2, 1.0, "1->2"),
            Edge(1, 3, 7.0, "1->3"),
            Edge(1, 4, 2.0, "1->4"),

            Edge(2, 5, 2.0, "2->5"),
            Edge(2, 3, 17.0, "2->3"),

            Edge(3, 1, 4.0, "3->1"),
            Edge(3, 2, 2.0, "3->2"),

            Edge(5, 1, 1.0, "5->1"),
            Edge(5, 2, 8.0, "5->2")
        )

    )

    private var exampleGraph2 = GraphMatrix(
        mutableListOf(
            Vertex(1, 1),
            Vertex(2, 2),
            Vertex(3, 3),
            Vertex(4, 4),
            Vertex(5, 5)
        ),
        mutableListOf(
            Edge(1, 2, 1.0, "1->2"),
            Edge(1, 3, 7.0, "1->3"),
            Edge(1, 4, 2.0, "1->4"),
            Edge(1, 5, 3.0, "1->5"),

            Edge(2, 3, 17.0, "2->3"),

            Edge(3, 2, 2.0, "3->2"),

            Edge(4, 5, 1.0, "4->5"),

            Edge(5, 2, 8.0, "5->2")
        )
    )

    private var exampleGraph3 = GraphMatrix(
        mutableListOf(
            Vertex(1, 1),
            Vertex(2, 2),
            Vertex(3, 3),
            Vertex(4, 4),
            Vertex(5, 5)
        ),
        mutableListOf(
            Edge(2, 1, 1.0, "2->1"),
            Edge(3, 1, 7.0, "3->1"),

            Edge(2, 3, 17.0, "2->3"),

            Edge(3, 2, 2.0, "3->2")

        )
    )


    private var exampleGraph4 = GraphMatrix(
        mutableListOf(
            Vertex(1, 1),
            Vertex(2, 2),
            Vertex(3, 3),
            Vertex(4, 4),
            Vertex(5, 5)
//            Vertex(6, 6)
        ),
        mutableListOf(
            Edge(1, 2, 2.0, "1->2"),
            Edge(2, 3, 3.0, "2->3"),
            Edge(3, 4, 4.0, "3->4"),
            Edge(3, 1, 5.0, "3->1"),
            Edge(4, 1, 5.0, "4->1"),

            Edge(5, 4, 6.0, "5->4"),
            Edge(5, 2, 6.0, "5->2")
        )
    )

    private var completeGraph4Example = GraphMatrix(
        mutableListOf(
            Vertex(1, 1),
            Vertex(2, 2),
            Vertex(3, 3),
            Vertex(4, 4)
        ),
        mutableListOf(
            //
            Edge(1, 2, 1.0, "1->2"),
            Edge(1, 3, 2.0, "1->3"),
            Edge(1, 4, 1.0, "1->4"),

            //
            Edge(2, 1, 1.0, "2->1"),
            Edge(2, 3, 1.0, "2->3"),
            Edge(2, 4, 1.0, "2->4"),

            //
            Edge(3, 1, 1.0, "3->1"),
            Edge(3, 2, 2.0, "3->2"),
            Edge(3, 4, 1.0, "3->4"),

//
            Edge(4, 1, 1.0, "4->1"),
            Edge(4, 2, 1.0, "4->2"),
            Edge(4, 3, 1.0, "4->3")


        )

    )


    @Test
    fun minimalSpanningTree() {


        //check on Empty graph
        assertTrue(minimalSpanningTree(graphSet[emptyGraph]!!).first.isEmpty())


        //check on CompleteGraphUnWeighthened
        assertEquals(2.0, minimalSpanningTree(graphSet[completeGraph3Unweighted]!!).second)

        assertTrue(minimalSpanningTree(graphSet[graph6NoEdges]!!).first.isEmpty())


        //
        assertEquals(18.0, minimalSpanningTree(graphSet[treeGraphOriented5]!!).second)


        assertEquals(16.0, minimalSpanningTree(graphSet[stronglyConnectedComponents3]!!).second)


        assertEquals(21.0, minimalSpanningTree(graphSet[noStronglyConnectedComponents]!!).second)


        assertEquals(34.0, minimalSpanningTree(exampleGraph2) { -it }.second)

        assertEquals(24.0, minimalSpanningTree(exampleGraph3) { -it }.second)


    }

    @Test
    fun inverseGraphTest() {

        for (gr in graphSet) {

            val graph = gr.value
            val actual = inverseGraph(inverseGraph(graph))

            for ((a, b) in (graph.getAllEdges().sortedBy { it.data } zip actual.getAllEdges().toList()
                .sortedBy { it.data })) {
                assertEquals(a.data, b.data)
                assertEquals(a.from, b.from)
                assertEquals(a.to, b.to)
                assertEquals(a.score, b.score)
            }
            for ((a, b) in (graph.asSequence().toList().sortedBy { it.data } zip actual.asSequence().toList()
                .sortedBy { it.data })) {
                assertEquals(a.data, b.data)
                assertEquals(a.index, b.index)
            }

        }


    }

    @Test
    fun stronglyConnectedComponentsTest() {
        assertEquals(2, stronglyConnectedComponents(exampleGraph1).size)
        assertEquals(4, stronglyConnectedComponents(exampleGraph2).size)
        assertEquals(4, stronglyConnectedComponents(exampleGraph3).size)
        assertEquals(3, stronglyConnectedComponents(graphSet[stronglyConnectedComponents3]!!).size)
        assertEquals(2, stronglyConnectedComponents(graphSet[treeGraphOriented5]!!).size)
        assertEquals(1, stronglyConnectedComponents(graphSet[graphComplete10Unweighted]!!).size)
        assertEquals(6, stronglyConnectedComponents(graphSet[noStronglyConnectedComponents]!!).size)
    }

    @Test
    fun connectedComponentsTest() {
        assertEquals(1, connectedComponents(exampleGraph1).size)
        assertEquals(1, connectedComponents(exampleGraph2).size)
        assertEquals(3, connectedComponents(exampleGraph3).size)
        assertEquals(6, connectedComponents(graphSet[graph6NoEdges]!!).size)
        assertEquals(2, connectedComponents(graphSet[treeGraphOriented5]!!).size)
        assertEquals(1, connectedComponents(graphSet[noStronglyConnectedComponents]!!).size)
    }

    @Test
    fun rArborosercenceTest() {
        val r = tarjanOptimalBranchingBranching(exampleGraph2, mutableListOf(Vertex(2, 1)), true)
        assertEquals(22.0, r.fold(0.0, { acc: Double, iEdge: IEdge<String> -> acc + iEdge.score }))

        val r2 = tarjanOptimalBranchingBranching(exampleGraph1, mutableListOf(Vertex(2, 1)), true)
        assertEquals(25.0, r2.fold(0.0, { acc, edge -> acc + edge.score }))

        val r3 = tarjanOptimalBranchingBranching(completeGraph4Example, mutableListOf(Vertex(4, 2)), true)
        assertEquals(5.0, r3.fold(0.0, { acc, edge -> acc + edge.score }))

        val r4 = tarjanOptimalBranchingBranching(exampleGraph4, mutableListOf(), true)
        assertEquals(20.0, r4.fold(0.0, { acc, edge -> acc + edge.score }))

        for (gr in graphSet) {

            val graph = gr.value
            println(gr.key)
            tarjanOptimalBranchingBranching(graph, mutableListOf(), true).forEach { println(it) }
            println(tarjanOptimalBranchingBranching(graph, mutableListOf(), true).sumByDouble { it.score })
            println()
        }
    }

    private var normalizedGraph = GraphMatrix(
        mutableListOf(
            Vertex(1, 1),
            Vertex(2, 2),
            Vertex(3, 3),
            Vertex(4, 4),
            Vertex(5, 5),
            Vertex(6, 6)
        ),
        mutableListOf(
            Edge(1, 2, 0.98, "1->2"),
            Edge(2, 1, 0.98, "2->1"),//should be clique [1,2, 3] [4,5]

            Edge(4, 5, 0.98, "4->5"),
            Edge(5, 4, 0.98, "5->4"),//should be clique [4,5]

            Edge(3, 1, 0.91, "3->1"), // shoukd be clique [1,2,3]
            Edge(1, 3, 0.91, "1->3"), // shoukd be clique [1,2,3]


            Edge(1, 4, 0.75, "1->4"), // shoukd be cluster  [1,2,3], [4,5]
            Edge(4, 1, 0.75, "4->1"), // shoukd be cluster  [1,2,3], [4,5]


            Edge(6, 4, 0.69, "6->4"), // shoukd be cluster  [[1,2,3], [4,5]],[ 6 ]
            Edge(4, 6, 0.69, "4->6") // shoukd be cluster  [[1,2,3], [4,5]],[ 6 ]
        )


    )


    @Test
    fun HierarhicalClusteringMinDistanceTest() {

        for (gr in graphSet) {

            var graph = gr.value
            val sum = graph.getAllEdges().sumByDouble { it.score }
            val edges = graph.getAllEdges().map { it.copy() }
            edges.forEach { it.score = it.score / sum }
            graph = GraphMatrix(graph.toList(), edges)
            graph.getAllEdges().forEach { println(it) }

            println(gr.key)
            val clustering = HierarhicalClusteringMinDistance(0.10, 0.0, graph).buildHierarchy()
            println(clustering.toString())
            println(clustering.size)

            println()
        }

    }

    @Test
    fun mclAlgoTest() {


        for (gr in graphSet) {

            val graph = gr.value
            val maxVertexIndex = graph.toList().maxBy { it.index } ?: continue

            val matrix = Array(maxVertexIndex.index + 1) { DoubleArray(maxVertexIndex.index + 1) { 0.0 } }
            graph.getAllEdges().forEach {
                matrix[it.to][it.from] = if (matrix[it.to][it.from] < it.score) it.score else matrix[it.to][it.from]
                matrix[it.from][it.to] = if (matrix[it.from][it.to] < it.score) it.score else matrix[it.from][it.to]
            }


            println(gr.key)

            for (i in 0 until matrix.size) {
                for (j in 0 until matrix.size) {
                    print("${matrix[i][j].round(3)}   ")

                }
                println()
            }

            println()
            val clusters = mclClustering(matrix, r = 5, p = 2, addSelfLoops = true)

            println("Not in clusters: " +
                    clusters.mapIndexed { index: Int, cluster: Int -> Pair(index, cluster) }.filter { it.second == 0 }
                        .map { it.first }.joinToString(separator = ",")
            )

            clusters.mapIndexed { index, cluster -> Pair(index, cluster) }.filter { it.second != 0 }
                .groupBy { it.second }
                .forEach { println("In cluster ${it.key}: " + it.value.map { it.first }.joinToString(separator = ",")) }


        }
    }
//
//        println(
//            normalizedGraph.numVertices()
////        )
//        val arr = Array(normalizedGraph.numVertices()) { DoubleArray(normalizedGraph.numVertices()) { 0.0 } }
//
//        for (it in normalizedGraph.getAllEdges()) {
//            arr[it.from - 1][it.to - 1] = it.score
//        }
//
//        println(mclClustering(arr).forEach { println(it) })
//        println()


//        mclClustering(
//            arrayOf(
//                doubleArrayOf(0.0, 6.0, 1.0, 6.0),
//                doubleArrayOf(6.0, 0.0, 1.0, 7.0),
//                doubleArrayOf(1.0, 1.0, 0.0, 1.0),
//                doubleArrayOf(6.0, 7.0, 1.0, 0.0)
//            ), p = 2, r = 2, addSelfLoops = true
//        )
//
//
//        mclClustering(
//            arrayOf(
//                doubleArrayOf(0.0, 1.0, 1.0, 0.0, 0.0, 0.0),
//                doubleArrayOf(1.0, 0.0, 1.0, 0.0, 0.0, 0.0),
//                doubleArrayOf(1.0, 1.0, 0.0, 1.0, 0.0, 0.0),
//                doubleArrayOf(0.0, 0.0, 1.0, 0.0, 1.0, 1.0),
//                doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 1.0),
//                doubleArrayOf(0.0, 0.0, 0.0, 1.0, 1.0, 0.0)
//            )
//
//
//        )


}