package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.gen.SyntaxException
import com.github.gumtreediff.gen.treesitter.AbstractTreeSitterGenerator
import com.github.gumtreediff.gen.treesitter.JavaTreeSitterTreeGenerator
import com.github.gumtreediff.tree.Tree
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.vfs.VirtualFile
import com.paulpanther.actiondetector.content

data class Snapshot(
    val id: Int,
    val content: String,
    val tree: Tree
)

class FileSnapshotProvider(
    private val file: VirtualFile,
    private val snapshots: MutableList<Snapshot> = mutableListOf()
): MutableList<Snapshot> by snapshots {

    private val treeGenerator: AbstractTreeSitterGenerator
    val root get() = snapshots.firstOrNull().also {
        if (it == null) {
            Notifications.Bus.notify(Notification("Error Report", "Could not create root Snapshot", "Please remove syntax errors and clear snapshots", NotificationType.ERROR))
        }
    }
    var lastId = 0

    init {
        val tS = System.getProperty("tree-sitter", "/home/paul/dev/uni/ts-edit-action-detector/tree-sitter-parser/tree-sitter-parser.py")
        System.setProperty("gt.ts.path", tS)

//        treeGenerator = JavaTreeSitterTreeGenerator()
        treeGenerator = KotlinTreeSitterGenerator()
    }

    fun buildNextSnapshot(): Snapshot? {
        val content = file.content ?: error("Could not get latest text")

        return try {
            val tree =
                treeGenerator.generateFrom().string(content).root
            Snapshot(lastId++, content, tree)
        } catch (e: SyntaxException) {
            null
        }
    }

    override fun clear() {
        lastId = 0
        snapshots.clear()
        buildNextSnapshot()?.let { this += it }
    }
}
