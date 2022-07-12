package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class ActionLogGenerator(
    project: Project,
    file: VirtualFile
) {
    private val miner = ActionMiner()
    private val grouper: ActionGrouper = ClassContentActionGrouper()

    private val snapshots = FileSnapshotProvider(file)
    private var lastNewActions = listOf<Action>()

    private val timeline = Timeline()
    val actionGraph by timeline::actionGraph

    var currentShortestPath = listOf<ActionGroup>()
        private set

    init {
        snapshots.buildNextSnapshot()?.also { root ->
            snapshots += root
            timeline.add(root, mapOf())
        }
    }

    fun update(): Boolean {
        // make diff to root snapshot
        // is actions != last actions
        // diff to all prev snapshots
        // store actions of each pair
        // find the new shortest path

        if (!timeline.ready()) return false
        val next = snapshots.buildNextSnapshot() ?: return false
        val newActions = findNewActions(next) ?: return false

        if (!newActions.similarTo(lastNewActions)) {
            val actionsPerSnap = findActionsToAllPrevious(next) ?: return false

            snapshots += next
            timeline.add(next, actionsPerSnap)
            currentShortestPath = grouper.groupActions(timeline.findShortestPath())
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

        snapshots.root?.let {
            timeline.add(it, mapOf())
        }
    }

    private fun findNewActions(next: Snapshot): List<Action>? {
        val root = snapshots.root ?: return null
        return miner.getRefactoring(next, root)
    }

    private fun findActionsToAllPrevious(next: Snapshot): Map<Snapshot, List<Action>>? {
        return snapshots.associateWith {
            miner.getRefactoring(next, it) ?: return null
        }
    }
}
