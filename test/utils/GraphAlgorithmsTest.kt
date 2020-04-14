package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GraphAlgorithmsTest {

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
        val resMin = minimalSpanningTree(exampleGraph2)
        var resMax = utils.minimalSpanningTree(exampleGraph2, { -it })
        assertEquals(34.0, resMax.second)
    }

    @Test
    fun inverseGraphTest() {
        val actual = inverseGraph(inverseGraph(exampleGraph2))

        for ((a, b) in (exampleGraph2.getAllEdges().toList().sortedBy { it.data } zip actual.getAllEdges().toList()
            .sortedBy { it.data })) {
            assertEquals(a.data, b.data)
            assertEquals(a.from, b.from)
            assertEquals(a.to, b.to)
            assertEquals(a.score, b.score)
        }
        for ((a, b) in (exampleGraph2.asSequence().toList().sortedBy { it.data } zip actual.asSequence().toList()
            .sortedBy { it.data })) {
            assertEquals(a.data, b.data)
            assertEquals(a.index, b.index)
        }

    }

    @Test
    fun stronglyConnectedComponentsTest() {
        assertEquals(2, stronglyConnectedComponents(exampleGraph1).size)
        assertEquals(4, stronglyConnectedComponents(exampleGraph2).size)
        assertEquals(4, stronglyConnectedComponents(exampleGraph3).size)
    }

    @Test
    fun connectedComponentsTest() {
        assertEquals(1, connectedComponents(exampleGraph1).size)
        assertEquals(1, connectedComponents(exampleGraph2).size)
        assertEquals(3, connectedComponents(exampleGraph3).size)
    }

    @Test
    fun rArborosercenceTest() {
        val r = tarjanOptimalBranchingBranching(exampleGraph2, mutableListOf(Vertex(2, 1)), true)
        r.forEach { println(it.data + " " + it.score) }
        assertEquals(22.0, r.fold(0.0, { acc: Double, iEdge: IEdge<String> -> acc + iEdge.score }))

        val r2 = tarjanOptimalBranchingBranching(exampleGraph1, mutableListOf(Vertex(2, 1)), true)
        assertEquals(25.0, r2.fold(0.0, { acc, edge -> acc + edge.score }))

        val r3 = tarjanOptimalBranchingBranching(completeGraph4Example, mutableListOf(Vertex(4, 2)), true)
        assertEquals(5.0, r3.fold(0.0, { acc, edge -> acc + edge.score }))

        val r4 = tarjanOptimalBranchingBranching(exampleGraph4, mutableListOf(), true)
        assertEquals(20.0, r4.fold(0.0, { acc, edge -> acc + edge.score }))
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


        val c = HierarhicalClusteringMinDistance(0.9, 0.7, normalizedGraph).buildHierarchy()
    }

    @Test
    fun mclAlgoTest(){
println(       normalizedGraph.numVertices()
)
        val arr = Array(normalizedGraph.numVertices()){DoubleArray(normalizedGraph.numVertices()){0.0} }

        for( it in normalizedGraph.getAllEdges()){
            arr[it.from-1][it.to-1] = it.score
        }

        mclClustering(arr)
        println()

        mclClustering(
            arrayOf(
                doubleArrayOf(0.0,1.0,1.0,1.0),
                doubleArrayOf(1.0,0.0,0.0,1.0),
                doubleArrayOf(1.0,0.0,0.0,0.0),
                doubleArrayOf(1.0,1.0,0.0,0.0)
            )
        )

    }


}