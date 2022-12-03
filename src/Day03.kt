import java.io.Reader

val priorities get() = mutableMapOf<Char, Int>().also { map ->
    ('a'..'z').forEachIndexed { index, c ->
        map[c] = index + 1
    }
    ('A'..'Z').forEachIndexed { index, c ->
        map[c] = index + 27
    }
}.toMap()

fun main() {
    val testInput = """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()

    check(partOne(testInput.reader()) == 157)

    val input = file("Day03.txt")

    println(partOne(input.bufferedReader()))



    check(partTwo(testInput.reader()) == 70)
    println(partTwo(input.bufferedReader()))

}

private fun partOne(source: Reader): Int {
    val priorities = priorities
    var prioritySum = 0

    source.forEachLine { line ->
        val mid = line.length / 2
        val compartment1 = line.substring(0, mid)
        val compartment2 = line.substring(mid)
        check(compartment1.length == compartment2.length)

        prioritySum += compartment1.toSet().intersect(compartment2.toSet()).fold(0) { acc, c ->
            acc + (priorities[c] ?: 0)
        }
    }

    return prioritySum
}

private fun partTwo(source: Reader): Int {
    val priorities = priorities
    var prioritySum = 0
    val twoLineBuffer = ArrayList<String>(2)

    source.forEachLine { line ->
        if (twoLineBuffer.size < 2) {
            twoLineBuffer.add(line)
        } else {
            val a = twoLineBuffer[0].toSet()
            val b = twoLineBuffer[1].toSet()
            val c = line.toSet()
            val intersect = a.intersect(b).intersect(c)
            check(intersect.size == 1)
            val badge = intersect.toList()[0]
            prioritySum += priorities[badge] ?: 0
            twoLineBuffer.clear()
        }
    }

    return prioritySum
}