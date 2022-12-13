package com.adventofcode.day12.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class HillClimbingTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            HillClimbingTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    getHillClimbingSteps(reader)
                }
            }
        assertThat(result).isEqualTo(31)
    }
}
