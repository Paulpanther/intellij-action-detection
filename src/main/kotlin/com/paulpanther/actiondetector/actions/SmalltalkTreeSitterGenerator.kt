package com.paulpanther.actiondetector.actions

import com.github.gumtreediff.gen.Register
import com.github.gumtreediff.gen.treesitter.AbstractTreeSitterGenerator

@Register(id = "smalltalk-treesitter", accept = ["\\.stm$"], priority = 25)
class SmalltalkTreeSitterGenerator : AbstractTreeSitterGenerator() {
    override fun getParserName(): String {
        return "smalltalk"
    }

    companion object {
        private const val JAVA_PARSER_NAME = "smalltalk"
    }
}
