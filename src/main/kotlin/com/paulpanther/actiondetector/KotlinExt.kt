package com.paulpanther.actiondetector

fun String.nullIfEmpty() = ifEmpty { null }

fun <T, K> List<Pair<T, K>>.toMutableMap() = this.toMap().toMutableMap()

fun <K, V> Map<K, V>.find(value: V) = keys.find { this[it] == value }
//fun <K, V> MutableMap<K, V>.find(value: V) = toMap().find(value)
