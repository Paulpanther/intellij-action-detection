package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.gen.SyntaxException
import com.github.gumtreediff.gen.treesitter.JavaTreeSitterTreeGenerator
import com.github.gumtreediff.matchers.Matchers

class ActionMiner {
    private val matcher = Matchers.getInstance().matcher
    private val editGenerator = DisplayActionsGenerator()

    fun getRefactoring(original: Snapshot, snap: Snapshot): List<Action>? {
        var startTime = System.currentTimeMillis()

        val mappings = matcher.match(snap.tree, original.tree)

        println("2: ${System.currentTimeMillis() - startTime} ms")
        startTime = System.currentTimeMillis()

        val res = editGenerator.computeActions(mappings)

        println("3: ${System.currentTimeMillis() - startTime} ms")

        return res
    }
}
