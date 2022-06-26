package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.actions.model.Addition
import com.github.gumtreediff.actions.model.Delete
import com.github.gumtreediff.actions.model.Insert
import com.github.gumtreediff.actions.model.TreeDelete
import com.github.gumtreediff.actions.model.TreeInsert
import com.github.gumtreediff.tree.Tree

data class ActionDetail(
    val label: String?,
    val code: String,
)

sealed interface DisplayAction {
    val title: String
    val details: List<ActionDetail>
}

class Add(node: Tree, parent: Tree, pos: Int) : Addition(node, parent, pos), DisplayAction {
    constructor(action: Insert): this(action.node, action.parent, action.position)
    constructor(action: TreeInsert): this(action.node, action.parent, action.position)

    override fun getName(): String = "Add"

    companion object {
        fun from(action: Action): Add = when(action) {
            is Insert -> Add(action)
            is TreeInsert -> Add(action)
            else -> throw IllegalStateException()
        }
    }

    override val title: String
        get() = name
    override val details: List<ActionDetail>
        get() = TODO("Not yet implemented")
}

class Remove(node: Tree) : Action(node), DisplayAction {
    constructor(action: Delete): this(action.node)
    constructor(action: TreeDelete): this(action.node)

    override fun getName(): String = "Remove"

    companion object {
        fun from(action: Action): Remove = when(action) {
            is Delete -> Remove(action)
            is TreeDelete -> Remove(action)
            else -> throw IllegalStateException()
        }
    }

    override val title: String
        get() = name
    override val details: List<ActionDetail>
        get() = TODO("Not yet implemented")
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

    override val title: String
        get() = name
    override val details: List<ActionDetail>
        get() = TODO("Not yet implemented")
}

// TODO: Add cool aggregated actions like Replace, Extract, etc.
