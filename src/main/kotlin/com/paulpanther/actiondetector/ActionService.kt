package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.paulpanther.actiondetector.actions.ActionLogGenerator

typealias RefactoringListener = (refactorings: List<Action>) -> Unit

@Service
class ActionService(private val project: Project) {
    private val listeners = mutableListOf<RefactoringListener>()
    private val generators = mutableMapOf<VirtualFile, ActionLogGenerator>()

    fun update(file: VirtualFile) {

    }

    fun showRefactorings(file: VirtualFile) {
        val refactorings = generators
            .getOrPut(file) { ActionLogGenerator(project, file.toNioPath().toFile()) }
            .update()
        listeners.forEach { it(refactorings) }
    }

    fun addRefactoringListener(listener: RefactoringListener) {
        listeners += listener
    }
}

val Project.actionService get() = service<ActionService>()
