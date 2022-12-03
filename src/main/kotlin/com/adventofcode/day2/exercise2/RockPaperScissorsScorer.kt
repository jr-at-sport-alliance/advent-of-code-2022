package com.adventofcode.day2.exercise2

import com.adventofcode.day2.exercise2.Result.Companion.resultFromSymbol
import com.adventofcode.day2.exercise2.Result.DRAW
import com.adventofcode.day2.exercise2.Result.WIN
import com.adventofcode.day2.exercise2.RockPaperScissors.Companion.rockPaperScissorsFromSymbol
import com.adventofcode.day2.exercise2.RockPaperScissors.PAPER
import com.adventofcode.day2.exercise2.RockPaperScissors.ROCK
import com.adventofcode.day2.exercise2.RockPaperScissors.SCISSORS
import java.io.Reader
import java.nio.charset.StandardCharsets

fun main() {
    val result =
        Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
            inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
                determineRockPaperScissorsScore(reader)
            }
        }
    println(result)
}

internal object Resources

@SuppressWarnings("MagicNumber")
fun determineRockPaperScissorsScore(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.map { line ->
            val opponentChoice = rockPaperScissorsFromSymbol(line.substring(0, 1))
            val result = resultFromSymbol(line.substring(2, 3))
            val ownChoice = determineOwnChoice(opponentChoice, result)
            val score = result.value + ownChoice.value
            score
        }.sum()
    }
}

private fun determineOwnChoice(opponentChoice: RockPaperScissors, desiredResult: Result) =
    if (desiredResult == DRAW) opponentChoice
    else when (opponentChoice) {
        ROCK -> if (desiredResult == WIN) PAPER else SCISSORS
        PAPER -> if (desiredResult == WIN) SCISSORS else ROCK
        SCISSORS -> if (desiredResult == WIN) ROCK else PAPER
    }

@SuppressWarnings("MagicNumber")
private enum class Result(val value: Int) {
    LOSS(0), DRAW(3), WIN(6);

    companion object {
        fun resultFromSymbol(symbol: String): Result =
            when (symbol) {
                "X" -> LOSS
                "Y" -> DRAW
                "Z" -> WIN
                else -> error("Unknown input: $symbol")
            }
    }
}

@SuppressWarnings("MagicNumber")
private enum class RockPaperScissors(val value: Int) {
    ROCK(1), PAPER(2), SCISSORS(3);

    companion object {
        fun rockPaperScissorsFromSymbol(symbol: String): RockPaperScissors =
            when (symbol) {
                "A" -> ROCK
                "B" -> PAPER
                "C" -> SCISSORS
                else -> error("Unknown input: $symbol")
            }
    }
}
