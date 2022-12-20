package com.adventofcode.day19.exercise2

import com.adventofcode.day19.exercise2.RobotType.CLAY
import com.adventofcode.day19.exercise2.RobotType.GEODE
import com.adventofcode.day19.exercise2.RobotType.OBSIDIAN
import com.adventofcode.day19.exercise2.RobotType.ORE
import java.io.Reader
import java.lang.Integer.max
import java.lang.System.lineSeparator
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            countFactoryBlueprintOutput(reader)
        }
    }
    println(result)
}

object Resources

val BLUEPRINT_REGEX =
    """
        Blueprint (\d+):\s+
        Each ore robot costs (\d+) ore\.\s+
        Each clay robot costs (\d+) ore\.\s+
        Each obsidian robot costs (\d+) ore and (\d+) clay\.\s+
        Each geode robot costs (\d+) ore and (\d+) obsidian\.
    """.trimIndent().replace(lineSeparator(), "").toRegex()

fun countFactoryBlueprintOutput(reader: Reader): Int {
    return reader.useLines { lines ->
        lines.take(3).map { line ->
            BLUEPRINT_REGEX.matchEntire(line)?.let {
                Blueprint(
                    id = it.groupValues[1].toInt(),
                    oreRobotOreCost = it.groupValues[2].toInt(),
                    clayRobotOreCost = it.groupValues[3].toInt(),
                    obsidianRobotOreCost = it.groupValues[4].toInt(),
                    obsidianRobotClayCost = it.groupValues[5].toInt(),
                    geodeRobotOreCost = it.groupValues[6].toInt(),
                    geodeRobotObsidianCost = it.groupValues[7].toInt()
                )
            } ?: error("$line did not match blueprint regex")
        }.map(::calculateGeodesMinedForBlueprintViaDecisionTree).reduce { a, b -> a * b }
    }
}

enum class RobotType { ORE, CLAY, OBSIDIAN, GEODE }

fun maxPossibleGeodesHeuristic(
    currentGeodes: Int,
    currentGeodeRobots: Int,
    currentObsidian: Int,
    currentObsidianRobots: Int,
    geodeRobotObsidianCost: Int,
    timeRemaining: Int
): Int {
    return currentGeodes +
        timeRemaining * currentGeodeRobots +
        timeRemaining * Integer.min(
        timeRemaining,
        maxPossibleObsidianHeuristic(
            currentObsidian,
            currentObsidianRobots,
            timeRemaining
        ) / geodeRobotObsidianCost
    ) / 2
}

fun maxPossibleObsidianHeuristic(
    currentObsidian: Int,
    currentObsidianRobots: Int,
    timeRemaining: Int
): Int {
    return currentObsidian +
        timeRemaining * currentObsidianRobots +
        timeRemaining * timeRemaining / 2
}

