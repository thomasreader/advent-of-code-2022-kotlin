import java.util.LinkedList
import java.util.Queue

fun main() {
    val testInputs = listOf<String>(
        "bvwbjplbgvbhsrlpgdmjqwftvncz",
        "nppdvjthqldpwncqszvftbrmjlhg",
        "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg",
        "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"
    )

    val testOutputs = testInputs.map { partOne(it) }
    check(testOutputs == listOf(5, 6, 10, 11))

    val input: String = readInput("Day06.txt")[0]
    println(partOne(input))

    val testOutputs2 = testInputs.map { partTwo(it) }
    check(testOutputs2 == listOf(23, 23, 29, 26))

    println(partTwo(input))
}

private fun partX(input: String, bufferSize: Int): Int {
    val previousChars = SizedQueue<Char>(bufferSize)
    input.forEachIndexed { index, c ->
        previousChars.offer(c)
        if (previousChars.isMaxSize) {
            if (previousChars.hasNoDuplicates()) {
                return index + 1
            }
        }
    }
    return -1
}

private fun partOne(input: String): Int = partX(input, 4)
private fun partTwo(input: String): Int = partX(input, 14)

fun <E> Queue<E>.hasNoDuplicates() = this.toSet().size == this.size

data class SizedQueue<E> private constructor(
    val maxSize: Int,
    private val linkedList: LinkedList<E>
): Queue<E> by linkedList {
    constructor(maxSize: Int): this(maxSize, LinkedList())

    init {
        assert(maxSize > 0)
    }

    override fun offer(element: E): Boolean {
        return if (linkedList.size < maxSize) {
            linkedList.offer(element)
        } else {
            linkedList.poll()
            this.offer(element)
        }
    }

    val isMaxSize get() = linkedList.size == maxSize

    override fun add(element: E): Boolean {
        return this.offer(element)
    }
}