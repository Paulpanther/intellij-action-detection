import com.github.gumtreediff.actions.ChawatheScriptGenerator
import com.github.gumtreediff.actions.EditScript
import com.github.gumtreediff.actions.EditScriptGenerator
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator
import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.actions.model.Delete
import com.github.gumtreediff.actions.model.Insert
import com.github.gumtreediff.actions.model.TreeDelete
import com.github.gumtreediff.actions.model.TreeInsert
import com.github.gumtreediff.matchers.MappingStore

// TODO: Implement more advance aggregator which aggregates Add/Remove actions
//  to more advanced actions like Move, Replace, Extract, etc.

class SimpleScriptGenerator: EditScriptGenerator {
    override fun computeActions(mappings: MappingStore?): EditScript {
        val actions = SimplifiedChawatheScriptGenerator().computeActions(mappings)
        return classify(actions)
    }
}

class GranularScriptGenerator: EditScriptGenerator {
    override fun computeActions(mappings: MappingStore?): EditScript {
        val actions = ChawatheScriptGenerator().computeActions(mappings)
        return classify(actions)
    }
}

private fun classify(actions: EditScript): EditScript {
    val removed = mutableListOf<Action>()
    val added = mutableListOf<Action>()

    val replace = { old: Action, new: Action ->
        removed.add(old)
        added.add(new)
    }

    for (action in actions) {
        when (action) {
            is Insert, is TreeInsert -> replace(action, Add.from(action))
            is Delete, is TreeDelete -> replace(action, Remove.from(action))
        }
    }

    removed.forEach { actions.remove(it) }
    added.forEach { actions.add(it) }

    return actions
}