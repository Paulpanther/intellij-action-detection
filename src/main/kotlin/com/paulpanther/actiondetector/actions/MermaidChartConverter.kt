package com.paulpanther.actiondetector.actions

fun Graph.convertToMermaidChart(): String {
    return "flowchart TB\n" + flatMap { (_, edges) ->
        edges.outgoing.map { edge ->
            "\t${edge.origin.id}-- ${edge.actions.map { it.displayName }.concat(", ")} -->${edge.destination.id}\n"
        }
    }.concat()
}

private fun List<String>.concat(delimiter: String = "") =
    if (isEmpty()) "" else reduce { acc, s -> acc + delimiter + s }