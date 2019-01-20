package ru.spbstu.ics

import org.spf4j.ds.RTree

class ObstacleTree {
    private val rTree = RTree<Obstacle>(4, 2, 2, RTree.SeedPicker.LINEAR)

    val size: Int get() = rTree.size()

    fun put(obstacle: Obstacle) {
        // Coords for bottom left vertex of a bounding rectangle.
        // Bounding rectangle is a wrapping rectangle, containing the wall
        //   and is always perpendicular to axes.
        val bottomLeft = getCorner(obstacle, true)
        val topRight = getCorner(obstacle, false)
        val dimensions = getDimensions(bottomLeft, topRight)
        rTree.insert(bottomLeft, dimensions, obstacle)
    }

    fun intersects(vertex: Vertex): Boolean {
        val bottomLeftCoords = floatArrayOf(vertex.y.toFloat(), vertex.x.toFloat())
        val dimensions = floatArrayOf(0f, 0f)
        return !rTree.search(bottomLeftCoords, dimensions).isEmpty()
    }
}

private fun getDimensions(bottomLeft: FloatArray, topRight: FloatArray): FloatArray {
    val dimensions = FloatArray(2)
    dimensions[0] = topRight[0] - bottomLeft[0]
    dimensions[1] = topRight[1] - bottomLeft[1]
    return dimensions
}

private fun getCorner(obstacle: Obstacle, bottom: Boolean): FloatArray {
    val y = getMinValue(obstacle, bottom) { it.y.toFloat() }
    val x = getMinValue(obstacle, bottom) { it.x.toFloat() }
    val bottomLeft = FloatArray(2)
    bottomLeft[0] = y
    bottomLeft[1] = x
    return bottomLeft
}

private fun getMinValue(
    obstacle: Obstacle,
    bottom: Boolean,
    mapper: (v: Vertex) -> Float
): Float {
    val comparator: Comparator<Float> = if (bottom) Comparator.naturalOrder() else Comparator.reverseOrder()
    return obstacle.vertexes
        .asSequence()
        .map(mapper)
        .sortedWith(comparator)
        .min()!!
}