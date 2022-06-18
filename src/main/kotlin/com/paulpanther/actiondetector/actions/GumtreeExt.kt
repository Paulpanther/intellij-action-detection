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
