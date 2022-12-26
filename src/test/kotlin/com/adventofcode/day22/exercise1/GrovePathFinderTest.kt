package com.adventofcode.day22.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class GrovePathFinderTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            GrovePathFinderTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findFinalPositionAndFacing(reader)
                }
            }
        assertThat(result).isEqualTo(6032)
    }
}
