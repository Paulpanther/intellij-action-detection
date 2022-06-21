package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

data class ActionWithFile(
    val action: Action,
    val from: File,
    val to: File,
)

class ActionLogGenerator(
    project: Project,
    virtualFile: VirtualFile
) {
    private val miner = ActionMiner()
    private val snapshots = FileSnapshotProvider(project, virtualFile)

    private var lastNewActions = listOf<Action>()

    private val timeline = Timeline()
    val actionGraph
        get() = timeline.actionGraph
    var currentShortestPath = listOf<Action>()
        private set

    init {
        val root = snapshots.buildNextSnapshot()
        timeline.add(root, mapOf())
    }

    fun update(): Boolean {
        // make diff to root snapshot
        // is actions != last actions
        // diff to all prev snapshots
        // store actions of each pair
        // find the new shortest path

        val newActions = findNewActions() ?: return false
        if (!newActions.similarTo(lastNewActions)) {
            val actionsPerSnap = findActionsToAllPrevious() ?: return false
            val next = snapshots.buildNextSnapshot()

            timeline.add(next, actionsPerSnap)
            currentShortestPath = timeline.findShortestPath()
            lastNewActions = newActions

            return true
        }
        lastNewActions = newActions

        return false
    }

    private fun findNewActions(): List<Action>? {
        val file = snapshots.updateLatestFile() ?: return listOf()
        return miner.getRefactoring(file, snapshots.root.file)
    }

    private fun findActionsToAllPrevious(): Map<Snapshot, List<Action>>? {
        val file = snapshots.updateLatestFile() ?: return mapOf()
        return snapshots.associateWith { miner.getRefactoring(file, it.file) ?: return null }
    }
}
