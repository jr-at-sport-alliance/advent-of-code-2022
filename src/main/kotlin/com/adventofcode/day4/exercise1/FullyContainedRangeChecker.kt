package com.adventofcode.day4.exercise1

import java.io.Reader
import java.lang.Integer.min
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            countFullyContainedRanges(reader)
        }
    }
    println(result)
}

internal object Resources

fun countFullyContainedRanges(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.map {
            it.split(",").toPair()
        }.map {
            pairOfRawRangesToPairOfRanges(it)
        }.filter { (rangeA, rangeB) ->
            eitherRangeFullyContainedInOther(rangeA, rangeB)
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

private fun eitherRangeFullyContainedInOther(rangeA: IntRange, rangeB: IntRange) =
    rangeA.intersect(rangeB).size == min(rangeA.toSet().size, rangeB.toSet().size)

private fun <T> List<T>.toPair() = zipWithNext().single()
