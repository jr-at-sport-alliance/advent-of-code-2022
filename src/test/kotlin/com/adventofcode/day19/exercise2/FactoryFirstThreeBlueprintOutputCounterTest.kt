package com.adventofcode.day19.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class FactoryFirstThreeBlueprintOutputCounterTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            FactoryFirstThreeBlueprintOutputCounterTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    countFactoryBlueprintOutput(reader)
                }
            }
        assertThat(result).isEqualTo(56 * 62)
    }
}
