import java.io.Reader
import java.util.*

fun main() {
    val testInput = """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 

        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent()

    val testResultStacks = partOne(testInput.reader())
    check(testResultStacks.getTops() == "CMZ")

    val input = file("Day05.txt")

    println(
        partOne(input.bufferedReader()).getTops()
    )

    check(
        partTwo(testInput.reader()).getTops() == "MCD"
    )

    println(
        partTwo(input.bufferedReader()).getTops()
    )
}

private fun List<Stack<Char>>.getTops(): String = buildString(this.size) {
    this@getTops.forEach { stack ->
        stack.peek()?.let { append(it) }
    }
}

private fun List<Stack<Char>>.applyCraneOperation(fromStackIx: Int, toStackIx: Int, amount: Int, isModel9001: Boolean) {
    val fromStack = this[fromStackIx]
    val toStack = this[toStackIx]

    if (fromStack.isNotEmpty()) {
        val moving = mutableListOf<Char>()
        for (i in (0 until amount)) {
            moving.add(fromStack.pop())
        }
        val movingChecked = if (isModel9001) moving.reversed() else moving
        movingChecked.forEach {
            toStack.push(it)
        }
    }
}

private fun getStacks(source: Reader, isModel9001: Boolean): List<Stack<Char>> {
    val startingValues = mutableListOf<String>()
    val stacks = mutableListOf<Stack<Char>>()
    var isStackInit = false
    fun MutableList<Stack<Char>>.init(startingValues: List<String>): MutableList<Stack<Char>> {
        val rowNumbersIx: Int = startingValues.indexOfLast { row ->
            row.isNotBlank() || row.replace(" ", "").toBigIntegerOrNull() != null
        }

        val colIxs = startingValues[rowNumbersIx]
            .toCharArray()
            .mapIndexed { index, c ->
                if (c.digitToIntOrNull() != null) index else null
            }
            .filterNotNull()

        val numCols = colIxs.size//startingValues[rowNumbersIx].findLast { it.isDigit() }!!.digitToInt()
        for (i in 0 until numCols) {
            this.add(Stack())
        }

        val values = startingValues.subList(0, rowNumbersIx).reversed()
        values.forEachIndexed { rowIx, row ->
            colIxs.forEachIndexed { index, columnIx ->
                if (columnIx < row.length) {
                    val v = row[columnIx]
                    if (v.isLetter()) {
                        this[index].push(row[columnIx])
                    }
                }
            }
        }

//        val values = startingValues.subList(0, rowNumbersIx).map { s ->
//            s
//                .replace(Regex(""" {3}|\] \["""), ",")
//                .replace(Regex("""\[|\]| +"""), "")
//                .split(",")
//                .map { if (it.isBlank()) null else it[0] }
//        }
//
//        values.reversed().forEach { row ->
//            row.forEachIndexed { index, c ->
//                if (c != null) {
//                    this[index].push(c)
//                }
//            }
//        }
        return this
    }

    source.forEachLine { line ->
        if (line.startsWith("move")) {
            if (!isStackInit) {
                stacks.init(startingValues)
                isStackInit = true
            }
            line.split(' ').also { stringList ->
                stacks.applyCraneOperation(
                    fromStackIx = stringList[3].toInt() - 1,
                    toStackIx = stringList[5].toInt() - 1,
                    amount = stringList[1].toInt(),
                    isModel9001 = isModel9001
                )
            }

        } else {
            startingValues.add(line)
        }
    }
    return stacks
}

private fun partOne(source: Reader) = getStacks(source, false)

private fun partTwo(source: Reader) = getStacks(source, true)