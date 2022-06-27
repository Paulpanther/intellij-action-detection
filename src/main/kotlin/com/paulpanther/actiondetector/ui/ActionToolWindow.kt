package com.paulpanther.actiondetector.ui

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.paulpanther.actiondetector.actionService
import com.paulpanther.actiondetector.actions.displayName
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
        contentPanel = JPanel()
        actionsPanel = JPanel()
        contentPanel?.add(JBScrollPane(actionsPanel))
    }

    @Suppress("UnstableApiUsage")
    private fun buildEntries(refactorings: List<Action>) {
        actionsPanel?.add(panel {
            for (ref in refactorings) {
                row {
                    label(ref.displayName)
                }
            }
        })
    }

}
