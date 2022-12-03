import java.io.Reader
import java.util.*

fun main() {
    val testInput = """
        A Y
        B X
        C Z
    """.trimIndent()

    check(
        partOne(testInput.reader()) == 15
    )

    val input = file("Day02.txt")

    println(partOne(input.bufferedReader()))

    check(
        partTwo(testInput.reader()) == 12
    )

    println(partTwo(input.bufferedReader()))
}

private fun partOne(reader: Reader): Int {
    var tally = 0
    reader.forEachLine { line ->
        val (opponent, me) = line.split(' ').map {
            RockPaperScissor.fromChar(it[0])
        }
        tally += RockPaperScissor.play(me, opponent)
    }
    return tally
}

private fun partTwo(reader: Reader): Int {
    var tally = 0

    reader.forEachLine { line ->
        val (opponentChar, neededResultChar) = line.split(' ').map { it[0] }
        val opponent = RockPaperScissor.fromChar(opponentChar)
        val me = when (neededResultChar.uppercaseChar()) {
            'X' -> opponent.winsTo
            'Y' -> opponent.drawsTo
            'Z' -> opponent.losesTo
            else -> TODO()
        }
        tally += RockPaperScissor.play(me, opponent)
    }

    return tally
}

enum class RockPaperScissor(val score: Int) {
    ROCK(1), PAPER(2), SCISSORS(3);

    val drawsTo get() = this
    val winsTo get() = when (this) {
        ROCK -> SCISSORS
        PAPER -> ROCK
        SCISSORS -> PAPER
    }
    val losesTo get() = when (this) {
        ROCK -> PAPER
        PAPER -> SCISSORS
        SCISSORS -> ROCK
    }

    companion object {
        fun fromChar(character: Char): RockPaperScissor {
            return when (character.uppercaseChar()) {
                'A', 'X' -> ROCK
                'B', 'Y' -> PAPER
                'C', 'Z' -> SCISSORS
                else -> TODO()
            }
        }

        private val resultLookupTable: Map<RockPaperScissor, Map<RockPaperScissor, Int>> = run {
            val result = mutableMapOf<RockPaperScissor, Map<RockPaperScissor, Int>>()
            result[ROCK] = mapOf(
                ROCK to 3,
                PAPER to 0,
                SCISSORS to 6
            )
            result[PAPER] = mapOf(
                ROCK to 6,
                PAPER to 3,
                SCISSORS to 0
            )
            result[SCISSORS] = mapOf(
                ROCK to 0,
                PAPER to 6,
                SCISSORS to 3
            )
            result
        }

        fun getResult(x: RockPaperScissor, y: RockPaperScissor): Int {
            return resultLookupTable[x]!![y]!!
        }

        fun play(me: RockPaperScissor, opponent: RockPaperScissor): Int {
            return me.score + getResult(me, opponent)
        }
    }
}

