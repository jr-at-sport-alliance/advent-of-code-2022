package com.adventofcode.day4.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class FullyContainedRangeCheckerTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            FullyContainedRangeCheckerTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    countFullyContainedRanges(reader)
                }
            }
        assertThat(result).isEqualTo(2)
    }
}
