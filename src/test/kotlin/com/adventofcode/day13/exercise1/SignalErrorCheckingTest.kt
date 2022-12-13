package com.adventofcode.day13.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class SignalErrorCheckingTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            SignalErrorCheckingTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    countCorrectSignals(reader)
                }
            }
        assertThat(result).isEqualTo(13)
    }
}
