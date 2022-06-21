package com.paulpanther.actiondetector

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile

val VirtualFile.content get() =
    FileDocumentManager.getInstance().getDocument(this)?.charsSequence?.toString()
