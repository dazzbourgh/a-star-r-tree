package ru.spbstu.ics

data class Vertex(val x: Int, val y: Int)
data class Obstacle(val vertexes: List<Vertex>)