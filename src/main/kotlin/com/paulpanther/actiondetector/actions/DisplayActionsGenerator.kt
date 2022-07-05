package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.ChawatheScriptGenerator
import com.github.gumtreediff.actions.EditScript
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator
import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.matchers.MappingStore

class DisplayAction(
    action: Action,
    private val name: String
): Action(action.node) {
    override fun getName() = name
}

class DisplayActionsGenerator {

    fun computeActions(mappings: MappingStore?): List<DisplayAction> {
        val actions = SimplifiedChawatheScriptGenerator().computeActions(mappings)
        return classify(actions)
    }
}

class GranularScriptGenerator {
    fun computeActions(mappings: MappingStore?): List<DisplayAction> {
        val actions = ChawatheScriptGenerator().computeActions(mappings)
        return classify(actions)
    }
}

private fun classify(actions: EditScript): List<DisplayAction> {
    return actions.map { it.toDisplayAction() }
}
