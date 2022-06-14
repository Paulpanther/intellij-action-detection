package com.paulpanther.actiondetector

import com.github.gumtreediff.gen.treesitter.JavaTreeSitterTreeGenerator
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator
import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.matchers.Matchers
import java.io.File

class ActionMiner {
    private val matcher = Matchers.getInstance().matcher
    private val editGenerator = PerformanceProfilerScriptGenerator(SimpleScriptGenerator(), 1000)
    private val treeGenerator: JavaTreeSitterTreeGenerator

    init {
        val tS = System.getProperty("tree-sitter", "/Users/anaumann/Development/Study/edit-action-detection/tree-sitter-parser/tree-sitter-parser.py")
        System.setProperty("gt.ts.path", tS)

        treeGenerator = JavaTreeSitterTreeGenerator()
    }

    fun getRefactoring(original: File, snap: File): List<Action> {
        val originalRoot = treeGenerator.generateFrom().file(original.absoluteFile).root
        val snapshotRoot = treeGenerator.generateFrom().file(snap.absoluteFile).root

//        val c1 = File(f1).readText()
//        val c2 = File(f2).readText()

        val mappings = matcher.match(snapshotRoot, originalRoot)
        return editGenerator.computeActions(mappings)?.asList() ?: listOf()
    }
}
