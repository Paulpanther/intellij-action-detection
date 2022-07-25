package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.gen.Register
import com.github.gumtreediff.gen.treesitter.AbstractTreeSitterGenerator

@Register(id = "kotlin-treesitter", accept = ["\\.kt$"], priority = 25)
class KotlinTreeSitterGenerator : AbstractTreeSitterGenerator() {
    override fun getParserName(): String {
        return "kotlin"
    }

    companion object {
        private const val JAVA_PARSER_NAME = "kotlin"
    }
}
