package com.paulpanther.actiondetector

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

val VirtualFile.content get() =
    document?.charsSequence?.toString()

val Document.file get() =
    FileDocumentManager.getInstance().getFile(this)

val VirtualFile.document get() =
    FileDocumentManager.getInstance().getDocument(this)

val Project.openFile get() =
    FileEditorManager.getInstance(this).selectedTextEditor?.document?.file

