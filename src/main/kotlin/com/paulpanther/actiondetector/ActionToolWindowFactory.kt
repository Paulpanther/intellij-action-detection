package com.paulpanther.actiondetector

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.paulpanther.actiondetector.actions.ActionWithFile
import com.paulpanther.actiondetector.actions.displayName
import javax.swing.JPanel

class ActionToolWindowFactory: ToolWindowFactory, DumbAware {
    private val entriesPane = JPanel()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val manager = toolWindow.contentManager
        val content = manager.factory.createContent(actionToolWindow(), "", false)
        manager.addContent(content, -1)
        // Does not work, shortcutSet.shortcuts is empty: buildPluginShortcutsHelp()

        project.actionService.addRefactoringListener {
            entriesPane.removeAll()
            buildEntries(it)
            entriesPane.validate()
            entriesPane.repaint()
        }
    }

    private fun actionToolWindow() = JBScrollPane(entriesPane)

    @Suppress("UnstableApiUsage")
    private fun buildPluginShortcutsHelp() {
        entriesPane.add(panel {
            row {
                label("${ShowRefactoringsAction().shortcutSet.shortcuts.firstOrNull()}")
                label("${CreateSnapshotAction().shortcutSet.shortcuts.firstOrNull()}")
            }
        })
    }
    @Suppress("UnstableApiUsage")
    private fun buildEntries(refactorings: List<ActionWithFile>) {
        entriesPane.add(panel {
            for (ref in refactorings) {
                row {
                    label(ref.action.displayName)
                }
            }
        })
    }
}

