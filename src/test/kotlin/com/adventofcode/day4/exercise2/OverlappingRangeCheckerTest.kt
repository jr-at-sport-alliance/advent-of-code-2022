package com.adventofcode.day4.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class OverlappingRangeCheckerTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            OverlappingRangeCheckerTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    countOverlappingRanges(reader)
                }
            }
        assertThat(result).isEqualTo(4)
    }
}
