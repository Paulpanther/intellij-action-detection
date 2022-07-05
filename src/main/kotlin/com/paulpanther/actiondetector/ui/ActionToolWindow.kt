package com.paulpanther.actiondetector.ui

import com.github.gumtreediff.actions.model.Action
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaCodeFragmentFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.cellPanel
import com.paulpanther.actiondetector.actionService
import com.paulpanther.actiondetector.actions.ActionGroup
import com.paulpanther.actiondetector.actions.DisplayAction
import com.paulpanther.actiondetector.actions.displayName
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel

class ActionToolWindow(private val project: Project) {
    private var clearSnapshotsButton: JButton? = null
    private var generateTreeImageButton: JButton? = null
    private var contentPanel: JPanel? = null
    private var actionsPanel: JPanel? = null
    var window: JPanel? = null

    init {
        clearSnapshotsButton?.addActionListener(this::onClearSnapshots)
        generateTreeImageButton?.addActionListener(this::onGenerateTreeImage)

        project.actionService.addRefactoringListener { actions, _ ->
            actionsPanel?.removeAll()
            buildEntries(actions)
            actionsPanel?.validate()
            actionsPanel?.repaint()
        }
    }
    
    private fun onClearSnapshots(e: ActionEvent) {
        project.actionService.clear()
    }

    private fun onGenerateTreeImage(e: ActionEvent) {
//        project.actionService.generateTree()
    }

    fun createUIComponents() {
        contentPanel = JPanel().apply {
            layout = BorderLayout()
        }
        actionsPanel = JPanel().apply {
            layout = BorderLayout()
        }
        contentPanel?.add(JBScrollPane(actionsPanel), BorderLayout.CENTER)
    }

    @Suppress("UnstableApiUsage")
    private fun buildEntries(actions: List<ActionGroup>) {
        actionsPanel?.add(panel {
            @Suppress("UnstableApiUsage")
            for (group in actions) {
                row {
                    panel {
                        collapsibleGroup(group.title) {
                            buildDetails(this, group)
                        }.apply {
                            expanded = true
                        }
                    }
                }
            }
        }, BorderLayout.NORTH)
    }

    @Suppress("UnstableApiUsage")
    private fun buildDetails(p: Panel, group: ActionGroup) {
        for (action in group.actions) {
            p.row {
                label(action.name)
            }
        }
    }
}
