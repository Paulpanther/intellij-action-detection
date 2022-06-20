package com.paulpanther.actiondetector.actions

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.file.Path

data class Snapshot(
    val id: Int,
    val file: File)

class FileSnapshotProvider(
    private val project: Project,
    private val virtualFile: VirtualFile,
    private val snapshots: MutableList<Snapshot> = mutableListOf()
): List<Snapshot> by snapshots {

    private val snapshotDir =
        File(project.basePath ?: error("No base path"), snapshotDirName)

    private var lastId = -1

    val root get() = snapshots.first()

    companion object {
        const val snapshotDirName = ".snapshots"
        fun isSnapshotFile(file: VirtualFile): Boolean {
            return file.toNioPath().contains(Path.of(snapshotDirName))
        }
    }

    fun buildNextSnapshot(): Snapshot {
        buildSnapshot(++lastId)
        return (getSnapshot(lastId) ?: error("Could not create File"))
            .also {
                snapshots += it
            }
    }

    private fun buildSnapshot(name: Int) {
        val snapshot = getSnapshotFile(name)
        val file = updateLatestFile()
        file?.copyTo(snapshot, true)
    }

    private fun getSnapshot(name: Int): Snapshot? {
        val snapshot = getSnapshotFile(name)
        if (!snapshot.exists()) return null
        return Snapshot(name, snapshot)
    }

    private fun getSnapshotFile(name: Int) =
        File(snapshotDir, "${virtualFile.name}-$name")

    fun updateLatestFile(): File? {
        val file = File(snapshotDir, "${virtualFile.name}-latest")
        val text = FileDocumentManager.getInstance().getDocument(virtualFile)?.charsSequence ?: return null

        file.createNewFile()
        file.writeText(text.toString())

        return file
    }

}
