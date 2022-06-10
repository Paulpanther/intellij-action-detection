package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

typealias RefactoringListener = (refactorings: List<Action>) -> Unit

@Service
class ActionService(private val project: Project) {
    private val listeners = mutableListOf<RefactoringListener>()
    private val allRefactorings = mutableListOf<Action>()
    private val miner = ActionMiner()

    fun update(file: VirtualFile) {
        FileSnapshotProvider.buildSnapshot(project, file)
    }

    fun showRefactorings(file: VirtualFile) {
        val (from, to) = FileSnapshotProvider.getSnapshot(project, file) ?: return
        val refactorings = miner.getRefactoring(from, to)
//        allRefactorings += refactorings
        listeners.forEach { it(refactorings) }
    }

    fun addRefactoringListener(listener: RefactoringListener) {
        listeners += listener
    }
}

val Project.actionService get() = service<ActionService>()
