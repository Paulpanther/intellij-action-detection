package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.actions.model.TreeInsert
import com.github.gumtreediff.tree.Tree
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.paulpanther.actiondetector.actions.Add
import com.paulpanther.actiondetector.actions.Remove
import com.paulpanther.actiondetector.actions.range

object ActionHighlightAttribs {
    val insert = TextAttributesKey.createTextAttributesKey("INSERT_ACTION_INSERTED", EditorColors.SEARCH_RESULT_ATTRIBUTES)
    val remove = TextAttributesKey.createTextAttributesKey("REMOVE_ACTION", EditorColors.DELETED_TEXT_ATTRIBUTES)
//    val insertParent = TextAttributesKey.createTextAttributesKey("INSERT_ACTION_PARENT", EditorColors.IDENTIFIER_UNDER_CARET_ATTRIBUTES)
}

class ActionAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val project = element.project
        val file = element.containingFile.virtualFile
        val annotations = project.actionService.annotations[file] ?: return

        annotations.forEach { it.annotate(holder, element) }
    }
}

class ActionAnnotation<T: Action>(
    val action: T,
    private val annotator: (AnnotationHolder, T, PsiElement) -> Unit,
    var visible: Boolean = false
) {
    companion object {
        private fun <T: Action> from(action: T): ActionAnnotation<out T>? {
            return when (action) {
                is Add -> ActionAnnotation(action, ::annotateInsertTree)
                is Remove -> ActionAnnotation(action, ::annotateRemoveTree)
                else -> null
            }
        }

        fun from(actions: List<Action>): List<ActionAnnotation<*>> {
            return actions.mapNotNull { from(it) }
        }
    }

    fun annotate(holder: AnnotationHolder, element: PsiElement) {
        if (visible) annotator(holder, action, element)
    }
}

fun annotateInsertTree(holder: AnnotationHolder, action: Add, element: PsiElement) {
    holder.createTreeAnnotation(action.node, ActionHighlightAttribs.insert, element)
}

fun annotateRemoveTree(holder: AnnotationHolder, action: Remove, element: PsiElement) {
    holder.createTreeAnnotation(action.node, ActionHighlightAttribs.remove, element) {
        afterEndOfLine()
    }
}

fun AnnotationHolder.createTreeAnnotation(tree: Tree, attribs: TextAttributesKey, element: PsiElement, modifier: AnnotationBuilder.() -> Unit = {}) {
    if (tree.range in element.textRange) {
        newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(tree.range)
            .textAttributes(attribs)
            .apply(modifier)
            .create()
    }
}
