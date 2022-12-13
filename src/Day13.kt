import kotlin.math.max

fun main() {
    val testInput = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent()

    generatePackets(testInput).foldIndexed(0) { index, acc, packagePair ->
        if (packagePair.isRightOrder) acc + (index + 1) else acc
    }.let { check(it == 13) }

    val input = readString("Day13.txt")
    generatePackets(input).foldIndexed(0) { index, acc, packagePair ->
        if (packagePair.isRightOrder) acc + (index + 1) else acc
    }.let { println(it) }

    val dividerPacket1 = PackageData.Arr(listOf(PackageData.Arr(listOf(PackageData.Integer(2)))))
    val dividerPacket2 = PackageData.Arr(listOf(PackageData.Arr(listOf(PackageData.Integer(6)))))

    val testGenerated = generatePackets(testInput)
    val testAllPackets = ArrayList<PackageData.Arr>(testGenerated.size * 2 + 2)
    testAllPackets.add(dividerPacket1)
    testAllPackets.add(dividerPacket2)
    testGenerated.forEach {
        testAllPackets.add(it.left)
        testAllPackets.add(it.right)
    }
    val testSorted = testAllPackets.sorted()
    val testPacket1Ix = testSorted.indexOfFirst { it == dividerPacket1 } + 1
    val testPacket2Ix = testSorted.indexOfFirst { it == dividerPacket2 } + 1
    check(testPacket1Ix * testPacket2Ix == 140)


    val generated = generatePackets(input)
    val allPackets = ArrayList<PackageData.Arr>(generated.size * 2 + 2)
    allPackets.add(dividerPacket1)
    allPackets.add(dividerPacket2)
    generated.forEach {
        allPackets.add(it.left)
        allPackets.add(it.right)
    }
    val sorted = allPackets.sorted()
    val packet1Ix = sorted.indexOfFirst { it == dividerPacket1 } + 1
    val packet2Ix = sorted.indexOfFirst { it == dividerPacket2 } + 1
    println(packet1Ix * packet2Ix)
}

private fun generatePackets(src: String): List<PackagePair> {
    return src.split("\n\n").map { packets ->
        val (left, right) = packets.split("\n")
        // remove first and last brackets
        val leftArr = buildArr(StringIterator(left.substring(1, left.length - 1)))
        val rightArr = buildArr(StringIterator(right.substring(1, right.length - 1)))

        PackagePair(
            left = leftArr,
            right = rightArr
        )
    }
}

private fun buildArr(iterator: StringIterator): PackageData.Arr {
    val arr = mutableListOf<PackageData>()
    val charRangeCheck = '0'..'9'
    while (iterator.hasNext()) {
        when (val c = iterator.nextChar()) {
            '[' -> arr.add(buildArr(iterator))
            ']' -> break
            ',' -> Unit
            in charRangeCheck -> {
                val peek = iterator.peekNextChar()
                val intBuilder = StringBuilder(1).append(c.digitToInt())
                if (peek != null && peek in charRangeCheck) {
                    val next = iterator.nextChar()
                    intBuilder.append(next.digitToInt())
                }
                arr.add(PackageData.Integer(intBuilder.toString().toInt()))
            }
        }
    }
    return PackageData.Arr(arr)
}

class StringIterator(
    s: String
): CharIterator() {
    val array = s.toCharArray()
    var index = 0
    override fun hasNext() = index < array.size
    override fun nextChar() = try {
        array[index++]
    } catch (e: ArrayIndexOutOfBoundsException) {
        index -= 1; throw NoSuchElementException(e.message)
    }
    fun peekNextChar(): Char? = array.getOrNull(index)
}

sealed interface PackageData: Comparable<PackageData> {
    data class Integer(val int: Int): PackageData, Comparable<PackageData> {
        override fun compareTo(other: PackageData): Int {
            return when (other) {
                is Arr -> this.toArr().compareTo(other)
                is Integer -> this.int.compareTo(other.int)
            }
        }

        override fun toString(): String {
            return int.toString()
        }
    }
    data class Arr(val packageData: List<PackageData>): PackageData, Comparable<PackageData> {
        private fun compare(t: Arr, other: Arr): Int {
            val maxIx = max(t.packageData.lastIndex, other.packageData.lastIndex)
            for (i in 0..maxIx) {
                // t is smaller therefore right order
                if (t.packageData.lastIndex < i) {
                    return -1
                } else if (other.packageData.lastIndex < i) {
                    // t is bigger therefore wrong order
                    return 1
                } else {
                    val thisI = t.packageData[i]
                    val otherI = other.packageData[i]
                    if (thisI is Integer && otherI is Integer) {
                        // both ints so can compare. If not equal then we can finish early
                        val comparison = thisI.compareTo(otherI)
                        if (comparison != 0) {
                            return comparison
                        }
                    } else {
                        // mixed types so convert to arrays and compare them
                        val iArr = thisI.toArr()
                        val oArr = otherI.toArr()
                        val comparison = iArr.compareTo(oArr)
                        if (comparison != 0) {
                            return comparison
                        }
                    }
                }
            }

            // fallback return 0...
            return 0
        }
        
        override fun compareTo(other: PackageData): Int {
            return when (other) {
                is Arr -> compare(this, other)
                is Integer -> compare(this, other.toArr())
            }
        }

        override fun toString(): String {
            return this.packageData.toString()
        }
    }
}

fun PackageData.toArr(): PackageData.Arr = when (this) {
    is PackageData.Arr -> this
    is PackageData.Integer -> PackageData.Arr(listOf(this))
}

data class PackagePair(
    val left: PackageData.Arr,
    val right: PackageData.Arr
) {
    val isRightOrder get() = left < right

    override fun toString(): String {
        return "${left}, $right"
    }
}