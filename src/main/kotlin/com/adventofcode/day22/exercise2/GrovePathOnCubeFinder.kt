package com.adventofcode.day22.exercise2

import com.adventofcode.day22.exercise2.Direction.DOWN
import com.adventofcode.day22.exercise2.Direction.LEFT
import com.adventofcode.day22.exercise2.Direction.RIGHT
import com.adventofcode.day22.exercise2.Direction.UP
import java.io.Reader
import java.nio.charset.StandardCharsets
import kotlin.math.abs

typealias IntTriple = Triple<Int, Int, Int>
typealias IntTripleTriple = Triple<IntTriple, IntTriple, IntTriple>

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            findFinalPositionAndFacing(reader)
        }
    }
    println(result)
}

object Resources

fun findFinalPositionAndFacing(reader: Reader): Int {
    val (instructions, map) = reader.useLines { lines ->
        lines.filter { it.isNotBlank() }.partition { it.contains("""\d""".toRegex()) }
    }.let { (instructionLines, mapLines) ->
        parseInstructions(instructionLines) to connectNodes(parseMapLines(mapLines))
    }

    var node = map[0]
    var direction = RIGHT
    for (instruction in instructions) {
        when (instruction) {
            is DistanceInstruction -> run {
                repeat(instruction.distance) {
                    if (node.connections[direction] != null) {
                        val transition = node.connections[direction]!!
                        node = transition.first
                        direction += transition.second
                    } else return@run
                }
            }
            is RotationInstruction -> direction += instruction.rotation
        }
    }
    return 1000 * node.row + 4 * node.column + direction.facing
}

fun parseInstructions(lines: List<String>): List<Instruction> =
    lines.single().split("""(?<=\d)(?=[RL])|(?<=[RL])(?=\d+)""".toRegex()).map { instruction ->
        """(\d+|[RL])""".toRegex().matchEntire(instruction)?.groupValues?.get(1)?.let {
            when (it) {
                "R" -> RotationInstruction(Rotation.RIGHT)
                "L" -> RotationInstruction(Rotation.LEFT)
                else -> DistanceInstruction(it.toInt())
            }
        } ?: error("Could not match instruction: $instruction")
    }

fun parseMapLines(lines: List<String>): List<List<MapElement>> {
    val maxWidth = lines.maxOfOrNull { it.length }!!
    return lines.mapIndexed { i, line ->
        (line + nSpaces(maxWidth - line.length)).mapIndexed { j, it ->
            when (it) {
                ' ' -> ::Space
                '#' -> ::Wall
                '.' -> ::MapNode
                else -> error("Unknown map char: $it")
            }(i + 1, j + 1)
        }
    }
}

fun determineEdgeLength(map: List<List<MapElement>>) =
    sequenceOf(map, map.transpose()).map { arrangement ->
        arrangement.minOfOrNull { row ->
            row.count { it !is Space }
        }!!
    }.min()

fun splitIntoFaces(map: List<List<MapElement>>): List<List<List<MapElement>>> {
    val edgeLength = determineEdgeLength(map)
    require(map.all { it.size == map[0].size })
    val faces = mutableListOf<List<List<MapElement>>>()
    var i = 0
    var j = 0
    var facesFoundInCurrentRow = false
    while (faces.size < 6 && (i < map.size - edgeLength || j < map[0].size)) {
        val candidate = map.subMap(i, i + edgeLength, j, j + edgeLength)
        j += if (candidate.all { row -> row.all { it is MapNode || it is Wall } }) {
            faces.add(candidate)
            facesFoundInCurrentRow = true
            edgeLength
        } else 1
        if (j > map[i].size - edgeLength) {
            j = 0
            i += if (facesFoundInCurrentRow) edgeLength else 1
            facesFoundInCurrentRow = false
        }
    }
    return faces
}

fun assignFaces(unassignedFaces: List<List<List<MapElement>>>): Map<IntTriple, Face> {
    val unassignedFacesWorkingCopy = unassignedFaces.toMutableList()
    val edgeLength = unassignedFaces[0].size
    val faces = mutableMapOf(
        Triple(0, 0, 1) to Face(
            Triple(
                Triple(1, 0, 0),
                Triple(0, 1, 0),
                Triple(0, 0, 1)
            ),
            unassignedFaces[0]
        )
    )
    unassignedFacesWorkingCopy.removeAt(0)

    while (unassignedFacesWorkingCopy.isNotEmpty()) {
        val unassignedFacesIterator = unassignedFacesWorkingCopy.map { it }.toList().iterator()
        val unassignedFacesWorkingCopySize = unassignedFacesWorkingCopy.size
        while (unassignedFacesIterator.hasNext()) {
            val currentFace = unassignedFacesIterator.next()
            val currentFaceOrigin = currentFace[0][0].let { it.row to it.column }
            val neighbouringFace = faces.values.find { face ->
                val (x, y) = face.nodes[0][0].let { it.row to it.column }
                currentFaceOrigin in listOf(
                    x - edgeLength to y,
                    x + edgeLength to y,
                    x to y - edgeLength,
                    x to y + edgeLength
                )
            } ?: continue
            val neighbouringFaceOrigin = neighbouringFace.nodes[0][0].let { it.row to it.column }
            val originDifference =
                (currentFaceOrigin.first - neighbouringFaceOrigin.first) to
                    (currentFaceOrigin.second - neighbouringFaceOrigin.second)
            val rotation = rotationFromOrientationAndDirection(
                neighbouringFace.orientation,
                when (originDifference) {
                    0 to edgeLength -> RIGHT
                    edgeLength to 0 -> DOWN
                    0 to -edgeLength -> LEFT
                    -edgeLength to 0 -> UP
                    else -> error("unexpected cube face offset: $originDifference")
                }
            )
            val currentFaceAssigned = Face(
                rotate(neighbouringFace.orientation.transpose(), rotation).transpose(),
                currentFace
            )
            require(!faces.containsKey(currentFaceAssigned.orientation.third))
            faces[currentFaceAssigned.orientation.third] = currentFaceAssigned
            unassignedFacesWorkingCopy.remove(currentFace)
        }
        require(unassignedFacesWorkingCopySize != unassignedFacesWorkingCopy.size) {
            "No progress in assigning faces after pass through entire list"
        }
    }
    return faces
}

