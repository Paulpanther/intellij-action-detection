package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.paulpanther.actiondetector.content
import java.io.File

data class ActionWithFile(
    val action: Action,
    val from: File,
    val to: File,
)

class ActionLogGenerator(
    project: Project,
    private val file: VirtualFile
) {
    private val miner = ActionMiner()
    private val snapshots = FileSnapshotProvider(file)

    private var lastNewActions = listOf<Action>()

    private val timeline = Timeline()
    val actionGraph by timeline::actionGraph

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

    fun clear() {
        lastNewActions = listOf()
        timeline.clear()
        currentShortestPath = listOf()
        snapshots.clear()

        timeline.add(snapshots.root, mapOf())
    }

    private fun findNewActions(): List<Action>? {
        val current = file.content ?: return null
        return miner.getRefactoring(current, snapshots.root.content)
    }

    private fun findActionsToAllPrevious(): Map<Snapshot, List<Action>>? {
        val current = file.content ?: return null
        return snapshots.associateWith { miner.getRefactoring(current, it.content) ?: return null }
    }
}
