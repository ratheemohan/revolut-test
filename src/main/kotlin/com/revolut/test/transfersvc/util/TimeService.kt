package com.revolut.test.transfersvc.util

import java.time.Instant

interface TimeService {
    fun now(): Instant
}

class DefaultTimeService: TimeService {
    override fun now(): Instant {
        return Instant.now()
    }
}