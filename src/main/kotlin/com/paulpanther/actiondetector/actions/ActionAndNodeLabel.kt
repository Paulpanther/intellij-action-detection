package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.actions.model.Delete
import com.github.gumtreediff.actions.model.Insert
import com.github.gumtreediff.actions.model.Move
import com.github.gumtreediff.actions.model.TreeDelete
import com.github.gumtreediff.actions.model.TreeInsert
import com.github.gumtreediff.actions.model.Update
import com.github.gumtreediff.tree.Tree
import com.paulpanther.actiondetector.nullIfEmpty

fun Action.toDisplayAction() = DisplayAction(this, ActionLabel(this).serialize())

class ActionLabel(
    private val action: Action
) {
    fun serialize(): String {
        return when (action) {
            is Insert, is TreeInsert -> "Insert ${action.node.serialize(parentConnector = "into")}"
            is Delete, is TreeDelete -> "Remove ${action.node.serialize(parentConnector = "from")}"
            is Move -> "Move ${action.node.serialize(false)} to ${action.parent.serialize(false)}"
            is Update -> "Rename ${action.node.serialize()} to \"${action.value}\""
            else -> action.displayName
        }
    }
}

fun Tree.serialize(
    withParent: Boolean = true,
    parentConnector: String = "in"
) = NodeLabel(this).serialize(withParent, parentConnector)

class NodeLabel(
    private val node: Tree
) {
    fun serialize(withParent: Boolean, parentConnector: String): String {
        val parentStr = if (withParent) serializeParent()?.let { " $parentConnector $it" } ?: "" else ""
        val nodeStr: String = when (node.type.name) {
            "modifiers" -> serializeModifiers()
            "field_declaration" -> serializeFieldDeclaration()
            "local_variable_declaration" -> serializeLocalVariableDeclaration()
            "expression_statement" -> serializeExpressionStatement()
            "method_declaration" -> serializeMethodDeclaration()
            "type_body" -> serializeParent() ?: "orphan body"
            "block" -> serializeParent() ?: "orphan block"
            "type_declaration" -> serializeTypeDeclaration()
            else -> node.type.toString()
        }
        return "$nodeStr$parentStr"
    }

    private fun serializeMethodDeclaration(): String {
        val identifier = node.findChildOfType("identifier")?.label

        return if (identifier == null) {
            "unnamed method"
        } else {
            "method \"$identifier\""
        }
    }

    private fun serializeExpressionStatement(): String {
        val child = node.children.firstOrNull()
        return when (child?.type?.name) {
            "method_invocation" -> "method invocation"
            else -> "expression statement"
        }
    }

    private fun serializeModifiers(): String {
        val label = node.children[0]?.label
        val labelStr = label?.let { " $it" } ?: ""
        return "modifier$labelStr"
    }

    private fun serializeFieldDeclaration(): String {
        val type = node.findChildOfType(Regex("(\\w*_)?type"))?.label?.nullIfEmpty()
        val label = node.findChildOfType("variable_declarator", "identifier")?.label

        val labelStr = if (label == null) "unnamed field" else "field \"$label\""
        val typeStr = type?.let { " with type $it" } ?: ""
        return "$labelStr$typeStr"
    }

    private fun serializeLocalVariableDeclaration(): String {
        val type = node.findChildOfType("type")?.label?.nullIfEmpty()
        val label = node.findChildOfType("variable_declarator", "identifier")?.label

        val labelStr = if (label == null) "unnamed variable" else "variable \"$label\""
        val typeStr = type?.let { " with type $it" } ?: ""
        return "$labelStr$typeStr"
    }

    private fun serializeParent(): String? {
        return node.parent?.serialize(false)
    }

    private fun serializeTypeDeclaration(): String {
        val keyword = node.findChildOfType("type_keyword")?.label
        val identifier = node.findChildOfType("identifier")?.label

        return if (keyword == null && identifier == null)  {
            "unnamed declaration"
        } else if (keyword == null){
            "type \"$identifier\""
        } else if (identifier == null) {
            "unnamed $keyword"
        } else {
            "$keyword \"$identifier\""
        }
    }
}
