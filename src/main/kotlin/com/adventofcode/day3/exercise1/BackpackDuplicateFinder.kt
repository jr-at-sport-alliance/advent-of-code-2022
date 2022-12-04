package com.adventofcode.day3.exercise1

import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            determineBackpackDuplicates(reader)
        }
    }
    println(result)
}

internal object Resources

fun determineBackpackDuplicates(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.map {
            it.substring(0, it.length / 2) to it.substring(it.length / 2)
        }.map { compartments ->
            compartments.toList().map { it.toCharArray().toSet() }.zipWithNext().single()
        }.map {
            it.first.intersect(it.second)
        }.map {
            itemToPriority(it.single())
        }.sum()
    }
}

@SuppressWarnings("MagicNumber")
fun itemToPriority(item: Char): Int =
    when (item) {
        in 'a'..'z' -> item - 'a' + 1
        in 'A'..'Z' -> item - 'A' + 27
        else -> error("Item must be an ASCII alpha char but was $item")
    }
