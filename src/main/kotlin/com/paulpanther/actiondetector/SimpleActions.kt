import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.actions.model.Addition
import com.github.gumtreediff.actions.model.Delete
import com.github.gumtreediff.actions.model.Insert
import com.github.gumtreediff.actions.model.TreeDelete
import com.github.gumtreediff.actions.model.TreeInsert
import com.github.gumtreediff.tree.Tree

class Add(node: Tree, parent: Tree, pos: Int) : Addition(node, parent, pos) {
    constructor(action: Insert): this(action.node, action.parent, action.position)
    constructor(action: TreeInsert): this(action.node, action.parent, action.position)

    override fun getName(): String = "Add"

    companion object {
        fun <A: Action> from(action: A): Add {
            (action as? Insert)?.let { return Add(it) }
            (action as? TreeInsert)?.let { return Add(it) }
            throw IllegalStateException()
        }
    }
}

class Remove(node: Tree) : Action(node) {
    constructor(action: Delete): this(action.node)
    constructor(action: TreeDelete): this(action.node)

    override fun getName(): String = "Remove"

    companion object {
        fun <A: Action> from(action: A): Remove {
            (action as? Delete)?.let { return Remove(it) }
            (action as? TreeDelete)?.let { return Remove(it) }
            throw IllegalStateException()
        }
    }
}

// TODO: Add cool aggregated actions like Move, Replace, Extract, etc.