fun List<List<MapElement>>.subMap(iStart: Int, iEnd: Int, jStart: Int, jEnd: Int) =
    filterIndexed { i, _ -> i in iStart until iEnd }.map { row ->
        row.filterIndexed { j, _ -> j in jStart until jEnd }
    }

fun connectNodes(nodeMap: List<List<MapElement>>): List<MapNode> {
    val faces = splitIntoFaces(nodeMap)
    val assignedFaces = assignFaces(faces)
    return listOf(
        listOf(assignedFaces[Triple(0, 0, 1)]!!),
        assignedFaces.values.filterNot { it.orientation.third == Triple(0, 0, 1) }
    ).flatten().map { face ->
        face.nodes.mapIndexed { i, row ->
            row.withIndex().filter { (_, element) ->
                element is MapNode
            }.map { (j, element) ->
                IndexedValue(j, element as MapNode)
            }.map { (j, node) ->
                node.also {
                    listOf(
                        RIGHT to selectNextMapNode(assignedFaces, face, i, j) { i, j -> i to j + 1 },
                        DOWN to selectNextMapNode(assignedFaces, face, i, j) { i, j -> i + 1 to j },
                        LEFT to selectNextMapNode(assignedFaces, face, i, j) { i, j -> i to j - 1 },
                        UP to selectNextMapNode(assignedFaces, face, i, j) { i, j -> i - 1 to j }
                    ).filter { it.second != null }.map { (direction, node) -> direction to node!! }.forEach {
                        node.connections[it.first] = it.second
                    }
                }
            }
        }
    }.flatten().flatten()
}

fun nSpaces(n: Int) = (0 until n).joinToString(separator = "") { " " }

private fun selectNextMapNode(
    faces: Map<IntTriple, Face>,
    currentFace: Face,
    iInitial: Int,
    jInitial: Int,
    mover: (Int, Int) -> Pair<Int, Int>
): Pair<MapNode, Rotation>? {
    val (i, j) = mover(iInitial, jInitial)

    if (i >= 0 && i < currentFace.nodes.size && j >= 0 && j < currentFace.nodes[i].size) {
        return (currentFace.nodes[i][j] as? MapNode)?.let { it to Rotation.NONE }
    }

    val direction = if (j >= currentFace.nodes.size) RIGHT
    else if (i >= currentFace.nodes.size) DOWN
    else if (j < 0) LEFT
    else UP

    var connectingOrientation = rotate(
        currentFace.orientation.transpose(),
        rotationFromOrientationAndDirection(currentFace.orientation, direction)
    ).transpose()
    val connectingFace = faces[connectingOrientation.third]!!
    var rotations = 0
    while (connectingOrientation != connectingFace.orientation) {
        if (rotations > 4) error("Cannot rotate to find shape")
        connectingOrientation = rotate(connectingOrientation.transpose(), connectingOrientation.third).transpose()
        rotations++
    }
    val iRotatedConnectingFace =
        if (i < 0) connectingFace.nodes.size + (i % connectingFace.nodes.size)
        else if (i >= connectingFace.nodes.size) i % connectingFace.nodes.size
        else i
    val jRotatedConnectingFace =
        if (j < 0) {
            connectingFace.nodes[iRotatedConnectingFace].size + (j % connectingFace.nodes[iRotatedConnectingFace].size)
        } else if (j >= connectingFace.nodes[iRotatedConnectingFace].size) {
            j % connectingFace.nodes[iRotatedConnectingFace].size
        } else j

    val rotation = Rotation.byClockwiseTurns(rotations)
    val node =
        connectingFace.nodes.rotateElements((-rotations).mod(4))[iRotatedConnectingFace][jRotatedConnectingFace]
    return (node as? MapNode)?.let { it to rotation }
}

enum class Direction(val facing: Int) {
    RIGHT(0), DOWN(1), LEFT(2), UP(3);

