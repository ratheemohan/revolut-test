package com.revolut.test.transfersvc.util

import java.util.*

interface IdGenerator {
    fun generateUUID(): String
}

class DefaultIdGenerator : IdGenerator {
    override fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}