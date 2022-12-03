package com.adventofcode.day2.exercise1

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class RockPaperScissorsScorerTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            RockPaperScissorsScorerTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    determineRockPaperScissorsScore(reader)
                }
            }
        Assertions.assertThat(result).isEqualTo(15)
    }
}
