package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.tree.Tree
import com.paulpanther.actiondetector.nullIfEmpty

class NodeLabel(
    private val node: Tree
) {
    fun serialize(): String {
        return when (node.type.name) {
            "field_declaration" -> serializeFieldDeclaration()
            "local_variable_declaration" -> serializeLocalVariableDeclaration()
            else -> node.toString()
        }
    }

    private fun serializeFieldDeclaration(): String {
        val type = node.findChildOfType(Regex("(\\w*_)?type"))?.label?.nullIfEmpty()
        val label = node.findChildOfType("variable_declarator", "identifier")?.label

        val labelStr = if (label == null) "unnamed field" else "field \"$label\""
        val typeStr = type?.let { " with type $it" } ?: ""
        val parentStr = serializeParent()?.let { " in $it" } ?: ""
        return "$labelStr$typeStr$parentStr"
    }

    private fun serializeLocalVariableDeclaration(): String {
        val type = node.findChildOfType("type")?.label?.nullIfEmpty()
        val label = node.findChildOfType("variable_declarator", "identifier")?.label

        val labelStr = if (label == null) "unnamed variable" else "variable \"$label\""
        val typeStr = type?.let { " with type $it" } ?: ""
        val parentStr = serializeParent()?.let { " in $it" } ?: ""
        return "$labelStr$typeStr$parentStr"
    }

    private fun serializeParent(): String? {
        return node.parent?.let { NodeLabel(it).serializeAsParent() }
    }

    private fun serializeAsParent(): String? {
        return when (node.type.name) {
            "type_body" -> serializeParent()
            "block" -> serializeParent()
            "type_declaration" -> serializeAsParentTypeDeclaration()
            "method_declaration" -> serializeAsParentMethodDeclaration()
            else -> null
        }
    }

    private fun serializeAsParentTypeDeclaration(): String? {
        val keyword = node.findChildOfType("type_keyword")?.label
        val identifier = node.findChildOfType("identifier")?.label

        return if (keyword == null && identifier == null)  {
            null
        } else if (keyword == null){
            "parent \"$identifier\""
        } else if (identifier == null) {
            "unnamed $keyword"
        } else {
            "$keyword \"$identifier\""
        }
    }

    private fun serializeAsParentMethodDeclaration(): String? {
        val identifier = node.findChildOfType("identifier")?.label

        return if (identifier == null) {
            "unnamed method"
        } else {
            "method \"$identifier\""
        }
    }
}
