package ru.spbstu.ics

data class Vertex(val x: Float, val y: Float)
data class Obstacle(val vertexes: List<Vertex>)