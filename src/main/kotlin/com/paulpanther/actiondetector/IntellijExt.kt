package com.paulpanther.actiondetector

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

val VirtualFile.content get() =
    FileDocumentManager.getInstance().getDocument(this)?.charsSequence?.toString()

val Document.file get() =
    FileDocumentManager.getInstance().getFile(this)

val Project.openFile get() =
    FileEditorManager.getInstance(this).selectedTextEditor?.document?.file

