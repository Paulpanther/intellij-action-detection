package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.tree.Tree
import com.jetbrains.rd.util.first
import com.paulpanther.actiondetector.find
import com.paulpanther.actiondetector.toMutableMap
import java.util.LinkedList
import java.util.Queue

private data class MethodWithChange(
    val action: Action,
    val node: Tree,
    val name: String)

private data class MethodWithChanges(
    val actions: List<Action>,
    val node: Tree,
    val name: String)

private typealias MethodGraph = Map<MethodWithChanges, MutableList<MethodWithChanges>>

object ConnectedChangesGrouper: ActionGrouper {
    override fun groupActions(actions: List<Action>): List<ActionGroup> {
        val graph = buildMethodGraph(actions)
        val connections = findConnection(graph)
        return connections.map { connection ->
            val leafs = connection.map { method ->
                ActionLeafGroup(method.name, method.actions)
            }
            ActionGroupGroup("Connected Methods", leafs)
        }
    }

    private fun findConnection(graph: MethodGraph): List<List<MethodWithChanges>> {
        val visited = graph.map { it.key to false }.toMutableMap()
        var root: MethodWithChanges
        val connections = mutableListOf<List<MethodWithChanges>>()

        while (!visited.all { it.value }) {
            root = visited.find(false)!!
            visited[root] = true
            connections += bfs(graph, visited, root)
        }

        return connections.toList()
    }

    private fun bfs(graph: MethodGraph, visited: MutableMap<MethodWithChanges, Boolean>, root: MethodWithChanges): List<MethodWithChanges> {
        val q = LinkedList<MethodWithChanges>()
        q += root
        val added = mutableListOf(root)

        while (!q.isEmpty()) {
            val v = q.pop()!!

            graph[v]!!.forEach { u ->
                if (!visited[u]!!) {
                    visited[u] = true
                    added += u
                    q += u
                }
            }
        }

        return added
    }

    private fun buildMethodGraph(actions: List<Action>): MethodGraph {
        val methods = findMethodsWithChanges(actions)

        val graph = methods.associateWith { method ->
            findCallsInMethod(method.node)
                .mapNotNull { call -> methods.find { it.name == call } }
                .toMutableList()
        }
        addReturnEdges(graph)
        return graph
    }

    private fun addReturnEdges(graph: MethodGraph) {
        for ((from, edges) in graph) {
            for (to in edges) {
                graph[to]?.let { it += from }
            }
        }
    }

    private fun findMethodsWithChanges(actions: List<Action>): List<MethodWithChanges> {
        val methods = actions.mapNotNull { action ->
            val parent = action.node.findRecursiveParentOfType("method_declaration") ?: return@mapNotNull null
            val identifier = identifierOfMethod(parent) ?: return@mapNotNull null
            MethodWithChange(action, parent, identifier)
        }
        return methods.groupBy { it.node }.values.map { allSameMethods ->
            val allActions = allSameMethods.map { it.action }
            MethodWithChanges(allActions, allSameMethods.first().node, allSameMethods.first().name)
        }
    }

    private fun findCallsInMethod(method: Tree) =
        method
            .findRecursiveChildrenOfType("method_invocation")
            .mapNotNull { identifierOfMethod(it) }

    private fun identifierOfMethod(method: Tree) = method.findChildOfType("identifier")?.label
}

