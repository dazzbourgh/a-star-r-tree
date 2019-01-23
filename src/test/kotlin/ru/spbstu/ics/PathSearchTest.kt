package ru.spbstu.ics

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.system.measureNanoTime
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
                testStructure(100) { size ->
                    val finish = Vertex(size, size)
                    val obstacleTree = ObstacleTree()
                    val initTime = measureNanoTime { initTree(size, obstacleTree) }
                    val execTime = test(start, finish) { obstacleTree.intersects(it) }
                    Pair(initTime, execTime)

                }
                println("Grid search:\n")
                testStructure(100) { size ->
                    val finish = Vertex(size, size)
                    var grid: Array<BooleanArray>? = null
                    val initTime = measureNanoTime { grid = initGrid(size) }
                    val execTime = test(start, finish) { it.x <= size && it.y <= size && !grid!![it.x][it.y] }
                    Pair(initTime, execTime)
                }
            }
        }
    }
})

private fun testStructure(
    initialSize: Int,
    testFun: (Int) -> Pair<Long, Long>
) {
    for (size in 0..initialSize * 100 step initialSize * 10) {
        if (size == 0) continue
        println("Size: $size")
        val results = mutableListOf<Pair<Long, Long>>()
        for (i in 0 until 50) {
            testFun(size).also { results.add(it) }
        }
        val res = results.average()
        println("Init Time: ${res.first / 1_000_000f} ms")
        println("Exec Time: ${res.second / 1_000_000f} ms")
        println()
    }
}

private fun test(start: Vertex, finish: Vertex, isObstacle: (v: Vertex) -> Boolean): Long {
    return measureNanoTime {
        val path = search(start, finish) { isObstacle(it) }
        assert(path.isNotEmpty())
        assertTrue { path[0] == start }
        assertTrue { path[path.size - 1] == finish }
    }
}

private fun initTree(size: Int, obstacleTree: ObstacleTree) {
    generateObstacles(size).forEach {
        obstacleTree.put(it)
    }
}

private fun initGrid(size: Int): Array<BooleanArray> {
    return Array(size + 1) {
        BooleanArray(size + 1) { true }
    }.also { arr ->
        generateObstacles(size).forEach {
            addObstacle(it, arr)
        }
    }
}

private fun addObstacle(obstacle: Obstacle, arr: Array<BooleanArray>) {
    obstacle.vertexes
        .sortedBy { it.x }
        .sortedBy { it.y }
        .also {
            for (i in it[0].x..it[3].x) {
                for (j in it[0].y..it[3].y) {
                    arr[i][j] = false
                }
            }
        }
}

private fun generateObstacles(size: Int): Sequence<Obstacle> {
    var x = 1
    var y = 1
    val obstacleSize: Int = size / 10 - 1
    return generateSequence {
        val obstacle: Obstacle? =
            if (y < size) Obstacle(
                listOf(
                    Vertex(x, y),
                    Vertex(x + obstacleSize - 1, y),
                    Vertex(x, y + obstacleSize - 1),
                    Vertex(x + obstacleSize - 1, y + obstacleSize - 1)
                )
            ) else null
        if (x >= size - obstacleSize) y += obstacleSize + 1
        x = if (x < size - obstacleSize) x + obstacleSize + 1 else 1
        obstacle
    }
}

private fun List<Pair<Long, Long>>.average(): Pair<Long, Long> {
    var count = 0
    var first = 0L
    var second = 0L
    for (i in 0 until this.size) {
        first += this[i].first
        second += this[i].second
        ++count
    }
    return Pair(first / count, second / count)
}