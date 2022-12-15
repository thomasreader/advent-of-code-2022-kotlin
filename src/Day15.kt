import java.io.Reader
import kotlin.math.abs

fun main() {
    val testInput = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent()

    val parsedTest = parseInput(testInput.reader())

    check(partOne(parsedTest, 10) == 26)
    val testPartTwo = partTwo(parsedTest, 20)
    check(testPartTwo.tuningFrequency == 56000011L)


    val parsed = parseInput(reader("Day15.txt"))
    println(partOne(parsed, 2000000))

    val partTwo = partTwo(parsed, 4000000)
    //println(partTwo)
    println(partTwo.tuningFrequency)
}

val TunnelCoordinates.tuningFrequency get() = this.x * 4000000L + this.y

private fun partTwo(sensorBeacons: List<SensorBeacon>, maxXY: Int): TunnelCoordinates {
    for (y in 0..maxXY) {
        val ranges = sensorBeacons.mapNotNull { it.xRangeCovered(y) }.sortedBy { it.first }
        var x = 0
        for (i in 0..ranges.lastIndex) {
            val range = ranges[i]
            if (x in range) {
                x = range.last + 1
            }
        }
        if (x < maxXY) {
            return(TunnelCoordinates(x, y))
        }
    }
    TODO()
}


private fun partOne(sensorBeacons: List<SensorBeacon>, y: Int): Int {
    val empties = mutableSetOf<TunnelCoordinates>()
    sensorBeacons.forEach { sb ->
        val (sensor, beacon) = sb
        empties.addAll(sensor.getEmptyBeacons(beacon, y))
    }

    return empties.size
}

private fun parseInput(src: Reader): List<SensorBeacon> {
    val out = mutableListOf<SensorBeacon>()
    val beacons = mutableMapOf<TunnelCoordinates, TunnelCoordinates>()

    val coordRegex = Regex("""-?\d+""")
    src.forEachLine { line ->
        val (sensorX, sensorY, beaconX, beaconY) = coordRegex.findAll(line).toList().map { it.value.toInt() }
        val sensor = TunnelCoordinates(sensorX, sensorY)
        val beacon = TunnelCoordinates(beaconX, beaconY).let {
            beacons[it] ?: it.also { beacons[it] = it }
        }
        out += SensorBeacon(sensor, beacon)
    }

    return out
}

data class SensorBeacon(
    val sensor: TunnelCoordinates,
    val nearestBeacon: TunnelCoordinates
) {
    private val beaconDistance = sensor.manhattanDistance(nearestBeacon)

    fun xRangeCovered(y: Int): IntRange? {
        val yDist = abs(sensor.y - y)
        if (yDist > beaconDistance) return null
        val xDelta = abs(beaconDistance - yDist)
        val min = sensor.x - xDelta
        val max = sensor.x + xDelta
        return min..max
    }
}

data class TunnelCoordinates(
    val x: Int,
    val y: Int
)

fun TunnelCoordinates.manhattanDistance(other: TunnelCoordinates): Int {
    return abs(this.x - other.x) + abs(this.y - other.y)
}

fun TunnelCoordinates.getEmptyBeacons(beacon: TunnelCoordinates, y: Int): Set<TunnelCoordinates> {
    val distance = this.manhattanDistance(beacon)
    val empties = mutableSetOf<TunnelCoordinates>()

    if (y in this.y-distance..this.y+distance) {
        val yDist = y - this.y
        val plusMinus = distance - abs(yDist)
        val x1 = this.x - plusMinus
        val x2 = this.x + plusMinus

        for (x in x1..x2) {
            empties += TunnelCoordinates(x, y)
        }
    }
    empties -= this
    empties -= beacon
    return empties
}