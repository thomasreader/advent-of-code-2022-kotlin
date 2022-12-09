import java.io.Reader
import kotlin.math.abs

data class RopeKnot(var x: Int, var y: Int)
data class IntPair(val x: Int, val y: Int)

infix fun Int.diff(other: Int) = abs((this) - (other))

fun RopeKnot.isAdjacentTo(other: RopeKnot): Boolean {
    return (this.x diff other.x < 2 && this.y diff other.y < 2)
}

fun RopeKnot.toPair() = IntPair(this.x, this.y)

fun main() {
    val testInput = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent()

    val testResult = partOne(testInput.reader())
    println(testResult.size)

    val input = file("Day09.txt")
    println(partOne(input.bufferedReader()).size)

    println(partTwo(input.bufferedReader()).size)
}


private fun partTwo(src: Reader): Set<IntPair> {
    val rope = Array(10) { RopeKnot(0, 0) }
    fun Array<RopeKnot>.head() = this[0]
    fun Array<RopeKnot>.tail() = this[this.size - 1]
    val tailVisited = mutableSetOf(rope.tail().toPair())
    src.forEachLine { line ->
        val (cmd, moves) = line.split(" ")
        repeat(moves.toInt()) { iter ->
            when (cmd) {
                "R" -> rope.head().x++
                "L" -> rope.head().x--
                "U" -> rope.head().y--
                "D" -> rope.head().y++
                else -> TODO()
            }

            for (i in 1 until rope.size) {
                val ahead = rope[i-1]
                val knot = rope[i]
                if (!knot.isAdjacentTo(ahead)) {
                    knot.x += ahead.x.compareTo(knot.x)
                    knot.y += ahead.y.compareTo(knot.y)
                }
            }

            tailVisited += rope.tail().toPair()
        }
    }
    return tailVisited
}

private fun partOne(src: Reader): Set<IntPair> {
    val head = RopeKnot(0, 0)
    val tail = RopeKnot(0, 0)
    val tailVisited = mutableSetOf(tail.toPair())
    src.forEachLine { line ->
        val (cmd, moves) = line.split(" ")
        repeat(moves.toInt()) { iter ->
            when (cmd) {
                "R" -> head.x++
                "L" -> head.x--
                "U" -> head.y--
                "D" -> head.y++
                else -> TODO()
            }

            if (!head.isAdjacentTo(tail)) {
                tail.x += head.x.compareTo(tail.x)
                tail.y += head.y.compareTo(tail.y)
            }

            tailVisited += tail.toPair()
        }
    }
    return tailVisited
}