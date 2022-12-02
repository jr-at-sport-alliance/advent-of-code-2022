package com.adventofcode.day1.exercise1

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
    var currentMax = 0
    reader.useLines() { lines ->
        lines.forEach { line ->
            if (line.isBlank()) {
                if (currentGroup > currentMax) currentMax = currentGroup
                currentGroup = 0
            } else {
                currentGroup += line.toInt()
            }
        }
    }
    if (currentGroup > currentMax) currentMax = currentGroup
    currentGroup = 0
    return currentMax
}
