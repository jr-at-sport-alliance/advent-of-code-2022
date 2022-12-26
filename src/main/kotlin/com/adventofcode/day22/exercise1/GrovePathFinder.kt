package com.adventofcode.day22.exercise1

import com.adventofcode.day22.exercise1.Direction.DOWN
import com.adventofcode.day22.exercise1.Direction.LEFT
import com.adventofcode.day22.exercise1.Direction.RIGHT
import com.adventofcode.day22.exercise1.Direction.UP
import java.io.Reader
import java.nio.charset.StandardCharsets

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
        parseInstructions(instructionLines) to connectNodes(parseMapLines(mapLines)).flatten()
    }

    var node = map[0]
    var direction = RIGHT
    for (instruction in instructions) {
        when (instruction) {
            is DistanceInstruction -> run {
                repeat(instruction.distance) {
                    if (node.connections[direction] != null) node = node.connections[direction]!! else return@run
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
                ' ' -> Space
                '#' -> Wall
                '.' -> MapNode(i + 1, j + 1)
                else -> error("Unknown map char: $it")
            }
        }
    }
}

fun connectNodes(nodeMap: List<List<MapElement>>): List<List<MapNode>> {
    return nodeMap.mapIndexed { i, row ->
        row.withIndex().filter { (_, element) ->
            element is MapNode
        }.map { (j, element) ->
            IndexedValue(j, element as MapNode)
        }.map { (j, node) ->
            node.also {
                listOf(
                    RIGHT to selectNextMapNode(nodeMap, i, j) { i, j -> i to j + 1 },
                    DOWN to selectNextMapNode(nodeMap, i, j) { i, j -> i + 1 to j },
                    LEFT to selectNextMapNode(nodeMap, i, j) { i, j -> i to j - 1 },
                    UP to selectNextMapNode(nodeMap, i, j) { i, j -> i - 1 to j }
                ).filter { it.second != null }.map { (direction, node) -> direction to node!! }.forEach {
                    node.connections[it.first] = it.second
                }
            }
        }
    }
}

fun nSpaces(n: Int) = (0 until n).joinToString(separator = "") { " " }

private fun selectNextMapNode(
    map: List<List<MapElement>>,
    iInitial: Int,
    jInitial: Int,
    mover: (Int, Int) -> Pair<Int, Int>
): MapNode? {
    var i = iInitial
    var j = jInitial
    do {
        val newCoord = mover(i, j)
        i = newCoord.first
        j = newCoord.second

        if (i < 0) i = map.size + (i % map.size) else if (i >= map.size) i %= map.size
        if (j < 0) j = map[i].size + (j % map[i].size) else if (j >= map[i].size) j %= map[i].size

        val el = map[i][j]
        if (el is MapNode) return el else if (el is Wall) return null
    } while (i != iInitial || j != jInitial)
    return null
}

enum class Direction(val facing: Int) {
    RIGHT(0), DOWN(1), LEFT(2), UP(3)
}

enum class Rotation {
    RIGHT, LEFT
}

operator fun Direction.plus(rotation: Rotation): Direction = when (this) {
    RIGHT -> if (rotation == Rotation.RIGHT) DOWN else UP
    DOWN -> if (rotation == Rotation.RIGHT) LEFT else RIGHT
    LEFT -> if (rotation == Rotation.RIGHT) UP else DOWN
    UP -> if (rotation == Rotation.RIGHT) RIGHT else LEFT
}

sealed interface MapElement

data class MapNode(
    val row: Int,
    val column: Int
) : MapElement {
    var connections: MutableMap<Direction, MapNode> = mutableMapOf()
}

object Wall : MapElement
object Space : MapElement

sealed interface Instruction
data class DistanceInstruction(val distance: Int) : Instruction
data class RotationInstruction(val rotation: Rotation) : Instruction
