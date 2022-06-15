package com.paulpanther.actiondetector.actions

import com.intellij.openapi.project.Project
import java.io.File

data class Snapshot(
    val id: Int,
    val file: File)

class FileSnapshotProvider(
    private val project: Project,
    private val file: File,
    private val snapshots: MutableList<Snapshot> = mutableListOf()
): List<Snapshot> by snapshots {

    private val snapshotDir =
        File(project.basePath ?: error("No base path"), ".snapshots")

    private var lastId = -1

    val root get() = snapshots.first()

    fun buildNextSnapshot(): Snapshot {
        buildSnapshot(++lastId)
        return (getSnapshot(lastId) ?: error("Could not create File"))
            .also {
                snapshots += it
            }
    }

    private fun buildSnapshot(name: Int) {
        val snapshot = getSnapshotFile(name)
        file.copyTo(snapshot, true)
    }

    private fun getSnapshot(name: Int): Snapshot? {
        val snapshot = getSnapshotFile(name)
        if (!snapshot.exists()) return null
        return Snapshot(name, snapshot)
    }

    private fun getSnapshotFile(name: Int) =
        File(snapshotDir, "${file.name}-$name")
}
