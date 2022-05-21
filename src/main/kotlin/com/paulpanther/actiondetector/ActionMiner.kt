package com.paulpanther.actiondetector

import gr.uom.java.xmi.UMLModelASTReader
import org.refactoringminer.api.Refactoring
import java.io.File

object ActionMiner {
    fun getRefactoring(from: File, to: File): List<Refactoring> {
        // TODO this only works for single files in parent directories
        val model1 = UMLModelASTReader(from.parentFile).umlModel
        val model2 = UMLModelASTReader(to.parentFile).umlModel
        val diff = model2.diff(model1)
        val refs = diff.refactorings
        return refs
    }
}
