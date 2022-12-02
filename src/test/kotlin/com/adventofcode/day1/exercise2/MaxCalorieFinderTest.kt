package com.adventofcode.day1.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class MaxCalorieFinderTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            MaxCalorieFinderTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findMaxCalories(reader)
                }
            }
        assertThat(result).isEqualTo(24000 + 11000 + 10000)
    }
}
