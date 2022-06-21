package com.paulpanther.actiondetector

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.DumbAware
import java.io.File

class ShowRefactoringsAction: AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        val project = e.project ?: return
        if (file != null) {
            val directory = File(project.basePath ?: error("No base path"), ".mermaid")
            project.actionService.outputActionGraph(file)?.generateMermaidImage(directory)
        }
    }
}
