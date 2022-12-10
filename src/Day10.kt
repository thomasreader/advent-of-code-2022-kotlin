import java.io.Reader
import java.util.TreeMap

fun main() {
    val testInput = """
        addx 15
        addx -11
        addx 6
        addx -3
        addx 5
        addx -1
        addx -8
        addx 13
        addx 4
        noop
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx -35
        addx 1
        addx 24
        addx -19
        addx 1
        addx 16
        addx -11
        noop
        noop
        addx 21
        addx -15
        noop
        noop
        addx -3
        addx 9
        addx 1
        addx -3
        addx 8
        addx 1
        addx 5
        noop
        noop
        noop
        noop
        noop
        addx -36
        noop
        addx 1
        addx 7
        noop
        noop
        noop
        addx 2
        addx 6
        noop
        noop
        noop
        noop
        noop
        addx 1
        noop
        noop
        addx 7
        addx 1
        noop
        addx -13
        addx 13
        addx 7
        noop
        addx 1
        addx -33
        noop
        noop
        noop
        addx 2
        noop
        noop
        noop
        addx 8
        noop
        addx -1
        addx 2
        addx 1
        noop
        addx 17
        addx -9
        addx 1
        addx 1
        addx -3
        addx 11
        noop
        noop
        addx 1
        noop
        addx 1
        noop
        noop
        addx -13
        addx -19
        addx 1
        addx 3
        addx 26
        addx -30
        addx 12
        addx -1
        addx 3
        addx 1
        noop
        noop
        noop
        addx -9
        addx 18
        addx 1
        addx 2
        noop
        noop
        addx 9
        noop
        noop
        noop
        addx -1
        addx 2
        addx -37
        addx 1
        addx 3
        noop
        addx 15
        addx -21
        addx 22
        addx -6
        addx 1
        noop
        addx 2
        addx 1
        noop
        addx -10
        noop
        noop
        addx 20
        addx 1
        addx 2
        addx 2
        addx -6
        addx -11
        noop
        noop
        noop
    """.trimIndent()

    check(partOne(testInput.reader()) == 13140)

    val input = file("Day10.txt")
    println(partOne(input.bufferedReader()))

    partTwo(testInput.reader()).forEach {
        it.forEach { c -> print(c) }
        println()
    }

    println()

    partTwo(input.bufferedReader()).forEach {
        it.forEach { c -> print(c) }
        println()
    }
}

private fun partTwo(src: Reader): Array<CharArray> {
    val screen = CharArray(240)
    var x = 1
    var cycle = 0

    src.forEachLine { line ->
        if (cycle < 240) {

            val xRange = (x - 1)..(x + 1)
            screen[cycle] = if ((cycle % 40) in xRange) '#' else '.'
            cycle++

            if (line != "noop") {
                screen[cycle] = if ((cycle % 40) in xRange) '#' else '.'
                val deltaX = line.split(" ")[1].toInt()
                cycle++
                x += deltaX
            }
        }
    }

    val screenList = screen.toList()
    println(screenList)
    return Array(6) { row ->
        screenList.subList(40 * row, 40 * row + 40).toCharArray()
    }
}

private fun partOne(src: Reader): Int {
    var x = 1
    var cycle = 1
    val cycleValues = TreeMap<Int, Int>()

    src.forEachLine { line ->
        if (line == "noop") {
            cycle++
        } else {
            val deltaX = line.split(" ")[1].toInt()
            cycle += 2
            x += deltaX
        }
        cycleValues[cycle] = x
    }

    val signalStrength = intArrayOf(20, 60, 100, 140, 180, 220).map {
        it * (cycleValues[it] ?: cycleValues.lowerEntry(it).value)
    }.sum()

    return signalStrength
}