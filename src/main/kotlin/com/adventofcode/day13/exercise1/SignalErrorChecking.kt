package com.adventofcode.day13.exercise1

import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            countCorrectSignals(reader)
        }
    }
    println(result)
}

object Resources

fun countCorrectSignals(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.chunked(3).onEach {
            require(it.size == 2 || it[2].isBlank())
        }.map {
            it[0] to it[1]
        }.map {
            it.toList().map(::inputToList).zipWithNext().single()
        }.mapIndexed { i, it -> (i + 1) to it }.filter { (_, packetPair) ->
            firstPacketIsLessThanSecondPacket(packetPair.first, packetPair.second)!!
        }.map { (index, _) -> index }.sum()
    }
}

fun firstPacketIsLessThanSecondPacket(first: List<Any>, second: List<Any>): Boolean? =
    first.asSequence().fullOuterJoin(second.asSequence())
        .map { (left, right) ->
            if (left == null) {
                true
            } else if (right == null) {
                false
            } else if (left is Int && right is Int) {
                if (left < right) true
                else if (right < left) false
                else null
            } else {
                firstPacketIsLessThanSecondPacket(
                    wrapIntToListIfNeeded(left),
                    wrapIntToListIfNeeded(right)
                )
            }
        }.filterNotNull().firstOrNull()

fun wrapIntToListIfNeeded(any: Any) = when (any) {
    is Int -> listOf(any)
    is List<*> -> any.requireNoNulls()
    else -> error("Unexpected type: ${any.javaClass}")
}

fun <T> Sequence<T>.fullOuterJoin(other: Sequence<T>) = object : Sequence<Pair<T?, T?>> {
    override fun iterator() = object : Iterator<Pair<T?, T?>> {
        val leftIterator = this@fullOuterJoin.iterator()
        val rightIterator = other.iterator()

        override fun hasNext() = leftIterator.hasNext() || rightIterator.hasNext()
        override fun next(): Pair<T?, T?> =
            (if (leftIterator.hasNext()) leftIterator.next() else null) to
                if (rightIterator.hasNext()) rightIterator.next() else null
    }
}

fun inputToList(input: String): List<Any> {
    if (input.isEmpty()) return emptyList()
    require(input.first() == '[')
    require(input.last() == ']')
    return input.substring(1, input.length - 1).splitByTopLevelCommas().map {
        it.toIntOrNull() ?: inputToList(it)
    }
}

fun String.splitByTopLevelCommas(): List<String> {
    var currentFirstChar = 0
    var currentDepth = 0
    val sections = mutableListOf<String>()
    forEachIndexed { i, char ->
        when (char) {
            '[' -> currentDepth++
            ']' -> currentDepth--
            ',' -> if (currentDepth == 0) {
                sections.add(substring(currentFirstChar, i))
                currentFirstChar = i + 1
            }
        }
    }
    sections.add(substring(currentFirstChar, length))
    return sections
}
