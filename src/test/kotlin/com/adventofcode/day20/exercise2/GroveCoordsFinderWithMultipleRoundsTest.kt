package com.adventofcode.day20.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class GroveCoordsFinderWithMultipleRoundsTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            GroveCoordsFinderWithMultipleRoundsTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findGroveCoords(reader)
                }
            }
        assertThat(result).isEqualTo(1623178306L)
    }

    @Test
    fun `test circular list rotate nil`() {
        val circularList = CircularList<Int>()
        circularList.addAll(listOf(1, 2, 3))
        circularList.rotate(0, 0)
        assert(circularList[0] == 1) { circularList }
        assert(circularList[1] == 2) { circularList }
        assert(circularList[2] == 3) { circularList }
    }

    @Test
    fun `test circular list rotate right without wrap`() {
        val circularList = CircularList<Int>()
        circularList.addAll(listOf(1, 2, 3))
        circularList.rotate(0, 1)
        assert(circularList[0] == 2) { circularList }
        assert(circularList[1] == 1) { circularList }
        assert(circularList[2] == 3) { circularList }
    }

    @Test
    fun `test circular list rotate right with wrap`() {
        val circularList = CircularList<Int>()
        circularList.addAll(listOf(1, 2, 3))
        circularList.rotate(0, 3)
        assert(circularList[0] == 2) { circularList }
        assert(circularList[1] == 1) { circularList }
        assert(circularList[2] == 3) { circularList }
    }

    @Test
    fun `test circular list rotate left without wrap`() {
        val circularList = CircularList<Int>()
        circularList.addAll(listOf(1, 2, 3))
        circularList.rotate(2, -1)
        assert(circularList[0] == 1) { circularList }
        assert(circularList[1] == 3) { circularList }
        assert(circularList[2] == 2) { circularList }
    }

    @Test
    fun `test circular list rotate left to start goes to end`() {
        val circularList = CircularList<Int>()
        circularList.addAll(listOf(1, 2, 3))
        circularList.rotate(2, -2)
        assert(circularList[0] == 1) { circularList }
        assert(circularList[1] == 2) { circularList }
        assert(circularList[2] == 3) { circularList }
    }

    @Test
    fun `test circular list rotate left with wrap`() {
        val circularList = CircularList<Int>()
        circularList.addAll(listOf(1, 2, 3))
        circularList.rotate(2, -3)
        assert(circularList[0] == 1) { circularList }
        assert(circularList[1] == 3) { circularList }
        assert(circularList[2] == 2) { circularList }
    }
}