    companion object {
        fun byFacing(facing: Int): Direction =
            values().find { it.facing == facing } ?: error("invalid facing: $facing")
    }
}

enum class Rotation(val facingDelta: Int) {
    NONE(0), RIGHT(1), HALF_TURN(2), LEFT(3);

    companion object {
        fun byClockwiseTurns(turns: Int): Rotation =
            values().find { it.facingDelta == turns } ?: error("invalid turns: $turns")
    }
}

operator fun Direction.plus(rotation: Rotation): Direction =
    Direction.byFacing((this.facing + rotation.facingDelta) % 4)

sealed interface MapElement {
    val row: Int
    val column: Int
}

data class MapNode(
    override val row: Int,
    override val column: Int
) : MapElement {
    var connections: MutableMap<Direction, Pair<MapNode, Rotation>> = mutableMapOf()
}

data class Wall(
    override val row: Int,
    override val column: Int
) : MapElement

data class Space(
    override val row: Int,
    override val column: Int
) : MapElement

sealed interface Instruction
data class DistanceInstruction(val distance: Int) : Instruction
data class RotationInstruction(val rotation: Rotation) : Instruction

data class Face(val orientation: IntTripleTriple, val nodes: List<List<MapElement>>)

operator fun List<List<Int>>.times(other: List<List<Int>>): List<List<Int>> {
    require(all { it.size == this[0].size })
    require(other.all { it.size == other[0].size })
    require(this[0].size == other.size)
    return map { thisRow ->
        other.transpose().map { otherColumn ->
            other.indices.sumOf { thisRow[it] * otherColumn[it] }
        }
    }
}

@JvmName("intTripleTripleTimesIntTriple")
operator fun IntTripleTriple.times(other: IntTriple): IntTriple =
    (toList().map { it.toList() } * listOf(other.toList()).transpose()).transpose()[0].toTriple()

@JvmName("intTripleTripleTimesIntTripleTriple")
operator fun IntTripleTriple.times(other: IntTripleTriple): IntTripleTriple =
    (toList().map { it.toList() } * other.toList().map { it.toList() }).map { it.toTriple() }.toTriple()

val ROTATE_PLUS_X = Triple(
    Triple(1, 0, 0),
    Triple(0, 0, -1),
    Triple(0, 1, 0)
)

val ROTATE_PLUS_Y = Triple(
    Triple(0, 0, 1),
    Triple(0, 1, 0),
    Triple(-1, 0, 0)
)

val ROTATE_PLUS_Z = Triple(
    Triple(0, -1, 0),
    Triple(1, 0, 0),
    Triple(0, 0, 1)
)

val ROTATE_MINUS_X = Triple(
    Triple(1, 0, 0),
    Triple(0, 0, 1),
    Triple(0, -1, 0)
)

val ROTATE_MINUS_Y = Triple(
    Triple(0, 0, -1),
    Triple(0, 1, 0),
    Triple(1, 0, 0)
)

val ROTATE_MINUS_Z = Triple(
    Triple(0, 1, 0),
    Triple(-1, 0, 0),
    Triple(0, 0, 1)
)

fun rotate(input: IntTripleTriple, rotation: IntTriple): IntTripleTriple {
    require(
        rotation.toList().map { abs(it) }.sortedDescending().drop(1).all { it == 0 } &&
            rotation.toList().maxOf { abs(it) } != 0
    ) { "$rotation not a rotation vector" }
    var result = input
    repeat(rotation.toList().maxOf { abs(it) }) {
        val rotationMatrix = if (rotation.first > 0) ROTATE_PLUS_X else if (rotation.first < 0) ROTATE_MINUS_X
        else if (rotation.second > 0) ROTATE_PLUS_Y else if (rotation.second < 0) ROTATE_MINUS_Y
        else if (rotation.third > 0) ROTATE_PLUS_Z else ROTATE_MINUS_Z
        result = rotationMatrix * result
    }
    return result
}

operator fun IntTriple.unaryMinus() = toList().map { -it }.toTriple()

fun <E> List<E>.toTriple() = chunked(3).map { Triple(it[0], it[1], it[2]) }.single()

fun rotationFromOrientationAndDirection(orientation: IntTripleTriple, direction: Direction): IntTriple =
    when (direction) {
        RIGHT -> -orientation.first
        DOWN -> orientation.second
        LEFT -> orientation.first
        UP -> -orientation.second
    }

fun <E> List<List<E>>.rotateElements(times: Int): List<List<E>> {
    var result = this
    repeat(times.mod(4)) {
        result = List(result[0].size) { j ->
            List(result.size) { i ->
                result[result.size - 1 - i][j]
            }
        }
    }
    return result
}

@JvmName("matrixTranspose")
fun <E> List<List<E>>.transpose(): List<List<E>> {
    val rows = size
    val columns = maxOfOrNull { it.size }!!
    require(all { it.size == columns })
    return (0 until columns).map { j ->
        (0 until rows).map { i ->
            get(i)[j]
        }
    }
}

@JvmName("intTripleTripleTranspose")
fun IntTripleTriple.transpose(): IntTripleTriple =
    toList().map { it.toList() }.transpose().map { it.toTriple() }.toTriple()
