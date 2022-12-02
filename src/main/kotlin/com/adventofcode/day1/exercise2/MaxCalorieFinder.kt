package com.adventofcode.day1.exercise2

import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result =
        Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
            inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                findMaxCalories(reader)
            }
        }
    println(result)
}

internal object Resources

fun findMaxCalories(reader: Reader): Int {
    var currentGroup = 0
    var currentMaximums = listOf(0, 0, 0)
    reader.useLines() { lines ->
        lines.forEach { line ->
            if (line.isBlank()) {
                if (currentGroup > currentMaximums.last()) {
                    currentMaximums = (currentMaximums + currentGroup).sortedDescending().dropLast(1)
                }
                currentGroup = 0
            } else {
                currentGroup += line.toInt()
            }
        }
    }
    if (currentMaximums.any { currentGroup > it }) {
        currentMaximums = (currentMaximums + currentGroup).sortedDescending().dropLast(1)
    }
    return currentMaximums.sum()
}
