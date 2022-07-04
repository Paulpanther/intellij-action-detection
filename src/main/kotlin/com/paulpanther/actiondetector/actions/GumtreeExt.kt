package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.tree.Tree
import com.intellij.openapi.util.TextRange
import java.io.File

val Action.displayName: String get() = "$name ${node.displayName}"

private val Tree.displayName: String get() = toString()
    .substringBefore("[")
    .replace("_", " ")

fun Action.similarTo(other: Action): Boolean {
    return this::class == other::class && node.type == other.node.type
}

fun List<Action>.similarTo(other: List<Action>): Boolean {
    return size == other.size && zip(other).all { (a1, a2) -> a1.similarTo(a2) }
}

val Tree.range get() = TextRange(pos, endPos)

fun Tree.text(file: File) = file.readText().substring(range)

fun String.substring(range: TextRange) = range.substring(this)

fun Tree.findChildOfType(vararg types: String) = findChildOfType(types.map { it.toRegex() })

fun Tree.findChildOfType(vararg types: Regex) = findChildOfType(types.toList())

fun Tree.findChildOfType(types: List<Regex>): Tree? {
    val next = children.find { it.type.name.matches(types.first()) }
    return if (types.size == 1) {
        next
    } else {
        next?.findChildOfType(types.slice(1 until types.size))
    }
}
