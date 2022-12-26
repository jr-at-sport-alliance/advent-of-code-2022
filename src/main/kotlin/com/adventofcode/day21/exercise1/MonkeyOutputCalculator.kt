package com.adventofcode.day21.exercise1

import java.io.Reader
import java.math.BigInteger
import java.nio.charset.StandardCharsets

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            findRootMonkeyOutput(reader)
        }
    }
    println(result)
}

object Resources

val LINE_REGEX = """([a-z]{4}):\s*(?:([a-z]{4})\s*([+\-*/])\s*([a-z]{4})|(\d+))""".toRegex()

fun findRootMonkeyOutput(reader: Reader): BigInteger {
    val unresolvedNodes = reader.useLines { lines ->
        lines.map { line ->
            with(LINE_REGEX.matchEntire(line)!!) {
                if (groupValues[5].isNotEmpty()) ConstantNode(
                    name = groupValues[1],
                    value = BigInteger(groupValues[5])
                )
                else UnresolvedCalculationNode(
                    name = groupValues[1],
                    lhs = groupValues[2],
                    operator = Operator.fromString(groupValues[3]),
                    rhs = groupValues[4]
                )
            }
        }.associateBy(Node::name).toMutableMap()
    }
    val resolvedNodes = mutableMapOf<String, ResolvedNode>()
    while (unresolvedNodes.isNotEmpty()) {
        val iterator = unresolvedNodes.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            if (value is ResolvedNode) {
                resolvedNodes[key] = value
                iterator.remove()
            } else {
                require(value is UnresolvedCalculationNode)
                val maybeLhs = resolvedNodes[value.lhs]
                val maybeRhs = resolvedNodes[value.rhs]
                if (maybeLhs != null && maybeRhs != null) {
                    resolvedNodes[key] = value.resolve(maybeLhs, maybeRhs)
                    iterator.remove()
                }
            }
        }
    }
    return resolvedNodes["root"]!!()
}

sealed interface Node {
    val name: String
}

sealed interface ResolvedNode : Node, () -> BigInteger

class ConstantNode(override val name: String, val value: BigInteger) : ResolvedNode {
    override fun invoke() = value
}

enum class Operator(
    private val operator: (BigInteger, BigInteger) -> BigInteger
) : (BigInteger, BigInteger) -> BigInteger by operator {
    ADD({ a, b -> a + b }),
    SUBTRACT({ a, b -> a - b }),
    MULTIPLY({ a, b -> a * b }),
    DIVIDE({ a, b -> a / b }),
    ;

    companion object {
        fun fromString(string: String) = when (string) {
            "+" -> ADD
            "-" -> SUBTRACT
            "*" -> MULTIPLY
            "/" -> DIVIDE
            else -> error("$string is not an operator")
        }
    }
}

class UnresolvedCalculationNode(
    override val name: String,
    val lhs: String,
    val rhs: String,
    val operator: Operator
) : Node {
    fun resolve(lhs: ResolvedNode, rhs: ResolvedNode) = ResolvedCalculationNode(name, lhs, rhs, operator)
}

class ResolvedCalculationNode(
    override val name: String,
    private val lhs: ResolvedNode,
    private val rhs: ResolvedNode,
    val operator: Operator
) : ResolvedNode {
    override fun invoke() = operator(lhs(), rhs())
}
