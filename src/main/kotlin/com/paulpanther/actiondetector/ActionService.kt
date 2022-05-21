package com.paulpanther.actiondetector

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.refactoringminer.api.Refactoring

typealias RefactoringListener = (refactorings: List<Refactoring>) -> Unit

@Service
class ActionService(private val project: Project) {
    private val listeners = mutableListOf<RefactoringListener>()
    private val allRefactorings = mutableListOf<Refactoring>()

    fun update(file: VirtualFile) {
        val (from, to) = FileSnapshotProvider.getSnapshot(project, file) ?: return
        val refactorings = ActionMiner.getRefactoring(from, to)
        FileSnapshotProvider.buildSnapshot(project, file)
        allRefactorings += refactorings
        listeners.forEach { it(allRefactorings) }
    }

    fun addRefactoringListener(listener: RefactoringListener) {
        listeners += listener
    }
}

val Project.actionService get() = service<ActionService>()