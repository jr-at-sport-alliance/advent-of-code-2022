package com.adventofcode.day22.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class GrovePathOnCubeFinderTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            GrovePathOnCubeFinderTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findFinalPositionAndFacing(reader)
                }
            }
        assertThat(result).isEqualTo(5031)
    }

    @Test
    fun `should multiple matrices correctly`() {
        val a = listOf(
            listOf(4, 1, 5, 2),
            listOf(3, -1, -3, 0),
            listOf(6, -2, 7, 8),
            listOf(9, 10, 11, 12)
        )
        val b = listOf(
            listOf(13, 1),
            listOf(3, 14),
            listOf(-1, 4),
            listOf(6, 9)
        )
        val c = listOf(
            listOf(62, 56),
            listOf(39, -23),
            listOf(113, 78),
            listOf(208, 301)
        )

        assertThat(a * b).isEqualTo(c)
    }

    @Test
    fun `should rotate matrices correctly`() {
        val a = Triple(
            Triple(1, 0, 0),
            Triple(0, 1, 0),
            Triple(0, 0, 1)
        )
        val aRotated = rotate(a, Triple(0, 1, 0))
        assertThat(aRotated).isEqualTo(
            Triple(
                Triple(0, 0, 1),
                Triple(0, 1, 0),
                Triple(-1, 0, 0)
            )
        )
    }

    @Test
    fun `should rotate elements correctly`() {
        val elements = listOf(
            listOf(1, 2, 3, 4),
            listOf(5, 6, 7, 8)
        )
        assertThat(elements.rotateElements(1)).isEqualTo(
            listOf(
                listOf(5, 1),
                listOf(6, 2),
                listOf(7, 3),
                listOf(8, 4)
            )
        )
    }
}
