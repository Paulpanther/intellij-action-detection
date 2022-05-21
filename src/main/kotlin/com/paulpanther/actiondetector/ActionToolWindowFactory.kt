package com.paulpanther.actiondetector

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.*
import org.refactoringminer.api.Refactoring
import javax.swing.JPanel

class ActionToolWindowFactory: ToolWindowFactory, DumbAware {
    private val entriesPane = JPanel()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val manager = toolWindow.contentManager
        val content = manager.factory.createContent(actionToolWindow(), "", false)
        manager.addContent(content, -1)

        project.actionService.addRefactoringListener {
            entriesPane.removeAll()
            buildEntries(it)
            entriesPane.validate()
            entriesPane.repaint()
        }
    }

    private fun actionToolWindow() = JBScrollPane(entriesPane)

    @Suppress("UnstableApiUsage")
    private fun buildEntries(refactorings: List<Refactoring>) {
        entriesPane.add(panel {
            for (ref in refactorings) {
                row {
                    label("Ref ${ref.name}")
                }
            }
        })
    }
}

