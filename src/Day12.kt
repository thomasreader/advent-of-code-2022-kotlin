import java.io.Reader

fun main() {
    val testInput = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent()

    val testCliff = getCliffNodes(testInput.reader())
    val testGraph = testCliff.nodes.flatten()
    val testDj = djikstra(testGraph, testCliff.start)
    check(testDj.first[testCliff.end] == 31)

    val cliff = getCliffNodes(reader("Day12.txt"))
    val graph = cliff.nodes.flatten()
    val dj = djikstra(graph, cliff.start)
    println(dj.first[cliff.end])

//    brute force
//    val aList = graph.filter { it.char == 'a' }
//    var minSteps = Int.MAX_VALUE
//    aList.forEachIndexed { index, a ->
//        val endSteps = djikstra(graph, a).first[cliff.end]!!
//        if (endSteps in 0 until minSteps) {
//            minSteps = endSteps
//        }
//        println("${index * 100f / aList.size.toFloat()} %")
//    }
//    println(minSteps)

    val reverseCliff = getCliffNodes(reader("Day12.txt"), true)
    val reverseGraph = reverseCliff.nodes.flatten()
    val reverseDj = djikstra(reverseGraph, reverseCliff.start)
    reverseDj.first
        .filter { cliffDist -> cliffDist.key.char == 'z' && cliffDist.value > 0 }
        .values
        .min()
        .also { println(it) }
}

private fun getCliffNodes(src: Reader, isReverse: Boolean = false): CliffGraph {
    val cliffs = mutableListOf<MutableList<CliffNode>>()
    lateinit var start: CliffNode
    lateinit var end: CliffNode
    var rowIx = 0
    src.forEachLine { row ->
        val cliffRow = mutableListOf<CliffNode>()
        row.forEachIndexed { column, c ->
            when (isReverse) {
                true -> when (c) {
                    'S' -> CliffNode('z', column, rowIx).also { end = it }
                    'E' -> CliffNode('a', column, rowIx).also { start = it }
                    else -> CliffNode(('a'.code + 'z'.code - c.code).toChar(), column, rowIx)
                }.also {
                    it.graph = cliffs
                    cliffRow += it
                }
                false -> when (c) {
                    'S' -> CliffNode('a', column, rowIx).also { start = it }
                    'E' -> CliffNode('z', column, rowIx).also { end = it }
                    else -> CliffNode(c, column, rowIx)
                }.also {
                    it.graph = cliffs
                    cliffRow += it
                }
            }

        }
        cliffs += cliffRow
        rowIx++
    }

    return CliffGraph(start, end, cliffs)
}

data class CliffGraph(
    val start: CliffNode,
    val end: CliffNode,
    val nodes: List<List<CliffNode>>
)

data class CliffNode(
    val char: Char,
    val x: Int,
    val y: Int
) {
    private val height = char.code
    val neighbours: List<CliffNode> by lazy {
        val list = ArrayList<CliffNode>(4)
        // left
        if (x != 0) list += graph[y][x-1]
        // right
        if (x < graph[y].lastIndex) list += graph[y][x+1]
        // up
        if (y != 0) list += graph[y-1][x]
        // down
        if (y < graph.lastIndex) list += graph[y+1][x]

        list.filter { cliff -> (this.height + 1) >= cliff.height }
    }
    lateinit var graph: List<List<CliffNode>>
}

private fun djikstra(graph: List<CliffNode>, source: CliffNode): Pair<Map<CliffNode, Int>, Map<CliffNode, CliffNode?>> {
    val distances: MutableMap<CliffNode, Int> = graph.associateWith { Int.MAX_VALUE }.toMutableMap()
    val prev: MutableMap<CliffNode, CliffNode?> = graph.associateWith { null }.toMutableMap()
    distances[source] = 0
    val queue = ArrayList(graph)
    val seen = mutableListOf<CliffNode>()

    while (queue.isNotEmpty()) {
        val u = run {
            var currentMin = queue[0]
            queue.forEach {
                if (distances[it]!! < distances[currentMin]!!) {
                    currentMin = it
                }
            }
            currentMin
        }

        queue -= u
        seen += u

        u.neighbours.forEach { v ->
            if (distances[v]!! > distances[u]!! + 1) {
                distances[v] = distances[u]!! + 1
                prev[v] = u
            }
        }
    }

    return distances to prev
}