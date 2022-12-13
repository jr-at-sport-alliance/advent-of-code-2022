package com.adventofcode.day13.exercise2

import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            findDecoderKey(reader)
        }
    }
    println(result)
}

object Resources

val dividerPackets = listOf(listOf(listOf(2)), listOf(listOf(6)))

fun findDecoderKey(reader: Reader): Int {
    return reader.useLines { lines ->
        (lines.filterNot { it.isBlank() }.map(::inputToList) + dividerPackets).sortedWith { first, second ->
            firstPacketIsLessThanSecondPacket(first, second)?.let { if (it) -1 else 1 } ?: 0
        }.mapIndexedNotNull { i, packet -> if (packet in dividerPackets) i + 1 else null }.reduce { a, b -> a * b }
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
