package com.paulpanther.actiondetector.actions

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.paulpanther.actiondetector.content
import java.io.File
import java.nio.file.Path

data class Snapshot(val content: String)

class FileSnapshotProvider(
    private val file: VirtualFile,
    private val snapshots: MutableList<Snapshot> = mutableListOf()
): List<Snapshot> by snapshots {

    val root get() = snapshots.first()

    fun buildNextSnapshot(): Snapshot {
        val content = file.content ?: error("Could not get latest text")
        return Snapshot(content)
            .also {
                snapshots += it
            }
    }
}
