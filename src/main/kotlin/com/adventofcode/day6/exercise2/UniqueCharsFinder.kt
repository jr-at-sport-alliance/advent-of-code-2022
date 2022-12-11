package com.adventofcode.day6.exercise2

import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            findFourteenUniqueCharsEndIndex(reader)
        }
    }
    println(result)
}

object Resources

fun findFourteenUniqueCharsEndIndex(reader: Reader) =
    generateSequence {
        reader.read().let { if (it == -1) null else it.toChar() }
    }.windowed(size = 14).mapIndexed { i, it ->
        if (it.toSet().size == 14) i + 14 else null
    }.filterNotNull().first()
