package com.paulpanther.actiondetector

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.DumbAware

class CreateSnapshotAction: AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        val project = e.project ?: return
        if (file != null) {
            project.actionService.update(file)
        }
    }
}
