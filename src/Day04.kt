import java.io.Reader

fun main() {
    val testInput = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()

    check(partOne(testInput.reader()) == 2)

    val input = file("Day04.txt")

    println(partOne(input.bufferedReader()))

    check(partTwo(testInput.reader()) == 4)
    println(partTwo(input.bufferedReader()))
}

private fun partOne(source: Reader): Int {
    var fullyCountains = 0

    source.forEachLine { line ->
        val (a, b) = line.split(',')
        val (aMin, aMax) = a.split('-').map { it.toInt() }
        val (bMin, bMax) = b.split('-').map { it.toInt() }
        val aNumbers = (aMin..aMax).toSet()
        val bNumbers = (bMin..bMax).toSet()
        if (aNumbers.containsAll(bNumbers) || bNumbers.containsAll(aNumbers)) {
            fullyCountains++
        }
    }

    return fullyCountains
}

private fun partTwo(source: Reader): Int {
    var overlaps = 0

    source.forEachLine { line ->
        val (a, b) = line.split(',')
        val (aMin, aMax) = a.split('-').map { it.toInt() }
        val (bMin, bMax) = b.split('-').map { it.toInt() }
        val aNumbers = (aMin..aMax).toSet()
        val bNumbers = (bMin..bMax).toSet()
        val intersect = aNumbers.intersect(bNumbers)
        if (intersect.isNotEmpty()) {
            overlaps++
        }
    }

    return overlaps
}