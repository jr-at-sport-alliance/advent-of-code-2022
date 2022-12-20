package com.adventofcode.day20.exercise2

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

fun findGroveCoords(reader: Reader): Long {
    val initialList = reader.useLines { lines -> lines.map { ListValue(it.toLong() * 811589153L) }.toList() }
    val resultList = CircularList<ListValue<Long>>()
    val zeroValue = initialList.find { it.value == 0L }
    resultList.addAll(initialList)
    repeat(10) {
        initialList.forEach {
            val index = resultList.indexOf(it)
            require(index >= 0) { "Cannot find $it in $resultList" }
            resultList.rotate(index, it.value)
        }
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

    fun get(index: Long) = backingList[backingListIndexPreferStart(index)]
    fun add(index: Long, element: E) = backingList.add(backingListIndexPreferEnd(index), element)
    fun removeAt(index: Long) = backingList.removeAt(backingListIndexPreferStart(index))
    fun set(index: Long, element: E) = backingList.set(backingListIndexPreferStart(index), element)

    fun rotate(index: Int, delta: Long) {
        if (delta == 0L) return
        val element = backingList.removeAt(index)
        backingList.add(backingListIndexPreferEnd(index + delta), element)
    }

    private fun backingListIndexPreferStart(index: Int) = backingListIndexPreferStart(index.toLong())
    private fun backingListIndexPreferEnd(index: Int) = backingListIndexPreferEnd(index.toLong())

    private fun backingListIndexPreferStart(index: Long) = (if (index == 0L) 0 else (index % size + size) % size).toInt()
    private fun backingListIndexPreferEnd(index: Long) =
        backingListIndexPreferStart(index).let { if (it == 0) size else it }
}

class ListValue<V>(val value: V) {
    override fun toString() = value.toString()
}
