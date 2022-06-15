package com.paulpanther.actiondetector

import com.intellij.openapi.util.TextRange
import com.paulpanther.actiondetector.actions.ActionWithFile
import com.paulpanther.actiondetector.actions.range

object ActionToPsiMapper {
    fun generateAnnotations(actions: List<ActionWithFile>): List<ActionAnnotation> {
        return actions.map { ActionAnnotation(it.action.node.range) }
    }
}

data class ActionAnnotation(
    val range: TextRange
)
