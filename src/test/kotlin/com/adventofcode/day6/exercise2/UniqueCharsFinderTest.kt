package com.adventofcode.day6.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class UniqueCharsFinderTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            UniqueCharsFinderTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findFourteenUniqueCharsEndIndex(reader)
                }
            }
        assertThat(result).isEqualTo(29)
    }
}
