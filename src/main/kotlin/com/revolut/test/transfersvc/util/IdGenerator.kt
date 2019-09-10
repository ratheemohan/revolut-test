package com.revolut.test.transfersvc.util

import java.util.*

/**
 * Single function interface to generate ids
 */
interface IdGenerator {
    fun generateId(): String
}

object UUIDGenerator : IdGenerator {
    override fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}