package com.paulpanther.actiondetector

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.name

object FileSnapshotProvider {
    private const val snapshotsDir = ".snapshots"

    fun getAndBuildSnapshot(project: Project, file: VirtualFile): Pair<File, File>? {
        val pair = getSnapshot(project, file)
        buildSnapshot(project, file)
        return pair
    }

    fun buildSnapshot(project: Project, file: VirtualFile) {
        createSnapshotDir(project)
        val (original, snapshot) = getSnapshotPaths(project, file)
        if (!Files.exists(snapshot)) {
            Files.createFile(snapshot)
        }
        Files.copy(original, snapshot, StandardCopyOption.REPLACE_EXISTING)
    }

    fun getSnapshot(project: Project, file: VirtualFile): Pair<File, File>? {
        val (original, snapshot) = getSnapshotPaths(project, file)
        val snapFile = snapshot.toFile()
        if (!snapFile.exists()) return null
        return Pair(original.toFile(), snapFile)
    }

    private fun createSnapshotDir(project: Project) {
        val snapDir = Paths.get(project.basePath, snapshotsDir).toFile()
        if (!snapDir.exists()) {
            snapDir.mkdir()
        }
    }

    private fun getSnapshotPaths(project: Project, file: VirtualFile): Pair<Path, Path> {
        val original = file.toNioPath()
        val snapshot = Paths.get(project.basePath, snapshotsDir, original.name)
        return Pair(original, snapshot)
    }
}
