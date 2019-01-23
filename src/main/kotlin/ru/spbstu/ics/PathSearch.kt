package ru.spbstu.ics

import java.util.PriorityQueue
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

fun search(start: Vertex, finish: Vertex, isObstacle: (v: Vertex) -> Boolean): List<Vertex> {
    val queue = PriorityQueue<Node>(compareBy { node ->
        node.cost + node.prevCost
    })
    queue.add(Node(start, computeCost(start, finish)))
    val visited = mutableSetOf<Vertex>()

    var currentNode: Node
    do {
        currentNode = queue.remove()
        visited.add(currentNode.vertex)
        neighbors(currentNode.vertex)
            .asSequence()
            .map {
                Node(
                    it,
                    computeCost(it, finish),
                    currentNode.cost,
                    currentNode
                )
            }
            .filter { !visited.contains(it.vertex) }
            .filter { !isObstacle(it.vertex) }
            .also { queue.addAll(it) }
    } while (currentNode.vertex != finish)
    return currentNode.route()
}

private fun neighbors(vertex: Vertex): List<Vertex> {
    val yList = (vertex.y - 1..vertex.y + 1)
        .toList()
    val xList = (vertex.x - 1..vertex.x + 1)
        .toList()
    return yList.flatMap { y ->
        xList.map { Vertex(it, y) }
    }
        .filter { it.x >= 0 && it.y >= 0 }
}

private data class Node(
    val vertex: Vertex,
    val cost: Float,
    val prevCost: Float = 0f,
    val prev: Node? = null
)

private fun Node.route(): List<Vertex> {
    val path = Stack<Vertex>()
    var currentNode: Node? = this
    while (currentNode != null) {
        path.add(currentNode.vertex)
        currentNode = currentNode.prev
    }
    return path.asReversed()
}

private fun computeCost(start: Vertex, finish: Vertex): Float {
    return sqrt((start.y - finish.y).toFloat().pow(2) + (start.x - finish.x).toFloat().pow(2))
}