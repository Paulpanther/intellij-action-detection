package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.tree.Tree

val Action.displayName: String get() = "$name ${node.displayName}"

private val Tree.displayName: String get() = toString()
    .substringBefore("[")
    .replace("_", " ")

fun Action.similarTo(other: Action): Boolean {
    TODO()
}

fun List<Action>.similarTo(other: List<Action>): Boolean {
    return zip(other).all { (a1, a2) -> a1.similarTo(a2) }
}
