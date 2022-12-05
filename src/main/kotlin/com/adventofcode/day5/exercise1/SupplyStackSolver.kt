package com.adventofcode.day5.exercise1

import com.adventofcode.day5.exercise1.InputState.CRATES
import com.adventofcode.day5.exercise1.InputState.CRATE_BASE
import com.adventofcode.day5.exercise1.InputState.CRATE_INSTRUCTION_DIVIDER
import com.adventofcode.day5.exercise1.InputState.INSTRUCTIONS
import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            solveForTopCrates(reader)
        }
    }
    println(result)
}

object Resources

val instructionLineRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

enum class InputState { CRATES, CRATE_BASE, CRATE_INSTRUCTION_DIVIDER, INSTRUCTIONS }

fun solveForTopCrates(reader: Reader): String {
    val stacks = mutableListOf<ArrayDeque<String>>()
    reader.useLines { lines ->
        var inputState: InputState? = null
        lines.forEach { line ->
            when (inputState) {
                null, CRATES -> {
                    if (line.contains("""\[[A-Z]]""".toRegex())) {
                        val numCratesInLine = (line.length + 1) / 4
                        while (stacks.size < numCratesInLine) stacks.add(ArrayDeque())
                        for (i in 0 until numCratesInLine) {
                            line[i * 4 + 1].toString().takeIf { it.isNotBlank() }?.also {
                                stacks[i].addLast(it)
                            }
                        }
                        inputState = CRATES
                    } else {
                        require(line.any { it.isDigit() })
                        require(
                            line.split(("\\s+").toRegex())
                                .filterNot { it.isEmpty() }
                                .last()
                                .toInt() == stacks.size
                        )
                        inputState = CRATE_BASE
                    }
                }
                CRATE_BASE -> {
                    require(line.isBlank())
                    inputState = CRATE_INSTRUCTION_DIVIDER
                }
                CRATE_INSTRUCTION_DIVIDER, INSTRUCTIONS -> {
                    val instructionLineMatchResult =
                        instructionLineRegex.matchEntire(line)
                    require(instructionLineMatchResult != null)
                    val (count, source, destination) =
                        instructionLineMatchResult.groupValues.drop(1).map {
                            it.toInt()
                        }.toTriple()
                    repeat(count) {
                        stacks[destination - 1].addFirst(stacks[source - 1].removeFirst())
                    }
                    inputState = INSTRUCTIONS
                }
            }
        }
    }
    return stacks.joinToString(separator = "") { it.first() }
}

fun <T> Iterable<T>.toTriple(): Triple<T, T, T> {
    val iterator = iterator()
    val result = Triple(iterator.next(), iterator.next(), iterator.next())
    require(!iterator.hasNext())
    return result
}
