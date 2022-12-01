import java.io.Reader
import java.util.SortedSet

fun main() {
    val testInput = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent()

    check(
        partOne(countCalories(testInput.reader())) == 24000
    )

    val input = reader("Day01")
    println(partOne(countCalories(input)))

    check(
        partTwo(countCalories(testInput.reader())) == 45000
    )

    println(partTwo(countCalories(reader("Day01"))))
}

fun partOne(elves: SortedSet<Elf>): Int {
    return elves.first().calories
}

fun partTwo(elves: SortedSet<Elf>): Int {
    return elves
        .take(3)
        .fold(0) { value, elf -> value + elf.calories }
}

data class Elf(
    val number: Int,
    val calories: Int
): Comparable<Elf> {
    override fun compareTo(other: Elf): Int {
        return this.calories.compareTo(other.calories)
    }

    class Builder(val number: Int) {
        var calories: Int = 0

        operator fun plusAssign(moreCalories: Int) {
            this.calories += moreCalories
        }

        fun build() = Elf(this.number, this.calories)
    }
}

fun countCalories(source: Reader): SortedSet<Elf> {
    val result: SortedSet<Elf> = sortedSetOf<Elf>().descendingSet()

    var currentElf = Elf.Builder(1)

    source.forEachLine { line ->
        if (line.isBlank()) {
            val elf = currentElf.build()
            currentElf = Elf.Builder(elf.number + 1)
            result.add(elf)
        } else {
            val calories = line.toInt()
            currentElf += calories
        }
    }
    result.add(currentElf.build())

    return result
}

