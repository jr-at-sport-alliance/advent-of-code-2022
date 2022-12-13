package com.adventofcode.day12.exercise1

import java.io.Reader
import java.lang.System.lineSeparator
import java.nio.charset.StandardCharsets
import kotlin.streams.asSequence

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            getHillClimbingSteps(reader)
        }
    }
    println(result)
}

object Resources

fun getHillClimbingSteps(reader: Reader): Int {
    var start: Node? = null
    var end: Node? = null
    val nodes = reader.useLines { lines ->
        lines.mapIndexed { i, line ->
            line.chars().asSequence().map { it.toChar() }.mapIndexed() { j, it ->
                Node(characterToValue(it), i to j).also { node ->
                    if (it == 'S') start = node
                    else if (it == 'E') end = node
                }
            }.toList()
        }.toList()
    }
    nodes.forEachIndexed { i, row ->
        row.forEachIndexed { j, node ->
            with(nodes) {
                listOf(
                    trySelect(i - 1, j), // up
                    trySelect(i, j + 1), // right
                    trySelect(i + 1, j), // down
                    trySelect(i, j - 1) // left
                )
            }.filterNotNull().forEach { neighbour ->
                if (neighbour.value <= node.value + 1) node.children.add(neighbour)
            }
        }
    }
    return dijkstraShortestDistance(nodes.flatten(), start!!, end!!)
}

private fun <T> List<List<T>>.trySelect(i: Int, j: Int): T? =
    if (i in indices && j in this[i].indices) this[i][j] else null

private fun dijkstraShortestDistance(graph: List<Node>, source: Node, target: Node): Int {
    val distances = mutableMapOf<Node, Int>()
    val previouses = mutableMapOf<Node, Node>()
    val unvisited = graph.toMutableSet()
    distances[source] = 0

    while (unvisited.isNotEmpty()) {
        val current = distances.entries.filter { it.key in unvisited }.minByOrNull { it.value }?.key
        if (current == null) {
            val debugGraph2 =
                graph.groupBy { it.position.first }.entries.map { row ->
                    row.key to row.value.sortedBy { it.position.second }
                }.sortedBy { it.first }.map { it.second }.toList().map { row ->
                    row.map {
                        when (it) {
                            in unvisited -> ' '
                            source -> 'S'
                            target -> 'E'
                            else -> 'a' + it.value
                        }
                    }.joinToString("")
                }.joinToString(lineSeparator())
            println(debugGraph2)
            return -1
        }
        unvisited.remove(current)

        if (current == target) {
            val debugGraph =
                graph.groupBy { it.position.first }.entries.map { row ->
                    row.key to row.value.sortedBy { it.position.second }
                }.sortedBy { it.first }.map { it.second }.toList().map { row ->
                    row.map {
                        if (it == source) 'S' else if (it == target) 'E' else ('a' + it.value)
                    }.joinToString("")
                }.toMutableList()
            val debugGraph2 =
                graph.groupBy { it.position.first }.entries.map { row ->
                    row.key to row.value.sortedBy { it.position.second }
                }.sortedBy { it.first }.map { it.second }.toList().map { row ->
                    row.map {
                        if (it in unvisited) ' ' else if (it == source) 'S' else if (it == target) 'E' else ('a' + it.value)
                    }.joinToString("")
                }.joinToString(lineSeparator())
            println(debugGraph2)
            var backtrack = target
            var steps = 0
            while (previouses.containsKey(backtrack)) {
                backtrack = previouses[backtrack]!!
                debugGraph[backtrack.position.first] =
                    debugGraph[backtrack.position.first].replaceRange(backtrack.position.second, backtrack.position.second + 1, "+")
                steps++
                println(backtrack)
            }
            println(debugGraph.joinToString(lineSeparator()))
            require(backtrack == source)
            return steps
        }

        current.children.filter { it in unvisited }.forEach { child ->
            if (child.position in listOf(8 to 160, 8 to 159)) println("$current (dist: ${distances[current]}) $child")
            val alternativeDistance = distances[current]!! + 1
            if (!distances.containsKey(child) || alternativeDistance < distances[child]!!) {
                distances[child] = alternativeDistance
                previouses[child] = current
            }
        }
    }
    return -1
}

fun characterToValue(char: Char) =
    when (char) {
        'S' -> 0
        'E' -> 25
        else -> if (char in 'a'..'z') char - 'a' else error("expected S, E or a-z but was: $char")
    }

class Node(val value: Int, val position: Pair<Int, Int>, val children: MutableList<Node> = mutableListOf()) {
    override fun toString() = "Node($value, [${position.first}, ${position.second}])"
}
