package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.Project
import java.io.File

class ActionLogGenerator(
    private val project: Project,
    private val file: File
) {
    private val miner = ActionMiner()
    private val snapshots = FileSnapshotProvider(project, file)

    private var lastNewActions = listOf<Action>()
    private val timeline = Timeline()
    private var lastSnap = snapshots.buildNextSnapshot()
        .also { timeline.add(it, mapOf()) }
    private var currentShortestPath = listOf<Action>()

    fun update(): List<Action> {
        // make diff to last snapshot
        // is actions != last actions
        // diff to all prev snapshots
        // store actions of each pair
        // find the new shortest path

        val newActions = findNewActions()
        if (!newActions.similarTo(lastNewActions)) {
            val actionsPerSnap = findActionsToAllPrevious()
            lastSnap = snapshots.buildNextSnapshot()

            timeline.add(lastSnap, actionsPerSnap)
            currentShortestPath = timeline.findShortestPath()
        }
        lastNewActions = newActions

        return currentShortestPath
    }

    private fun findNewActions(): List<Action> {
        return miner.getRefactoring(file, lastSnap.file)
    }

    private fun findActionsToAllPrevious(): Map<Snapshot, List<Action>> {
        return snapshots.associateWith { miner.getRefactoring(file, it.file) }
    }
}
