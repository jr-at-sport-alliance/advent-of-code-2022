package com.adventofcode.day3.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class BackpackDuplicateFinderTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            BackpackDuplicateFinderTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    determineBackpackDuplicates(reader)
                }
            }
        assertThat(result).isEqualTo(157)
    }
}
