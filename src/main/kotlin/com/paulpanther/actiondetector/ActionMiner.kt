package com.paulpanther.actiondetector

import com.github.gumtreediff.gen.treesitter.JavaTreeSitterTreeGenerator
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator
import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.matchers.Matchers
import java.io.File

class ActionMiner {
    private val matcher = Matchers.getInstance().matcher
    private val editGenerator = SimplifiedChawatheScriptGenerator()
    private val treeGenerator: JavaTreeSitterTreeGenerator

    init {
        val tS = System.getProperty("tree-sitter", "/home/paul/dev/uni/ts-edit-action-detector/tree-sitter-parser/tree-sitter-parser.py")
        System.setProperty("gt.ts.path", tS)

        treeGenerator = JavaTreeSitterTreeGenerator()
    }

    fun getRefactoring(from: File, to: File): List<Action> {
        val r1 = treeGenerator.generateFrom().file(from.absoluteFile).root
        val r2 = treeGenerator.generateFrom().file(to.absoluteFile).root

//        val c1 = File(f1).readText()
//        val c2 = File(f2).readText()

        val mappings = matcher.match(r1, r2)
        return editGenerator.computeActions(mappings).asList()
    }
}
