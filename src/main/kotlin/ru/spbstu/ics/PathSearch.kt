package ru.spbstu.ics

import java.util.PriorityQueue
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

class PathSearch(private val obstacleTree: ObstacleTree) {
    fun search(start: Vertex, finish: Vertex): List<Vertex> {
        val queue = PriorityQueue<Node>(compareBy { node ->
            sqrt((node.vertex.y - finish.y).pow(2) + (node.vertex.x - finish.x).pow(2))
        })
        queue.add(Node(start, null))
        val visited = mutableSetOf<Vertex>()

        var currentNode: Node
        do {
            currentNode = queue.remove()
                .also { println(it.vertex) }
            visited.add(currentNode.vertex)
            neighbors(currentNode.vertex)
                .asSequence()
                .map { Node(it, currentNode) }
                .filter { !visited.contains(it.vertex) }
                .filter { !obstacleTree.intersects(it.vertex) }
                .also { queue.addAll(it) }
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
        .filter { it.x >= 0 && it.y >= 0 }
}

private data class Node(val vertex: Vertex, val prev: Node?)

private fun Node.route(): List<Vertex> {
    val path = Stack<Vertex>()
    var currentNode: Node? = this
    while (currentNode != null) {
        path.add(currentNode.vertex)
        currentNode = currentNode.prev
    }
    return path.asReversed()
}