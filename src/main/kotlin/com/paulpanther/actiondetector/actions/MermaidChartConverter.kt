package com.paulpanther.actiondetector.actions

fun Timeline.convertToMermaidChart(): String {
    return "flowchart TB\n" + actionGraph.map { (_, edges) ->
        edges.outgoing.map { edge ->
            "\t${edge.origin.id}-- ${edge.actions.map { it.displayName + " " }} --> ${edge.destination.id}\n"
        }
    }
}