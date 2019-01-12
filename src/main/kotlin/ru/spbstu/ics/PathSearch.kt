package ru.spbstu.ics

import java.util.PriorityQueue
import java.util.Stack
import kotlin.math.sqrt

class PathSearch(private val obstacleTree: ObstacleTree) {
    fun search(start: Vertex, finish: Vertex): List<Vertex> {
        val queue = PriorityQueue<Node>(compareBy { node ->
            sqrt(node.vertex.y * node.vertex.y + node.vertex.x * node.vertex.x)
        })
        val visited = mutableSetOf<Node>()

        neighbors(start)
            .map { Node(it, Node(start, null)) }
            .also { queue.addAll(it) }

        var currentNode: Node
        do {
            currentNode = queue.remove()
            visited.add(currentNode)
            neighbors(currentNode.vertex)
                .asSequence()
                .map { Node(it, currentNode) }
                .filter { !visited.contains(it) }
        } while (currentNode.vertex != finish)
        return currentNode.route()
    }
}

private fun neighbors(vertex: Vertex): List<Vertex> {
    val yList = (vertex.y.toInt() - 1..vertex.y.toInt() + 1)
        .map { it.toFloat() }
        .toList()
    val xList = (vertex.x.toInt() - 1..vertex.x.toInt() + 1)
        .map { it.toFloat() }
        .toList()
    return yList.flatMap { y ->
        xList.map { Vertex(it, y) }
    }
}

private data class Node(val vertex: Vertex, val prev: Node?)

private fun Node.route(): List<Vertex> {
    val path = Stack<Vertex>()
    var currentNode: Node? = this
    while (currentNode != null) {
        path.add(currentNode.vertex)
        currentNode = currentNode.prev
    }
    return path
}