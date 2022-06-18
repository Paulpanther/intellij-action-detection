package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.Project
import java.io.File

data class ActionWithFile(
    val action: Action,
    val from: File,
    val to: File,
)

class ActionLogGenerator(
    private val project: Project,
    private val file: File
) {
    private val miner = ActionMiner()
    private val snapshots = FileSnapshotProvider(project, file)

    private var lastNewActions = listOf<Action>()

    private val timeline = Timeline()
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

        val newActions = findNewActions()
        if (!newActions.similarTo(lastNewActions)) {
            val actionsPerSnap = findActionsToAllPrevious()
            val next = snapshots.buildNextSnapshot()

            timeline.add(next, actionsPerSnap)
            currentShortestPath = timeline.findShortestPath()
            return true
        }
        lastNewActions = newActions

        return false
    }

    private fun findNewActions(): List<Action> {
        return miner.getRefactoring(file, snapshots.root.file)
    }

    private fun findActionsToAllPrevious(): Map<Snapshot, List<Action>> {
        return snapshots.associateWith { miner.getRefactoring(file, it.file) }
    }
}
