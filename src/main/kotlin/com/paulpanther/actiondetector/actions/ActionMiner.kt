package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.gen.treesitter.JavaTreeSitterTreeGenerator
import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.gen.SyntaxException
import com.github.gumtreediff.matchers.Matchers

class ActionMiner(private val grouper: ActionGrouper = ClassContentActionGrouper()) {
    private val matcher = Matchers.getInstance().matcher
    private val editGenerator = DisplayActionsGenerator()
    private val treeGenerator: JavaTreeSitterTreeGenerator

    init {
        val tS = System.getProperty("tree-sitter", "/home/paul/dev/uni/ts-edit-action-detector/tree-sitter-parser/tree-sitter-parser.py")
        System.setProperty("gt.ts.path", tS)

        treeGenerator = JavaTreeSitterTreeGenerator()
    }

    fun getRefactoringGroups(original: String, snap: String) = getRefactoring(original, snap)?.let { grouper.groupActions(it) }

    fun getRefactoring(original: String, snap: String): List<Action>? {
        return try {
            val originalRoot =
                treeGenerator.generateFrom().string(original).root
            val snapshotRoot =
                treeGenerator.generateFrom().string(snap).root

            val mappings = matcher.match(snapshotRoot, originalRoot)
            editGenerator.computeActions(mappings, original, snap).asList()
        } catch (e: SyntaxException) {
            null
        }
    }
}
