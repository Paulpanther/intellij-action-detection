package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.EditScript
import com.github.gumtreediff.actions.EditScriptGenerator
import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.matchers.MappingStore

/**
 * A wrapper class which measures how long it takes the passed script generator to compute actions and
 * appends the result as an action
 *
 * @param base Base script generator which performance should be measured
 */
class PerformanceProfilerScriptGenerator(
    private val base: EditScriptGenerator,
    private val numberOfRuns: Int = 1
): EditScriptGenerator {
    override fun computeActions(mappings: MappingStore?) : EditScript? {
        var actions: EditScript? = null

        val results = (1..numberOfRuns).map {
            val start = System.currentTimeMillis()
            actions = base.computeActions(mappings)
            val stop = System.currentTimeMillis()
            stop - start
        }

        actions?.add(0, PerformanceMeasure(results, actions?.size() ?: 0))

        return actions
    }
}

class PerformanceMeasure(private val results: List<Long>, private val actionCount: Int): Action(null) {
    private val min = results.minOrNull()
    private val max = results.maxOrNull()
    private val avg = results.average()

    override fun getName() = """
        Gumtree Performance
        Average: $avg ms
        Fastest: $min ms
        Slowest: $max ms
        Runs: ${results.size}
        
        Detected $actionCount actions"""
}