package com.paulpanther.actiondetector

import com.paulpanther.actiondetector.actions.Graph
import com.paulpanther.actiondetector.actions.convertToMermaidChart
import java.io.File

fun Graph.generateMermaidImage(directory: File) {
    val file = File(directory, "mermaid.mmd")
    val mermaid = convertToMermaidChart()

    directory.mkdirs()
    file.createNewFile()
    file.writeText(mermaid)

    Runtime.getRuntime().exec("mmdc", arrayOf("-i", ".mermaid/mermaid.mmd", "-o", "action_graph.png"))
}

private fun getMermaidFile(directory: File) = File(directory, "mermaid.mmd")