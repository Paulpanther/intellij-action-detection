package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action

class Timeline {
    // Edges are oriented from oldest to newest snapshot
    private val graph: MutableMap<Snapshot, Edges> = mutableMapOf()
    private var first: Snapshot? = null
    private var last: Snapshot? = null

    fun add(newSnap: Snapshot, newDiff: Map<Snapshot, List<Action>>) {
        graph[newSnap] = Edges(
            incoming = newDiff.map { (snapshot, actions) ->
                TimelineEdge(origin = snapshot, destination = newSnap, actions)
                    .also { graph.getValue(snapshot).outgoing.add(it) }
            }.toMutableList(),
            outgoing = mutableListOf())

        first = first ?: newSnap
        last = newSnap
    }

    fun findShortestPath(): List<ActionWithFile> {
        val first: Snapshot = this.first ?: return listOf()
        val last: Snapshot = this.last ?: return listOf()

        val queue = mutableListOf(graph.getValue(first))
        val visited = graph.mapValues { (_, _) -> 0 }.toMutableMap()
        val distances = graph.mapValues { (_, _) -> Int.MAX_VALUE }.toMutableMap()
        val shortest = mutableMapOf<Snapshot, TimelineEdge>()
        distances[first] = 0

        while(queue.isNotEmpty()) {
            val edges = queue.removeAt(0)

            edges.outgoing.forEach { edge ->
                visited[edge.destination] = visited.getValue(edge.destination) + 1
                val dist = edge.weight + (distances.getValue(edge.origin))
                if (dist < distances.getValue(edge.destination)) {
                    distances[edge.destination] = dist
                    shortest[edge.destination] = edge
                }

                if (visited.getValue(edge.destination) == graph.getValue(edge.destination).incoming.size) {
                    queue.add(graph.getValue(edge.destination))
                }
            }
        }

        var current = last
        val path = mutableListOf<TimelineEdge>()
        while (current != first) {
            val edge = shortest.getValue(current)
            path += edge
            current = edge.origin
        }

        return path
            .reversed()
            .flatMap { it.actions.map { a -> ActionWithFile(a, it.origin.file, it.destination.file) } }
    }
}


private data class Edges(
    val incoming: MutableList<TimelineEdge>,
    val outgoing: MutableList<TimelineEdge>
)
private data class TimelineEdge(
    val origin: Snapshot,
    val destination: Snapshot,
    val actions: List<Action>
) {
    val weight = actions.size
}
