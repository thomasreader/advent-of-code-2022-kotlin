import java.io.Reader

typealias ValveToValveDistance = Map<Valve, Map<Valve, Int>>
typealias MutableValveToValveDistance = MutableMap<Valve, Map<Valve, Int>>

fun main() {
    val testInput = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent()

    val testTunnels = parseInput(testInput.reader()).associateBy { it.id }
    val testDistances = djikstra(testTunnels)
    val testBestFlow = getBestFlow(
        currentValve = "AA",
        valveMap = testTunnels,
        distances = testDistances,
        openedValves = emptySet(),
        minutesLeft = 30
    )
    check(testBestFlow == 1651)

    val input = reader("Day16.txt")
    val tunnels = parseInput(input).associateBy { it.id }
    val distances = djikstra(tunnels)
    val bestFlow = getBestFlow(
        currentValve = "AA",
        valveMap = tunnels,
        distances = distances,
        openedValves = emptySet(),
        minutesLeft = 30
    )
    println(bestFlow)

    val testFlowElly = getBestFlowWithElly(
        myPosition = "AA",
        elephantPosition = "AA",
        valveMap = testTunnels,
        distances = testDistances,
        openedValves = emptySet(),
        myTimeLeft = 26,
        elephantTimeLeft = 26
    )
    check(testFlowElly == 1707)

    val flowElly = getBestFlowWithElly(
        myPosition = "AA",
        elephantPosition = "AA",
        valveMap = tunnels,
        distances = distances,
        openedValves = emptySet(),
        myTimeLeft = 26,
        elephantTimeLeft = 26
    )
    println(flowElly)
}

fun getBestFlowWithElly(
    myPosition: String,
    elephantPosition: String,
    valveMap: Map<String, Valve>,
    distances: ValveToValveDistance,
    openedValves: Set<Valve>,
    myTimeLeft: Int,
    elephantTimeLeft: Int
): Int {
    val possiblePaths = distances[valveMap[myPosition]!!]!!.filterKeys { it !in openedValves }
    val elephantPaths = distances[valveMap[elephantPosition]!!]!!.filterKeys { it !in openedValves }
    return possiblePaths.maxOfOrNull { (to, travelTime) ->
        val travelTimeAndOpeningTime = travelTime + 1
        if (myTimeLeft > 2 && myTimeLeft > travelTimeAndOpeningTime) {
            val myPressureGained = (myTimeLeft - travelTimeAndOpeningTime) * to.flowRate

            myPressureGained + (elephantPaths.maxOfOrNull { (elephantTo, elephantTravelTime) ->
                val eTravelTimeAndOpeningTime = elephantTravelTime + 1
                if (elephantTimeLeft > 2 && elephantTimeLeft > eTravelTimeAndOpeningTime) {
                    val ePressureGained = if (to == elephantTo) 0 else (elephantTimeLeft - eTravelTimeAndOpeningTime) * elephantTo.flowRate
                    ePressureGained + getBestFlowWithElly(
                        myPosition = to.id,
                        elephantPosition = elephantTo.id,
                        valveMap = valveMap,
                        distances = distances,
                        openedValves = openedValves + arrayOf(to, elephantTo),
                        myTimeLeft = myTimeLeft - travelTimeAndOpeningTime,
                        elephantTimeLeft = elephantTimeLeft - eTravelTimeAndOpeningTime
                    )
                } else { 0 }
            } ?: 0)

        } else { 0 }
    } ?: 0
}

fun getBestFlow(
    currentValve: String,
    valveMap: Map<String, Valve>,
    distances: ValveToValveDistance,
    openedValves: Set<Valve>,
    minutesLeft: Int
): Int {
    return when (minutesLeft) {
        0, 1, 2 -> 0
        else -> {
            val possiblePaths = distances[valveMap[currentValve]!!]!!.filterKeys { it !in openedValves }
            possiblePaths.maxOfOrNull { (to, travelTime) ->
                val travelTimeAndOpeningTime = travelTime + 1
                if (travelTimeAndOpeningTime >= minutesLeft) {
                    return@maxOfOrNull 0
                }

                val valveOpenFor = minutesLeft - travelTimeAndOpeningTime
                val pressureGained = (valveOpenFor) * to.flowRate

                pressureGained + getBestFlow(
                    currentValve = to.id,
                    valveMap = valveMap,
                    distances = distances,
                    openedValves = openedValves + to,
                    minutesLeft = valveOpenFor
                )
            } ?: 0
        }
    }
}

private fun parseInput(src: Reader): List<Valve> {
    val valves = mutableListOf<Valve>()
    val digitRegex = Regex("""\d+""")
    val idRegex = Regex("""[A-Z]{2}""")
    src.forEachLine { line ->
        val (a, b) = line.split("; ")
        val aSplit = a.split(' ')
        val id = aSplit[1]
        val flowRate = digitRegex.find(aSplit[4])!!.value.toInt()
        val toValves = b.split(' ').let {
            it.subList(4, it.size)
                .map { i -> idRegex.find(i)!!.value }
        }

        valves += Valve(id, flowRate, toValves)
    }
    return valves
}

fun ValveToValveDistance.prettyPrint() {
    forEach { fromEntry ->
        fromEntry.value.forEach { toEntry ->
            println("${fromEntry.key.id} -> ${toEntry.key.id} (${toEntry.value})")
        }
    }
}

data class Valve(
    val id: String,
    val flowRate: Int,
    val tunnelsTo: List<String>
)

private fun djikstra(graph: Map<String, Valve>): ValveToValveDistance {
    val valveToValveDistances: MutableValveToValveDistance = mutableMapOf()
    graph.forEach { entry ->
        val distances: MutableMap<Valve, Int> = graph.values.associateWith { Int.MAX_VALUE }.toMutableMap()
        val prev: MutableMap<Valve, Valve?> = graph.values.associateWith { null }.toMutableMap()
        distances[entry.value] = 0
        val queue = ArrayList(graph.values)
        val seen = mutableListOf<Valve>()

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

            u.tunnelsTo.forEach { vId ->
                val v = graph[vId]
                if (v!= null && distances[v]!! > distances[u]!! + 1) {
                    distances[v] = distances[u]!! + 1
                    prev[v] = u
                }
            }
        }
        valveToValveDistances[entry.value] = distances.filter { it.key.flowRate > 0 && it.key != entry.value }
    }


    return valveToValveDistances.filter { fromEntry ->
        fromEntry.key.id == "AA" || fromEntry.key.flowRate > 0
    }
}