package com.paulpanther.actiondetector

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.FileContentUtil
import com.paulpanther.actiondetector.actions.ActionLogGenerator
import com.paulpanther.actiondetector.actions.ActionWithFile

typealias RefactoringListener = (refactorings: List<ActionWithFile>) -> Unit

@Service
class ActionService(private val project: Project) {
    private val listeners = mutableListOf<RefactoringListener>()
    private val generators = mutableMapOf<VirtualFile, ActionLogGenerator>()
    val annotations = mutableMapOf<VirtualFile, List<ActionAnnotation>>()

    fun update(file: VirtualFile) {

    }

    fun showRefactorings(file: VirtualFile) {
        val gen = generators
            .getOrPut(file) { ActionLogGenerator(project, file.toNioPath().toFile()) }

        if (gen.update()) {
            listeners.forEach { it(gen.currentShortestPath) }
            annotations[file] = ActionToPsiMapper.generateAnnotations(gen.currentShortestPath)
            FileContentUtil.reparseFiles(project, listOf(file), true)
        }
    }

    fun addRefactoringListener(listener: RefactoringListener) {
        listeners += listener
    }

}

val Project.actionService get() = service<ActionService>()
