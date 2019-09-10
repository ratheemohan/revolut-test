package com.revolut.test.transfersvc.util

import java.time.Instant

/**
 * Single function interface to return the current time.
 */
interface TimeService {
    fun now(): Instant
}

object DefaultTimeService: TimeService {
    override fun now(): Instant {
        return Instant.now()
    }
}