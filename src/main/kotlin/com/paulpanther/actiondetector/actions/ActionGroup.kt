package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.tree.Tree

data class ActionGroup(
    val title: String,
    val actions: List<Action>
)


interface ActionGrouper {
    fun groupActions(actions: List<Action>): List<ActionGroup>
}

class ClassContentActionGrouper: ActionGrouper {
    override fun groupActions(actions: List<Action>): List<ActionGroup> {
        return actions
            .groupBy { action -> action.node.parents.findLast { it.isClassDeclaration() }?.className }
            .map { (className, actionsInClass) -> ActionGroup("class $className", actionsInClass) }
    }

    private fun Tree.isClassDeclaration() = type.name == "type_declaration" && children.any { child ->
        child.type.name == "type_keyword" && child.label == "class"
    }

    private val Tree.className: String? get() = children.find { it.type.name == "identifier" }?.label
}
