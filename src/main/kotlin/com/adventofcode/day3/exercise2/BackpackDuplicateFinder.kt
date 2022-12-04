package com.adventofcode.day3.exercise2

import com.adventofcode.day3.exercise1.Resources
import com.adventofcode.day3.exercise1.itemToPriority
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.Reader
import java.nio.charset.StandardCharsets
import kotlin.properties.Delegates
import kotlin.streams.asStream
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
@Suppress("MagicNumber")
fun main() {
    var result by Delegates.notNull<Int>()
    var bestElapsed = Long.MAX_VALUE.milliseconds
    repeat(100) {
        val (resultForThisRun, elapsedForThisRun) = measureTimedValue {
            Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    determineBackpackDuplicates(reader)
                }
            }
        }
        result = resultForThisRun
        if (elapsedForThisRun < bestElapsed) bestElapsed = elapsedForThisRun
    }
    println("Serial time taken: $bestElapsed")
    println(result)

    bestElapsed = Long.MAX_VALUE.milliseconds
    repeat(100) {
        val (resultForThisRun, elapsedForThisRun) = measureTimedValue {
            Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    determineBackpackDuplicatesParallel(reader)
                }
            }
        }
        result = resultForThisRun
        if (elapsedForThisRun < bestElapsed) bestElapsed = elapsedForThisRun
    }
    println("Parallel time taken: $bestElapsed")
    println(result)

    bestElapsed = Long.MAX_VALUE.milliseconds
    repeat(100) {
        val (resultForThisRun, elapsedForThisRun) = measureTimedValue {
            Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    determineBackpackDuplicatesParallelCoroutines(reader)
                }
            }
        }
        result = resultForThisRun
        if (elapsedForThisRun < bestElapsed) bestElapsed = elapsedForThisRun
    }
    println("Parallel (coroutines) time taken: $bestElapsed")
    println(result)
}

internal object Resources

fun determineBackpackDuplicates(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.windowed(size = 3, step = 3).map { backpacks ->
            backpacks.map { it.toCharArray().toSet() }
        }.map {
            it.reduce { a, b -> a.intersect(b) }
        }.map {
            itemToPriority(it.single())
        }.sum()
    }
}

fun determineBackpackDuplicatesParallel(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.windowed(size = 3, step = 3).asStream().parallel().map { backpacks ->
            backpacks.map { it.toCharArray().toSet() }
        }.map {
            it.reduce { a, b -> a.intersect(b) }
        }.map {
            itemToPriority(it.single())
        }.reduce { a, b -> a + b }.get()
    }
}

fun determineBackpackDuplicatesParallelCoroutines(reader: Reader): Int = runBlocking {
    withContext(Dispatchers.Default) {
        reader.useLines { lines ->
            lines.windowed(size = 3, step = 3).map { backpacks ->
                async(start = CoroutineStart.LAZY) { backpacks.map { it.toCharArray().toSet() } }
            }.map {
                async(start = CoroutineStart.LAZY) { it.await().reduce { a, b -> a.intersect(b) } }
            }.map {
                async(start = CoroutineStart.LAZY) { itemToPriority(it.await().single()) }
            }.reduce { a, b -> async(start = CoroutineStart.LAZY) { a.await() + b.await() } }.await()
        }
    }
}

fun printThreadName() = println(Thread.currentThread().name)

@SuppressWarnings("MagicNumber")
fun itemToPriority(item: Char): Int =
    when (item) {
        in 'a'..'z' -> item - 'a' + 1
        in 'A'..'Z' -> item - 'A' + 27
        else -> error("Item must be an ASCII alpha char but was $item")
    }
