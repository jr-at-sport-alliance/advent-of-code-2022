package com.adventofcode.day20.exercise1

import java.io.Reader
import java.nio.charset.StandardCharsets
import java.util.LinkedList

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            findGroveCoords(reader)
        }
    }
    println(result)
}

object Resources

fun findGroveCoords(reader: Reader): Int {
    val initialList = reader.useLines { lines -> lines.map { ListValue(it.toInt()) }.toList() }
    val resultList = CircularList<ListValue<Int>>()
    val zeroValue = initialList.find { it.value == 0 }
    resultList.addAll(initialList)
    initialList.forEach {
        val index = resultList.indexOf(it)
        require(index >= 0) { "Cannot find $it in $resultList" }
        resultList.rotate(index, it.value)
    }
    val indexOfZeroValue = resultList.indexOf(zeroValue)
    return listOf(1000, 2000, 3000).sumOf { resultList[it + indexOfZeroValue].value }
}

class CircularList<E> : AbstractMutableList<E>() {
    private val backingList: LinkedList<E> = LinkedList()

    override val size get() = backingList.size

    override fun get(index: Int) = backingList[backingListIndexPreferStart(index)]
    override fun add(index: Int, element: E) = backingList.add(backingListIndexPreferEnd(index), element)
    override fun removeAt(index: Int) = backingList.removeAt(backingListIndexPreferStart(index))
    override fun set(index: Int, element: E) = backingList.set(backingListIndexPreferStart(index), element)

    fun rotate(index: Int, delta: Int) {
        if (delta == 0) return
        val element = backingList.removeAt(index)
        backingList.add(backingListIndexPreferEnd(index + delta), element)
    }

    private fun backingListIndexPreferStart(index: Int) = if (index == 0) 0 else (index % size + size) % size
    private fun backingListIndexPreferEnd(index: Int) =
        backingListIndexPreferStart(index).let { if (it == 0) size else it }
}

class ListValue<V>(val value: V) {
    override fun toString() = value.toString()
}
