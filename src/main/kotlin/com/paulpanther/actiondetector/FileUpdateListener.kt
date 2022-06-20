package com.paulpanther.actiondetector

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

class FileUpdateListener: TypedHandlerDelegate(), FileEditorManagerListener {
    companion object {
        fun init(project: Project) {
            project.messageBus
                .connect()
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, FileUpdateListener())
        }
    }

    override fun charTyped(
        c: Char,
        project: Project,
        editor: Editor,
        file: PsiFile
    ): Result {
        project.actionService.update(file.virtualFile)
        return super.charTyped(c, project, editor, file)
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        source.project.actionService.update(file)
    }
}
