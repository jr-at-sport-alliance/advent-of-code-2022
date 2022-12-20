package com.adventofcode.day19.exercise1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class FactoryBlueprintRaterTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            FactoryBlueprintRaterTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    rateFactoryBlueprints(reader)
                }
            }
        assertThat(result).isEqualTo(33)
    }
}
