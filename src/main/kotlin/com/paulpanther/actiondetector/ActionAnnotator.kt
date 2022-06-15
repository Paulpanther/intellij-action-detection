package com.paulpanther.actiondetector

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.psi.PsiElement

class ActionAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val project = element.project
        val file = element.containingFile.virtualFile
        val annotations = project.actionService.annotations.get(file) ?: return

        val annotation = annotations.find {
            it.range == element.textRange
        } ?: return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(annotation.range)
            .textAttributes(CodeInsightColors.ERRORS_ATTRIBUTES)
            .create()
    }
}
