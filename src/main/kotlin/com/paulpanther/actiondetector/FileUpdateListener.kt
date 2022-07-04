package com.paulpanther.actiondetector

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class FileUpdateListener(
    private val project: Project
): FileEditorManagerListener, DocumentListener {

    companion object {
        fun init(project: Project) {
            project.messageBus
                .connect()
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, FileUpdateListener(project))
        }
    }

    private val documents = mutableListOf<Document>()

    override fun documentChanged(event: DocumentEvent) {
        val file = event.document.file ?: return
        project.actionService.update(file)
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        source.project.actionService.update(file)

        file.document?.let {
            if (it !in documents) {
                it.addDocumentListener(this)
                documents += it
            }
        }
    }
}
