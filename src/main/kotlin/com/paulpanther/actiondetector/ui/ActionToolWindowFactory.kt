package com.paulpanther.actiondetector.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class ActionToolWindowFactory: ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val windowBuilder = ActionToolWindow(project)
        val manager = toolWindow.contentManager
        val content =
            manager.factory.createContent(windowBuilder.window, "", false)
        manager.addContent(content)
    }
}

