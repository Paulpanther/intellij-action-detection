package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.actions.model.Addition
import com.github.gumtreediff.actions.model.Delete
import com.github.gumtreediff.actions.model.Insert
import com.github.gumtreediff.actions.model.TreeDelete
import com.github.gumtreediff.actions.model.TreeInsert
import com.github.gumtreediff.tree.Tree

data class ActionDetail(
    val code: String,
    val label: String? = null,
)

sealed interface DisplayAction {
    val title: String
}

class Add(node: Tree, parent: Tree, pos: Int, private val source: String? = null) : Addition(node, parent, pos), DisplayAction {
    constructor(action: Insert, source: String? = null): this(action.node, action.parent, action.position, source)
    constructor(action: TreeInsert, source: String? = null): this(action.node, action.parent, action.position, source)

    override fun getName(): String = "Add"

    companion object {
        fun from(action: Action, source: String? = null): Add = when(action) {
            is Insert -> Add(action, source)
            is TreeInsert -> Add(action, source)
            else -> throw IllegalStateException()
        }
    }

    override val title = "Insert ${NodeLabel(node).serialize()}"
}

class Remove(node: Tree, private val source: String? = null) : Action(node), DisplayAction {
    constructor(action: Delete, source: String? = null) : this(action.node, source)
    constructor(action: TreeDelete, source: String? = null) : this(action.node, source)

    override fun getName(): String = "Remove"

    companion object {
        fun from(action: Action, source: String? = null): Remove = when (action) {
            is Delete -> Remove(action, source)
            is TreeDelete -> Remove(action, source)
            else -> throw IllegalStateException()
        }
    }

    override val title by ::name
}

typealias TreeMove = com.github.gumtreediff.actions.model.Move
class Move(node: Tree, parent: Tree, pos: Int) : Addition(node, parent, pos), DisplayAction {
    constructor(action: TreeMove): this(action.node, action.parent, action.position)
    override fun getName(): String = "Move"

    companion object {
        fun from(action: Action): Move = when(action) {
            is TreeMove -> Move(action)
            else -> throw IllegalStateException()
        }
    }

    override val title by ::name
}

// TODO: Add cool aggregated actions like Replace, Extract, etc.
