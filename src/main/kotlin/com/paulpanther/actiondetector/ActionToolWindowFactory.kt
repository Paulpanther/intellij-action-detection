package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.paulpanther.actiondetector.actions.Graph
import com.paulpanther.actiondetector.actions.convertToMermaidChart
import com.paulpanther.actiondetector.actions.displayName
import javax.swing.JPanel

class ActionToolWindowFactory: ToolWindowFactory, DumbAware {
    private val entriesPane = JPanel()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val manager = toolWindow.contentManager
        val content = manager.factory.createContent(actionToolWindow(), "", false)
        manager.addContent(content, -1)
        // Does not work, shortcutSet.shortcuts is empty: buildPluginShortcutsHelp()

        project.actionService.addRefactoringListener { actions, _ ->
            entriesPane.removeAll()
            buildEntries(project, actions)
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
    private fun buildEntries(project: Project, refactorings: List<Action>) {
        entriesPane.add(panel {
            for (ref in refactorings) {
                row {
                    button(ref.displayName) {
                        project.actionService.show(ref)
                    }
                }
            }
        })
    }

    @Suppress("UnstableApiUsage")
    private fun buildGraph(graph: Graph) {
        entriesPane.add(panel {
            row {
                label(graph.convertToMermaidChart())
            }
        })
    }
}

