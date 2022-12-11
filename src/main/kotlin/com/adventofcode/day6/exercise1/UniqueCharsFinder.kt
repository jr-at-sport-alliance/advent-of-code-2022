package com.adventofcode.day6.exercise1

import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            findFourUniqueCharsEndIndex(reader)
        }
    }
    println(result)
}

object Resources

fun findFourUniqueCharsEndIndex(reader: Reader) =
    generateSequence {
        reader.read().let { if (it == -1) null else it.toChar() }
    }.windowed(size = 4).mapIndexed { i, it ->
        if (it.toSet().size == 4) i + 4 else null
    }.filterNotNull().first()
