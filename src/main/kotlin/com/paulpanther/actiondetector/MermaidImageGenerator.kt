package com.paulpanther.actiondetector

import com.paulpanther.actiondetector.actions.Graph
import com.paulpanther.actiondetector.actions.convertToMermaidChart
import java.io.File

fun Graph.generateMermaidImage(directory: File) {
    val file = File(directory, "mermaid.mmd")
    val mermaid = convertToMermaidChart()

    file.createNewFile()
    file.writeText(mermaid.toString())

    Runtime.getRuntime().exec("""
cat << EOF  | mmdc -o action_graph.png
    ${mermaid}
EOF
    """)
}

private fun getMermaidFile(directory: File) = File(directory, "mermaid.mmd")