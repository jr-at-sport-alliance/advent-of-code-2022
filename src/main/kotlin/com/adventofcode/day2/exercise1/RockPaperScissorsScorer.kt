package com.adventofcode.day2.exercise1

import com.adventofcode.day2.exercise1.Result.DRAW
import com.adventofcode.day2.exercise1.Result.LOSS
import com.adventofcode.day2.exercise1.Result.WIN
import com.adventofcode.day2.exercise1.RockPaperScissors.Companion.fromSymbol
import com.adventofcode.day2.exercise1.RockPaperScissors.PAPER
import com.adventofcode.day2.exercise1.RockPaperScissors.ROCK
import com.adventofcode.day2.exercise1.RockPaperScissors.SCISSORS
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
            val opponentChoice = fromSymbol(line.substring(0, 1))
            val ownChoice = fromSymbol(line.substring(2, 3))
            val result = determineResult(ownChoice, opponentChoice)
            val score = result.value + ownChoice.value
            score
        }.sum()
    }
}

private fun determineResult(ownChoice: RockPaperScissors, opponentChoice: RockPaperScissors) =
    if (ownChoice == opponentChoice) DRAW
    else when (ownChoice) {
        ROCK -> if (opponentChoice == SCISSORS) WIN else LOSS
        PAPER -> if (opponentChoice == ROCK) WIN else LOSS
        SCISSORS -> if (opponentChoice == PAPER) WIN else LOSS
    }

@SuppressWarnings("MagicNumber")
private enum class Result(val value: Int) {
    LOSS(0), DRAW(3), WIN(6)
}

@SuppressWarnings("MagicNumber")
private enum class RockPaperScissors(val value: Int) {
    ROCK(1), PAPER(2), SCISSORS(3);

    companion object {
        fun fromSymbol(symbol: String): RockPaperScissors =
            when (symbol) {
                "A", "X" -> ROCK
                "B", "Y" -> PAPER
                "C", "Z" -> SCISSORS
                else -> error("Unknown input: $symbol")
            }
    }
}
