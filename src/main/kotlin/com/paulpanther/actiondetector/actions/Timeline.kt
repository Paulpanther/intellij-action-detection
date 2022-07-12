package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action

class Timeline {
    // Edges are oriented from oldest to newest snapshot
    private val graph: MutableGraph = mutableMapOf()
    private var first: Snapshot? = null
    private var last: Snapshot? = null

    val actionGraph: Graph by ::graph

    fun add(newSnap: Snapshot, newDiff: Map<Snapshot, List<Action>>) {
        graph[newSnap] = MutableEdges(
            incoming = newDiff.map { (snapshot, actions) ->
                TimelineEdge(origin = snapshot, destination = newSnap, actions)
                    .also { graph.getValue(snapshot).outgoing.add(it) }
            }.toMutableList(),
            outgoing = mutableListOf())

        first = first ?: newSnap
        last = newSnap
    }

    fun clear() {
        graph.clear()
        first = null
        last = null
    }

    fun ready() = first != null

    fun findShortestPath(): List<Action> {
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
            .flatMap { it.actions }
    }
}

typealias Graph = Map<Snapshot, Edges>

private typealias MutableGraph = MutableMap<Snapshot, MutableEdges>

interface Edges {
    val incoming: List<TimelineEdge>
    val outgoing: List<TimelineEdge>
}

private data class MutableEdges(
    override val incoming: MutableList<TimelineEdge>,
    override val outgoing: MutableList<TimelineEdge>
): Edges
data class TimelineEdge(
    val origin: Snapshot,
    val destination: Snapshot,
    val actions: List<Action>
) {
    val weight = actions.size
}
