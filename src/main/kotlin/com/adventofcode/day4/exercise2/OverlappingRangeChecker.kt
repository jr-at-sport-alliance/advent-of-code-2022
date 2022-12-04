package com.adventofcode.day4.exercise2

import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            countOverlappingRanges(reader)
        }
    }
    println(result)
}

internal object Resources

fun countOverlappingRanges(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.map {
            it.split(",").toPair()
        }.map {
            pairOfRawRangesToPairOfRanges(it)
        }.filter { (rangeA, rangeB) ->
            rangesOverlap(rangeA, rangeB)
        }.count()
    }
}

private fun pairOfRawRangesToPairOfRanges(pairOfRawRanges: Pair<String, String>) =
    pairOfRawRanges.toList().map { rawRangeToRange(it) }.toPair()

private fun rawRangeToRange(rawRange: String) =
    rawRange.split("-").map {
        it.toInt()
    }.zipWithNext().map {
        it.first..it.second
    }.single()

private fun rangesOverlap(rangeA: IntRange, rangeB: IntRange) = rangeA.intersect(rangeB).isNotEmpty()

private fun <T> List<T>.toPair() = zipWithNext().single()
