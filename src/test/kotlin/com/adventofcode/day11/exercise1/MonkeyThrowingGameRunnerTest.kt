package com.adventofcode.day11.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class MonkeyThrowingGameRunnerTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            MonkeyThrowingGameRunnerTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    runMonkeyThrowingGame(reader)
                }
            }
        assertThat(result).isEqualTo(10605)
    }
}
