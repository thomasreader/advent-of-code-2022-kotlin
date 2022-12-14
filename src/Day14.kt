import java.io.Reader
import java.util.SortedMap
import java.util.SortedSet

fun main() {
    val testInput = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent()

    val testRocks = parseInput(testInput.reader())

    check(partOne(testRocks) == 24)

    val input = reader("Day14.txt")
    val rocks = parseInput(input)

    println(partOne(rocks))

    check(partTwo(testRocks) == 93)

    println(partTwo(rocks))
}

private fun partTwo(rocks: Set<Coordinate>): Int {
    val bottomY = rocks.maxBy { it.y }
    val floor = bottomY.y + 2
    val rocksAndSand = rocks.toMutableSet()

    while (Coordinate(500, 0) !in rocksAndSand) {
        var sandUnit = Coordinate(500, 0)

        var atRest = false
        while (!atRest) {
            val nextDrop = sandUnit.y + 1
            if (nextDrop == floor) {
                atRest = true
                rocksAndSand += sandUnit
                continue
            }

            val down = sandUnit.down()
            when (down !in rocksAndSand) {
                true -> sandUnit = down

                false -> {
                    val downLeft = sandUnit.downLeft()
                    when (downLeft !in rocksAndSand) {
                        true -> sandUnit = downLeft

                        false -> {
                            val downRight = sandUnit.downRight()
                            when (downRight !in rocksAndSand) {
                                true -> sandUnit = downRight

                                false -> {
                                    atRest = true
                                    rocksAndSand += sandUnit
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    return rocksAndSand.size - rocks.size
}

private fun partOne(rocks: Set<Coordinate>): Int {
    val floor = rocks.maxBy { it.y }
    val rocksAndSand = rocks.toMutableSet()

    var intoEndlessVoid = false
    while (!intoEndlessVoid) {
        var sandUnit = Coordinate(500, 0)

        var atRest = false
        while (!atRest && !intoEndlessVoid) {
            val down = sandUnit.down()
            when (down !in rocksAndSand) {
                true -> sandUnit = down

                false -> {
                    val downLeft = sandUnit.downLeft()
                    when (downLeft !in rocksAndSand) {
                        true -> sandUnit = downLeft

                        false -> {
                            val downRight = sandUnit.downRight()
                            when (downRight !in rocksAndSand) {
                                true -> sandUnit = downRight

                                false -> {
                                    atRest = true
                                    rocksAndSand += sandUnit
                                }
                            }
                        }
                    }
                }
            }
            if (sandUnit lowerThan floor) {
                intoEndlessVoid = true
            }
        }
    }

    return rocksAndSand.size - rocks.size
}

private fun parseInput(src: Reader): Set<Coordinate> {
    val rocks = mutableSetOf<Coordinate>()

    src.forEachLine { line ->
        val coords = line
            .split(" -> ")
            .map { c ->
                val (x, y) = c.split(",").map(String::toInt)
                Coordinate(x, y)
            }

        // should be added anyway
        rocks.addAll(coords)

        for (i in 0 until coords.lastIndex) {
            val first = coords[i]
            val second = coords[i + 1]

            rocks += first.linesBetween(second)
        }
    }

    return rocks
}

data class Coordinate(
    val x: Int,
    val y: Int
): Comparable<Coordinate> {
    override fun compareTo(other: Coordinate): Int {
        return if (this.x == other.x) {
            this.y.compareTo(other.y)
        } else this.x.compareTo(other.x)
    }

    infix fun lowerThan(other: Coordinate) = this.y > other.y
    //infix fun xlt(other: Coordinate) = this.x < other.x

    fun down() = Coordinate(x, y + 1)
    fun downLeft() = Coordinate(x - 1, y + 1)
    fun downRight() = Coordinate(x + 1, y + 1)
}

fun Coordinate.linesBetween(other: Coordinate): Set<Coordinate> {
    val line = mutableSetOf<Coordinate>()
    var x = this.x
    var y = this.y

    line += this
    line += other

    while (x != other.x) {
        line.add(Coordinate(x, y))
        x -= x.compareTo(other.x)
    }
    while (y != other.y) {
        line.add(Coordinate(x, y))
        y -= y.compareTo(other.y)
    }
    return line
}

class Cave(
    val rocks: SortedMap<Int, SortedSet<Int>>
) {
    val sand: SortedMap<Int, SortedSet<Int>> = sortedMapOf()
}