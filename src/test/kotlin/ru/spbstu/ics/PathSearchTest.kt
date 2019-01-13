package ru.spbstu.ics

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

object PathSearchTest : Spek({
    val obstacleTree = ObstacleTree()
    val obstacleAmount = 2000
    val start = Vertex(0f, 0f)
    val finish = Vertex(2050f, 2050f)
    val pathSearch = PathSearch(obstacleTree)

    describe("A path searching algorithm") {
        measureTimeMillis { initTree(obstacleAmount, obstacleTree) }
            .also {
                println(
                    """
                Tree initialization time: $it ms
            """.trimIndent()
                )
            }
        describe("searching path") {
            it("should find a path") {
                measureTimeMillis {
                    val path = pathSearch.search(start, finish)
                    assert(path.isNotEmpty())
                    assertTrue { path[0] == start }
                    assertTrue { path[path.size - 1] == finish }
                }.also {
                    println(
                        """
                    Search time: $it ms
                    """.trimIndent()
                    )
                }
            }
        }
    }
})

private fun initTree(obstacleAmount: Int, obstacleTree: ObstacleTree) {
    var x = 0
    var y = 0
    generateSequence {
        val obstacle: Obstacle? =
            if (y < obstacleAmount) Obstacle(
                listOf(
                    Vertex(x.toFloat(), y.toFloat()),
                    Vertex(x.toFloat() + 1, y.toFloat()),
                    Vertex(x.toFloat(), y.toFloat() + 1),
                    Vertex(x.toFloat() + 1, y.toFloat() + 1)
                )
            ) else null
        if (x >= obstacleAmount) y += 2
        x = if (x < obstacleAmount) x + 2 else 0
        obstacle
    }.forEach {
        obstacleTree.put(it)
    }
}