fun calculateGeodesMinedForBlueprintViaDecisionTree(
    blueprint: Blueprint,
    ore: Int = 0,
    clay: Int = 0,
    obsidian: Int = 0,
    geodes: Int = 0,
    oreRobots: Int = 1,
    clayRobots: Int = 0,
    obsidianRobots: Int = 0,
    geodeRobots: Int = 0,
    nextRobotType: RobotType? = null,
    currentBestGeodes: AtomicInteger = AtomicInteger(0),
    minutesRemaining: Int = 32
): Int {
    currentBestGeodes.getAndUpdate { max(geodes, it) }

    if (minutesRemaining < 1) {
        return geodes
    }

    // try to eliminate branches worse than current best
    if (maxPossibleGeodesHeuristic(
            geodes,
            geodeRobots,
            obsidian,
            obsidianRobots,
            blueprint.geodeRobotObsidianCost,
            minutesRemaining
        ) <= currentBestGeodes.get()
    ) {
        return 0
    }

    if (nextRobotType == null) {
        val nextRobotTypeChoices = mutableListOf<RobotType>()
        if (obsidianRobots > 0) nextRobotTypeChoices.add(GEODE)
        if (clayRobots > 0 && obsidianRobots < blueprint.geodeRobotObsidianCost) nextRobotTypeChoices.add(OBSIDIAN)
        if (clayRobots < blueprint.obsidianRobotClayCost) nextRobotTypeChoices.add(CLAY)
        if (oreRobots < blueprint.maxOreCost) nextRobotTypeChoices.add(ORE)

        return nextRobotTypeChoices.maxOfOrNull {
            calculateGeodesMinedForBlueprintViaDecisionTree(
                blueprint,
                ore,
                clay,
                obsidian,
                geodes,
                oreRobots,
                clayRobots,
                obsidianRobots,
                geodeRobots,
                it,
                currentBestGeodes,
                minutesRemaining
            )
        }!!
    }

    when (nextRobotType) {
        ORE -> {
            if (ore >= blueprint.oreRobotOreCost) {
                return calculateGeodesMinedForBlueprintViaDecisionTree(
                    blueprint,
                    ore + oreRobots - blueprint.oreRobotOreCost,
                    clay + clayRobots,
                    obsidian + obsidianRobots,
                    geodes + geodeRobots,
                    oreRobots + 1,
                    clayRobots,
                    obsidianRobots,
                    geodeRobots,
                    nextRobotType = null,
                    currentBestGeodes,
                    minutesRemaining - 1
                )
            }
        }
        CLAY -> {
            if (ore >= blueprint.clayRobotOreCost) {
                return calculateGeodesMinedForBlueprintViaDecisionTree(
                    blueprint,
                    ore + oreRobots - blueprint.clayRobotOreCost,
                    clay + clayRobots,
                    obsidian + obsidianRobots,
                    geodes + geodeRobots,
                    oreRobots,
                    clayRobots + 1,
                    obsidianRobots,
                    geodeRobots,
                    nextRobotType = null,
                    currentBestGeodes,
                    minutesRemaining - 1
                )
            }
        }
        OBSIDIAN -> {
            if (ore >= blueprint.obsidianRobotOreCost && clay >= blueprint.obsidianRobotClayCost) {
                return calculateGeodesMinedForBlueprintViaDecisionTree(
                    blueprint,
                    ore + oreRobots - blueprint.obsidianRobotOreCost,
                    clay + clayRobots - blueprint.obsidianRobotClayCost,
                    obsidian + obsidianRobots,
                    geodes + geodeRobots,
                    oreRobots,
                    clayRobots,
                    obsidianRobots + 1,
                    geodeRobots,
                    nextRobotType = null,
                    currentBestGeodes,
                    minutesRemaining - 1
                )
            }
        }
        GEODE -> {
            if (ore >= blueprint.geodeRobotOreCost && obsidian >= blueprint.geodeRobotObsidianCost) {
                return calculateGeodesMinedForBlueprintViaDecisionTree(
                    blueprint,
                    ore + oreRobots - blueprint.geodeRobotOreCost,
                    clay + clayRobots,
                    obsidian + obsidianRobots - blueprint.geodeRobotObsidianCost,
                    geodes + geodeRobots,
                    oreRobots,
                    clayRobots,
                    obsidianRobots,
                    geodeRobots + 1,
                    nextRobotType = null,
                    currentBestGeodes,
                    minutesRemaining - 1
                )
            }
        }
    }

    return calculateGeodesMinedForBlueprintViaDecisionTree(
        blueprint,
        ore + oreRobots,
        clay + clayRobots,
        obsidian + obsidianRobots,
        geodes + geodeRobots,
        oreRobots,
        clayRobots,
        obsidianRobots,
        geodeRobots,
        nextRobotType,
        currentBestGeodes,
        minutesRemaining - 1
    )
}

data class Blueprint(
    val id: Int,
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int
) {
    val maxOreCost = max(max(oreRobotOreCost, clayRobotOreCost), max(obsidianRobotOreCost, geodeRobotOreCost))
}
