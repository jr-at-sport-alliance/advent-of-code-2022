package com.adventofcode.day11.exercise2

import java.io.Reader
import java.lang.System.lineSeparator
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            runMonkeyThrowingGame(reader)
        }
    }
    println(result)
}

object Resources

private val monkeyRegex = """
    Monkey (\d+):\r?\n
    \s*Starting items: (\d+(?:, \d+)*)\r?\n
    \s*Operation: new = (old|\d+) (\*|\+) (old|\d+)\r?\n
    \s*Test: divisible by (\d+)\r?\n
    \s*If true: throw to monkey (\d+)\r?\n
    \s*If false: throw to monkey (\d+)
""".trimIndent().replace("\r?\n".toRegex(), "").toRegex()

fun runMonkeyThrowingGame(reader: Reader): Long {
    val monkeys = reader.useLines { lines ->
        lines.windowed(size = 6)
            .mapNotNull { chunk ->
                monkeyRegex.matchEntire(chunk.joinToString(lineSeparator()))
                    ?.let { match ->
                        Monkey(
                            id = match.groupValues[1].toInt(),
                            items = match.groupValues[2].split(", ").map { it.toInt() }.toMutableList(),
                            itemInspectionOperation = itemInspectionOperation(
                                match.groupValues[3],
                                match.groupValues[4],
                                match.groupValues[5]
                            ),
                            nextTargetOperation = {
                                if (it % match.groupValues[6].toInt() == 0) match.groupValues[7].toInt()
                                else match.groupValues[8].toInt()
                            },
                            nextTargetDivisor = match.groupValues[6].toInt()
                        )
                    }
            }.toList()
    }
    require(monkeys.mapIndexed { index, monkey -> index to monkey }.all { (index, monkey) -> monkey.id == index }) {
        "Monkey list must increase continuously and consecutively by ID, " +
            "starting at 0, but instead list contained following IDs: " +
            monkeys.map { it.id }
    }
    val commonDivisorMultiple = monkeys.map { it.nextTargetDivisor }.reduce { a, b -> if (a == b) a else a * b }
    repeat(10000) {
        monkeys.forEach { monkey ->
            val items = mutableListOf<Int>().also { it.addAll(monkey.items) }
            monkey.items.clear()
            items.forEach { item ->
                val newItem = (monkey.itemInspectionOperation(item) % commonDivisorMultiple).toInt()
                require(newItem >= 0) { "Integer overflow" }
                monkeys[monkey.nextTargetOperation(newItem)].items.add(newItem)
                monkey.inspectionCount++
            }
        }
    }
    return monkeys.map { it.inspectionCount }.sorted().takeLast(2).map { it.toLong() }.reduce { a, b -> a * b }
}

private fun itemInspectionOperation(operand1: String, operator: String, operand2: String): (Int) -> Long = {
    val operation: (Int, Int) -> Long =
        when (operator) {
            "+" -> { a, b -> a.toLong() + b.toLong() }
            "*" -> { a, b -> a.toLong() * b.toLong() }
            else -> error("invalid op: $operator")
        }
    operation(if (operand1 == "old") it else operand1.toInt(), if (operand2 == "old") it else operand2.toInt())
}

private data class Monkey(
    val id: Int,
    val items: MutableList<Int>,
    val itemInspectionOperation: (Int) -> Long,
    val nextTargetOperation: (Int) -> Int,
    val nextTargetDivisor: Int,
    var inspectionCount: Int = 0
)
