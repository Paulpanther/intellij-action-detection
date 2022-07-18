package com.paulpanther.actiondetector.ui

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.panel
import com.paulpanther.actiondetector.actionService
import com.paulpanther.actiondetector.actions.ActionGroup
import com.paulpanther.actiondetector.actions.ActionGroupGroup
import com.paulpanther.actiondetector.actions.ActionLeafGroup
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
    private fun buildEntries(groups: List<ActionGroup>) {
        actionsPanel?.add(panel {
            buildGroups(this, groups)
        }, BorderLayout.NORTH)
    }

    @Suppress("UnstableApiUsage")
    private fun buildGroups(p: Panel, groups: List<ActionGroup>) {
        for (group in groups) {
            buildGroup(p, group)
        }
    }

    @Suppress("UnstableApiUsage")
    private fun buildGroup(p: Panel, group: ActionGroup) {
        p.row {
            panel {
                collapsibleGroup(group.title) {
                    when (group) {
                        is ActionLeafGroup -> buildLeafs(this, group.actions)
                        is ActionGroupGroup -> buildGroups(this, group.groups)
                    }
                }.apply { expanded = true }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun buildLeafs(p: Panel, actions: List<Action>) {
        for (action in actions) {
            p.row {
                label(action.name)
            }
        }
    }
}
