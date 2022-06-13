import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.tree.Tree

val Action.displayName: String get() = "$name ${node.displayName}"

private val Tree.displayName: String get() = toString()
    .substringBefore("[")
    .replace("_", " ")
