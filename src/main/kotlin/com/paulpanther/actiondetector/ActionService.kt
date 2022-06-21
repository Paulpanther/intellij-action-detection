package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.FileContentUtil
import com.paulpanther.actiondetector.actions.ActionLogGenerator
import com.paulpanther.actiondetector.actions.ActionWithFile
import com.paulpanther.actiondetector.actions.FileSnapshotProvider

typealias RefactoringListener = (refactorings: List<Action>) -> Unit

@Service
class ActionService(private val project: Project) {
    private val listeners = mutableListOf<RefactoringListener>()
    private val generators = mutableMapOf<VirtualFile, ActionLogGenerator>()
    val annotations = mutableMapOf<VirtualFile, List<ActionAnnotation<*>>>()

    init {
        FileUpdateListener.init(project)
    }

    fun update(file: VirtualFile) {
        // TODO refactor into showRefactorings
        showRefactorings(file)
    }

    fun show(action: Action) {
        for ((file, annotations) in annotations) {
            val clickedAnnotation = annotations.find { it.action == action }
            if (clickedAnnotation != null) {

                if (!clickedAnnotation.visible) {
                    annotations.forEach { it.visible = false }
                }

                clickedAnnotation.visible = !clickedAnnotation.visible

                FileContentUtil.reparseFiles(project, listOf(file), true)
                break
            }
        }
    }

    fun showRefactorings(file: VirtualFile) {
        val gen = generators
            .getOrPut(file) { ActionLogGenerator(project, file) }

        if (gen.update()) {
            listeners.forEach { it(gen.currentShortestPath) }
            annotations[file] = ActionAnnotation.from(gen.currentShortestPath)
            FileContentUtil.reparseFiles(project, listOf(file), true)
        }
    }

    fun addRefactoringListener(listener: RefactoringListener) {
        listeners += listener
    }

}

val Project.actionService get() = service<ActionService>()
