package com.adventofcode.day5.exercise2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class SupplyStackSolverTest {

    @Test
    fun `for example input, should return example result`() {
        val result =
            SupplyStackSolverTest::class.java.getResourceAsStream("test_input.txt").use { inputStream ->
                inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                    solveForTopCrates(reader)
                }
            }
        assertThat(result).isEqualTo("MCD")
    }
}
