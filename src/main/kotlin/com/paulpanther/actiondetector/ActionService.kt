package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.FileContentUtil
import com.intellij.util.application
import com.paulpanther.actiondetector.actions.ActionLogGenerator
import com.paulpanther.actiondetector.actions.FileSnapshotProvider
import com.paulpanther.actiondetector.actions.Graph

typealias RefactoringListener = (refactorings: List<Action>, graph: Graph) -> Unit

@Service
class ActionService(private val project: Project) {
    private val listeners = mutableListOf<RefactoringListener>()
    private val generators = mutableMapOf<VirtualFile, ActionLogGenerator>()
    val annotations = mutableMapOf<VirtualFile, List<ActionAnnotation<*>>>()

    init {
        FileUpdateListener.init(project)
    }

    fun update(file: VirtualFile) {
        application.invokeLater {
            showRefactorings(file)
        }
    }

    fun clear(file: VirtualFile? = project.openFile) {
        file ?: return

        generators[file]?.let { log ->
            log.clear()
            updateUi(file, log)
        }
    }

    private fun showRefactorings(file: VirtualFile) {
        val gen = generators
            .getOrPut(file) { ActionLogGenerator(project, file) }

        if (gen.update()) {
            updateUi(file, gen)
        }
    }

    private fun updateUi(file: VirtualFile, gen: ActionLogGenerator) {
        listeners.forEach { it(gen.currentShortestPath, gen.actionGraph) }
        annotations[file] = ActionAnnotation.from(gen.currentShortestPath)
        FileContentUtil.reparseFiles(project, listOf(file), true)
    }

    fun outputActionGraph(file: VirtualFile? = project.openFile): Graph? {
        file ?: return null

        val gen = generators
            .getOrPut(file) { ActionLogGenerator(project, file) }

        return gen.actionGraph
    }

    fun addRefactoringListener(listener: RefactoringListener) {
        listeners += listener
    }

}

val Project.actionService get() = service<ActionService>()
