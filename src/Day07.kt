import java.io.Reader

fun main() {
    val testInput = """
        $ cd /
        $ ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        $ cd a
        $ ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        $ cd e
        $ ls
        584 i
        $ cd ..
        $ cd ..
        $ cd d
        $ ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent()

    val testFs = createFS(testInput.reader())

    check(partOne(testFs) == 95437)

    val input = file("Day07.txt")
    val fs = createFS(input.reader())

    println(
        partOne(fs)
    )

    println(partTwo(fs))
}

private fun createFS(source: Reader): Node.Dir {
    val root = Node.Dir("/")
    var currentDir = root
    source.forEachLine { line ->
        if (line.isNotEmpty() && line[0] == '$') {
            // command
            val split = line.split(" ")
            if (split[1] == "cd") {
                val newDir = split[2]
                currentDir = when (newDir) {
                    "/" -> root
                    ".." -> currentDir.parent ?: root
                    else -> {
                        currentDir[newDir] as Node.Dir
                    }
                }
            }
        } else {
            // file list
            val split = line.split(" ")
            if (split[0] == "dir") {
                currentDir.addDir(split[1])
            } else {
                currentDir.addFile(split[1], split[0].toInt())
            }
        }
    }
    return root
}

private fun Node.Dir.flatten(): List<Node.Dir> {
    return this.files.filterIsInstance<Node.Dir>().flatMap { it.flatten() } + this
}

private fun Node.Dir.dirSizes(): List<Int> {
    return this.flatten().map { it.size }
}

private fun partOne(rootfs: Node.Dir): Int {
    val dirs = rootfs.dirSizes()
    val filtered = dirs.filter { it <= 100000 }
    return filtered.fold(0) { acc, dir -> acc + dir }
}

private fun partTwo(rootfs: Node.Dir): Int {
    val nodes = rootfs.dirSizes()
    val totalSize = 70000000
    val needed = 30000000
    val free = totalSize - rootfs.size
    val toFree = needed - free
    return nodes.sorted().filter { it >= toFree }.apply { println(this) } .first()
}

sealed interface Node {
    val name: String
    val size: Int
    val parent: Dir?

    class File(
        override val name: String,
        override val size: Int,
        override val parent: Node.Dir
    ): Node {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as File

            if (name != other.name) return false
            if (parent != other.parent) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + parent.hashCode()
            return result
        }
    }

    class Dir(
        override val name: String,
        override val parent: Node.Dir? = null
    ): Node {
        val files: MutableList<Node> = mutableListOf()
        operator fun get(filename: String) = files.first { it.name == filename }
        fun addFile(filename: String, size: Int) = apply { files.add(File(filename, size, this)) }
        fun addDir(filename: String) = apply { files.add(Dir(filename, this)) }
        operator fun plus(file: Node) = apply { files.add(file) }

        override val size: Int
            get() = files.fold(0) { acc, sizedFile -> acc + sizedFile.size }


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Dir

            if (name != other.name) return false
            if (parent != other.parent) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + (parent?.hashCode() ?: 0)
            return result
        }
    }
}

