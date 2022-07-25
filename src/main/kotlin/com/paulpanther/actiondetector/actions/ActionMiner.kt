package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.matchers.Matchers

class ActionMiner {
    private val matcher = Matchers.getInstance().matcher
    private val editGenerator = DisplayActionsGenerator()

    fun getRefactoring(original: Snapshot, snap: Snapshot): List<Action>? {
        val mappings = matcher.match(snap.tree, original.tree)
        return editGenerator.computeActions(mappings)
    }
}
