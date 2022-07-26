package com.paulpanther.actiondetector

import com.github.gumtreediff.actions.model.Action
import com.paulpanther.actiondetector.actions.*
import java.io.File

fun List<ActionGroup>.generatePrReviewHtml(directory: File, content: String) {
    val file = File(directory, "review.html")
    val html = filter { !it.isClassOverview() }.convertToHtml(content)

    directory.mkdirs()
    file.createNewFile()
    file.writeText(html)
}

fun List<ActionGroup>.convertToHtml(content: String): String = this
    .map { group -> group.toHtml(content) }
    .reduce { acc, s -> "$acc\n$s" }

fun ActionGroup.toHtml(content: String): String = when (this) {
    is ActionGroupGroup -> html(content)
    is ActionLeafGroup -> html(content)
}

fun ActionGroupGroup.html(content: String) = "<details class=\"connected\"><summary>${title}</summary>\n${groups.convertToHtml(content)}\n</details>"

fun ActionLeafGroup.html(content: String) = "<details class=\"method\"><summary>${title}</summary>\n" +
        "<code class=\"code\">${codeFromContent(actions.first(), title, content) ?: "Error: Could not obtain code..."}</code>" +
        "\n</details>"

// TODO: This is a hack. Using appropriate grouper earlier would make more sense
fun ActionGroup.isClassOverview() = when (this) {
    is ActionGroupGroup -> false
    is ActionLeafGroup -> title.startsWith("class")
}

// TODO: Position might not be accurate
fun codeFromContent(action: Action, methodName: String, content: String): String? {
    val node = action.node.findRecursiveParentOfType("method_declaration") ?: return null
    return content.substring(node.pos..node.endPos)
}