package com.paulpanther.actiondetector

object Timer {
    val times = mutableMapOf<String, MutableList<Long>>()

    inline fun <T> time(key: String, b: () -> T): T {
        val start = System.currentTimeMillis()
        val out = b()
        val t = System.currentTimeMillis() - start

        times.getOrPut(key) { mutableListOf() } += t
        print()

        return out
    }

    fun print() {
        println("==== TIMES ====")
        for ((key, times) in times) {
            println("$key: ${times.average()}")
        }
        println()
    }
}
