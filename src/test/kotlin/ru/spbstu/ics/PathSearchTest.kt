package ru.spbstu.ics

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

/**
 * Boolean array: each boolean takes 1 byte (8 bits),
 *                then add array object overhead.
 *
 * R-Tree:        Vertex is an object with 2 ints,
 *                Obstacle is an object with list of 4 Vertices
 *                R-Tree is an object with with all Obstacles and some overhead
 */
object PathSearchTest : Spek({
    describe("A path searching algorithm") {
        describe("searching path with an R-Tree and an array") {
            it("should find a path") {
                val start = Vertex(0, 0)
                println("R-Tree search:\n")
                testStructure { results, size ->
                    val finish = Vertex(size, size)
                    val obstacleTree = ObstacleTree()
                    initTree(size, obstacleTree)
                    test(start, finish) { obstacleTree.intersects(it) }
                        .also { results.add(it) }
                }
                println("Grid search:\n")
                testStructure { results, size ->
                    val finish = Vertex(size, size)
                    val grid = initGrid(size)
                    test(start, finish) { it.x <= size && it.y <= size && grid[it.x][it.y] }
                        .also { results.add(it) }
                }
            }
        }
    }
})

private fun testStructure(f: (MutableList<Long>, Int) -> Unit) {
    for (size in 100..2000 step 100) {
        println("Size: $size")
        val results = mutableListOf<Long>()
        for (i in 0..99) {
            f(results, size)
        }
        val res = results.average()
        println("$res ms")
    }
}

private fun test(start: Vertex, finish: Vertex, isObstacle: (v: Vertex) -> Boolean): Long {
    return measureTimeMillis {
        val path = search(start, finish) { isObstacle(it) }
        assert(path.isNotEmpty())
        assertTrue { path[0] == start }
        assertTrue { path[path.size - 1] == finish }
    }
}

private fun initTree(size: Int, obstacleTree: ObstacleTree) {
    var x = 1
    var y = 1
    generateSequence {
        val obstacle: Obstacle? =
            if (y < size + 9) Obstacle(
                listOf(
                    Vertex(x, y),
                    Vertex(x + 9, y),
                    Vertex(x, y + 9),
                    Vertex(x + 9, y + 9)
                )
            ) else null
        if (x >= size + 9) y += 10
        x = if (x < size + 9) x + 10 else 1
        obstacle
    }.forEach {
        obstacleTree.put(it)
    }
}

private fun initGrid(size: Int): Array<BooleanArray> {
    return Array(size + 1) { row ->
        if (row % 10 == 0) BooleanArray(size + 1)
        else BooleanArray(size + 1) { i ->
            i % 10 != 0
        }
    }
}
