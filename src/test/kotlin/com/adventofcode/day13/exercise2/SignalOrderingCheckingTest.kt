package com.adventofcode.day13.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class SignalOrderingCheckingTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            SignalOrderingCheckingTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findDecoderKey(reader)
                }
            }
        assertThat(result).isEqualTo(140)
    }
}
