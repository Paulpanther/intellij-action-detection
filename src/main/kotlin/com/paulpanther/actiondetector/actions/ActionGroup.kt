package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.tree.Tree

sealed class ActionGroup(
    val title: String)

class ActionLeafGroup(
    title: String,
    val actions: List<Action>
): ActionGroup(title)

class ActionGroupGroup(
    title: String,
    val groups: List<ActionGroup>
): ActionGroup(title)

interface ActionGrouper {
    fun groupActions(actions: List<Action>): List<ActionGroup>
}

object AllActionGroupers: ActionGrouper {
    override fun groupActions(actions: List<Action>) =
        ClassContentActionGrouper.groupActions(actions) +
                ConnectedChangesGrouper.groupActions(actions)
}

// TODO ues https://github.com/tree-sitter/tree-sitter-typescript/blob/master/queries/tags.scm
// TODO https://github.com/tree-sitter/tree-sitter-javascript/blob/master/queries/locals.scm
object ClassContentActionGrouper: ActionGrouper {
    override fun groupActions(actions: List<Action>): List<ActionGroup> {
        return actions
            .groupBy { action -> action.node.parents.findLast { it.isClassDeclaration() }?.className }
            .map { (className, actionsInClass) -> ActionLeafGroup("class $className", actionsInClass) }
    }

    private fun Tree.isClassDeclaration() = type.name == "type_declaration" && children.any { child ->
        child.type.name == "type_keyword" && child.label == "class"
    }

    private val Tree.className: String? get() = children.find { it.type.name == "identifier" }?.label
}
