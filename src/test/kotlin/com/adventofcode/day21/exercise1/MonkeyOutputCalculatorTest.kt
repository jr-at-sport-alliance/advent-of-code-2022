package com.adventofcode.day21.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.nio.charset.StandardCharsets

class MonkeyOutputCalculatorTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            MonkeyOutputCalculatorTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findRootMonkeyOutput(reader)
                }
            }
        assertThat(result).isEqualTo(BigInteger.valueOf(152))
    }
}
