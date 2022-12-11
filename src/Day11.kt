import java.util.*
import java.util.function.IntUnaryOperator
import java.util.function.LongToIntFunction
import java.util.function.LongUnaryOperator
import kotlin.collections.HashMap

fun main() {
    val testInput = """
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
    """.trimIndent()

    check(partOne(createMonkeys(testInput)) == 10605L)

    println(partOne(createMonkeys(readString("Day11.txt"))))

    check(partTwo(createMonkeys(testInput)) == 2713310158L)
    println(partTwo(createMonkeys(readString("Day11.txt"))))
}

private fun partOne(monkeys: Map<Int, Monkey>): Long {
    return performRounds(monkeys, 20) {
        it / 3
    }
}

private fun partTwo(monkeys: Map<Int, Monkey>): Long {
    val lcd = getLCD(monkeys.values)
    return performRounds(monkeys, 10_000) {
        it % lcd
    }
}

private fun performRounds(monkeys: Map<Int, Monkey>, rounds: Int, worryHandler: LongUnaryOperator): Long {
    for (round in 1..rounds) {
        monkeys.forEach {
            val (_, monkey) = it
            val moves = monkey.inspect(worryHandler)
            moves.forEach { item ->
                monkeys[item.toMonkeyId]!! + item.worryLevel
            }
        }
    }
    println(monkeys.values.map(Monkey::inspectedItems))
    val (testHigh1, testHigh2) = monkeys.values.map { it.inspectedItems }.sortedDescending()
    return testHigh1.toLong() * testHigh2.toLong()
}

private fun getLCD(monkeys: Collection<Monkey>): Long {
    return monkeys
        .map(Monkey::divisor)
        .reduce { acc, l ->
            acc * l
        }
}

private fun createMonkeys(src: String): SortedMap<Int, Monkey> {
    val monkeyMap = sortedMapOf<Int, Monkey>()
    val monkeys = src.split("\n\n")
    monkeys.forEach { m ->
        val split = m.split("\n")
        val id = split[0].filter { it.isDigit() }[0].digitToInt()
        val startingItems = split[1]
            .replace("  Starting items: ", "")
            .replace(" ", "")
            .split(',')
            .map(String::toLong)

        val splitOperation = split[2].replace("  Operation: new = ", "").split(' ')
        val a = splitOperation[0].toLongOrNull()
        val b: Long.(Long) -> Long = when (splitOperation[1]) {
            "*" -> Long::times
            "+" -> Long::plus
            "-" -> Long::minus
            "/" -> Long::div
            else -> TODO()
        }
        val c = splitOperation[2].toLongOrNull()
        val operation = LongUnaryOperator {
            b(a ?: it, c ?: it)
        }
        val divisibleBy = split[3].split(' ').last().toLong()
        val trueThrowTo = split[4].last().digitToInt()
        val falseThrowTo = split[5].last().digitToInt()

        val monkey = Monkey(
            id = id,
            operation = operation,
            divisor = divisibleBy,
            ifDividableId = trueThrowTo,
            ifNotDividableId = falseThrowTo
        )

        startingItems.forEach {
            monkey + it
        }

        monkeyMap[monkey.id] = monkey
    }
    return monkeyMap
}

data class ItemThrown(
    val worryLevel: Long,
    val toMonkeyId: Int
)

class Monkey(
    val id: Int,
    private val operation: LongUnaryOperator,
    val divisor: Long,
    private val ifDividableId: Int,
    private val ifNotDividableId: Int
) {
    var inspectedItems = 0
        private set
    val items: Queue<Long> = LinkedList()

    operator fun plus(item: Long) = apply { items.offer(item) }

    fun inspect(worryHandler: LongUnaryOperator): List<ItemThrown> {
        val result = items.map { worryLevel ->
            inspectedItems++
            val newWorryLevel = operation.andThen(worryHandler).applyAsLong(worryLevel)
            val monkeyId = if (newWorryLevel % divisor == 0L) ifDividableId else ifNotDividableId
            ItemThrown(
                worryLevel = newWorryLevel,
                toMonkeyId = monkeyId
            )
        }
        items.clear()
        return result
    }

    override fun toString(): String {
        return "Monkey(id=$id, inspected=$inspectedItems, items=$items)"
    }
}