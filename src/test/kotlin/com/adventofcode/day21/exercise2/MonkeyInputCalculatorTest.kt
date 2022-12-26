package com.adventofcode.day21.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.nio.charset.StandardCharsets

class MonkeyInputCalculatorTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            MonkeyInputCalculatorTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    findHumanInput(reader)
                }
            }
        assertThat(result).isEqualTo(BigInteger.valueOf(301))
    }
}
