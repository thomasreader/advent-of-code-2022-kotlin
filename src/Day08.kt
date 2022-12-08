import java.util.function.Predicate

fun main() {
    val testInput = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()
        .split("\n")

    val testResult = inputToMatrix(testInput)

    check(partOne(testResult) == 21)

    val input = inputToMatrix(readInput("Day08.txt"))

    println(partOne(input))

    check(partTwo(testResult) == 8)

    println(partTwo(input))
}

fun partOne(intMatrix: IntMatrix): Int {
    val edgeTrees = intMatrix.rows * 2 + intMatrix.columns * 2 - 4
    var interiorTrees = 0
    for (r in 1..intMatrix.rows - 2) {
        for (c in 1..intMatrix.columns - 2) {
            val row = intMatrix.getRow(r).toList()
            val column = intMatrix.getColumn(c).toList()
            val thisHeight = intMatrix[r, c]
            val leftMax = row.subList(0, c).max()
            val rightMax = row.subList(c+1, intMatrix.columns).max()
            val topMax = column.subList(0, r).max()
            val bottomMax = column.subList(r+1, intMatrix.rows).max()
            if (thisHeight > leftMax || thisHeight > rightMax || thisHeight > topMax || thisHeight > bottomMax) {
                interiorTrees++
            }
        }
    }
    return edgeTrees + interiorTrees
}

fun partTwo(intMatrix: IntMatrix): Int {
    var highestScore = 0
    for (r in 0 until intMatrix.rows) {
        for (c in 0 until intMatrix.columns) {
            val row = intMatrix.getRow(r).toList()
            val column = intMatrix.getColumn(c).toList()
            val thisHeight = intMatrix[r, c]
            val left = row.subList(0, c)
            val right = row.subList(c+1, intMatrix.columns)
            val top = column.subList(0, r)
            val bottom = column.subList(r+1, intMatrix.rows)

            val predicate: (Int) -> Boolean = { it >= thisHeight }
            val leftTreesVisible = (left.indexOfLast(predicate)).let {
                if (it == -1) c else c - it
            }
            val rightTreesVisible = (right.indexOfFirst(predicate)).let {
                if (it == -1) intMatrix.columns - c - 1 else it + 1
            }
            val topTreesVisible = (top.indexOfLast(predicate)).let {
                if (it == -1) r else r - it
            }
            val bottomTreesVisible = (bottom.indexOfFirst(predicate)).let {
                if (it == -1) intMatrix.rows - r - 1 else it + 1
            }
            val scenicScore = leftTreesVisible * rightTreesVisible * topTreesVisible * bottomTreesVisible
            if (scenicScore > highestScore) {
                highestScore = scenicScore
            }
        }
    }
    return highestScore
}

class IntMatrix(
    private val internalMatrix: Array<IntArray>
) {
    private val columnMatrix = internalMatrix.getColumns()

    val rows: Int get() = internalMatrix.size
    val columns: Int get() = internalMatrix[0].size

    operator fun set(row: Int, column: Int, value: Int) {
        internalMatrix[row][column] = value
    }
    operator fun get(row: Int, column: Int): Int {
        return internalMatrix[row][column]
    }
    fun getRow(row: Int) = internalMatrix[row]
    fun getColumn(column: Int) = columnMatrix[column]

    private fun Array<IntArray>.getColumns(): Array<IntArray> {
        val numCols = this[0].size
        val numRows = this.size
        return Array(numCols) { col ->
            IntArray(numRows) { row ->
                this[row][col]
            }
        }
    }

    override fun toString(): String {
        return buildString(internalMatrix.size * (internalMatrix[0].size - 1) * 3 + 3) {
            internalMatrix.forEachIndexed { index, row ->
                append(row.contentToString())
                if (index - 1 < internalMatrix.size) {
                    append("\n")
                }
            }
        }
    }
}

//private fun inputToList(src: List<String>): List<List<Int>> = src.map { s ->
//    s.toCharArray().map { c -> c.digitToInt() }
//}

private fun inputToMatrix(src: List<String>): IntMatrix {
    return Array<IntArray>(src.size) { row ->
        val chars = src[row].toCharArray()
        IntArray(chars.size) { cols ->
            chars[cols].digitToInt()
        }
    }.let { IntMatrix(it) }
}