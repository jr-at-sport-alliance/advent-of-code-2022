package com.adventofcode.day21.exercise2

import com.adventofcode.day21.exercise2.Operator.ADD
import com.adventofcode.day21.exercise2.Operator.DIVIDE
import com.adventofcode.day21.exercise2.Operator.MULTIPLY
import com.adventofcode.day21.exercise2.Operator.SUBTRACT
import java.io.Reader
import java.lang.System.lineSeparator
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import kotlin.math.pow

fun main() {
    val result = Resources::class.java.getResourceAsStream("input.txt").use { inputStream ->
        inputStream!!.reader(StandardCharsets.UTF_8).use { reader ->
            findHumanInput(reader)
        }
    }
    println(result)
}

object Resources

val LINE_REGEX = """([a-z]{4}):\s*(?:([a-z]{4})\s*([+\-*/])\s*([a-z]{4})|(\d+))""".toRegex()

fun findHumanInput(reader: Reader): BigInteger {
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
    // println(drawTree(resolvedNodes["root"]!!))
    val rebuilt = rebuildTree(resolvedNodes["root"]!!)
    // println(drawTree(rebuilt))
    return rebuilt()
}

fun rebuildTree(root: ResolvedNode): ResolvedNode {
    require(root is ResolvedCalculationNode)
    val pathToHuman = walkTreeToHuman(root)
    val newRoot = IdentityNode(root.name, if (pathToHuman[0] == root.lhs) root.lhs else root.rhs)
    return rewriteTree(newRoot, newRoot, root, pathToHuman.drop(1))
}

fun walkTreeToHuman(node: ResolvedNode): List<ResolvedNode> {
    return if (node is ResolvedCalculationNode) {
        sequenceOf(node.lhs, node.rhs).map(::walkTreeToHuman).firstOrNull() { it.isNotEmpty() }?.withPrefix(node)
            ?: emptyList()
    } else if (node.name == "humn") listOf(node) else emptyList()
}

fun rewriteTree(
    newRoot: ResolvedNode,
    newPreviousNode: ResolvedNode,
    oldPreviousNode: ResolvedCalculationNode,
    remainingNodes: List<ResolvedNode>
): ResolvedNode {
    if (remainingNodes.isEmpty()) return newPreviousNode

    val current = remainingNodes[0]

    val newCurrent =
        if (newRoot == newPreviousNode) IdentityNode(current.name, newPreviousNode)
        else when (oldPreviousNode.operator) {
            ADD -> ResolvedCalculationNode(
                current.name,
                newPreviousNode,
                if (oldPreviousNode.lhs == current) oldPreviousNode.rhs else oldPreviousNode.lhs,
                SUBTRACT
            )
            SUBTRACT -> if (oldPreviousNode.lhs == current) {
                ResolvedCalculationNode(
                    current.name,
                    newPreviousNode,
                    oldPreviousNode.rhs,
                    ADD
                )
            } else {
                ResolvedCalculationNode(
                    current.name,
                    oldPreviousNode.lhs,
                    newPreviousNode,
                    SUBTRACT
                )
            }
            MULTIPLY -> ResolvedCalculationNode(
                current.name,
                newPreviousNode,
                if (oldPreviousNode.lhs == current) oldPreviousNode.rhs else oldPreviousNode.lhs,
                DIVIDE
            )
            DIVIDE -> if (oldPreviousNode.lhs == current) {
                ResolvedCalculationNode(
                    current.name,
                    newPreviousNode,
                    oldPreviousNode.rhs,
                    MULTIPLY
                )
            } else {
                ResolvedCalculationNode(
                    current.name,
                    oldPreviousNode.lhs,
                    newPreviousNode,
                    DIVIDE
                )
            }
        }

    if (current !is ResolvedCalculationNode) {
        require(remainingNodes.size == 1)
        return newCurrent
    }

    return rewriteTree(newRoot, newCurrent, current, remainingNodes.drop(1))
}

sealed interface Node {
    val name: String
}

fun <E> List<E>.withPrefix(e: E) = listOf(e) + this

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
    val lhs: ResolvedNode,
    val rhs: ResolvedNode,
    val operator: Operator
) : ResolvedNode {
    override fun invoke() = operator(lhs(), rhs())
}

class IdentityNode(
    override val name: String,
    val child: ResolvedNode
) : ResolvedNode {
    override fun invoke(): BigInteger = child()
}

fun drawTree(node: ResolvedNode): String {
    val layers = layers(listOf(listOf(node)))
    var width = 2.0.pow(layers.size.toDouble()).toInt() - 1
    return layers.joinToString(separator = lineSeparator()) { layer ->
        val prevWidth = width
        width /= 2
        layer.joinToString(
            separator = nSpaces(4 * prevWidth),
            prefix = nSpaces(4 * width),
            postfix = nSpaces(4 * width)
        ) { it?.name ?: "    " } +
            (
                lineSeparator() + layer.joinToString(
                    separator = nSpaces(4 * prevWidth),
                    prefix = nSpaces(4 * width),
                    postfix = nSpaces(4 * width)
                ) {
                    when (it) {
                        is ResolvedCalculationNode -> operatorToDrawString(it.operator)
                        is IdentityNode -> " =  "
                        else -> "    "
                    }
                }.ifBlank { "" }
                )
    }
}

fun operatorToDrawString(operator: Operator) = when (operator) {
    ADD -> " +  "
    SUBTRACT -> " -  "
    MULTIPLY -> " *  "
    DIVIDE -> " /  "
}

fun nSpaces(n: Int) = (0 until n).joinToString(separator = "") { " " }

fun layers(currentLayers: List<List<ResolvedNode?>>): List<List<ResolvedNode?>> {
    val nextLayer = currentLayers.last().map {
        when (it) {
            is ResolvedCalculationNode -> it.lhs to it.rhs
            is IdentityNode -> null to it.child
            else -> null to null
        }.toList()
    }.flatten()
    return if (nextLayer.all { it == null }) currentLayers else currentLayers + layers(listOf(nextLayer))
}
