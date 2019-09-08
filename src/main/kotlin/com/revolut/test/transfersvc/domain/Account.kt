package com.revolut.test.transfersvc.domain

import java.math.BigDecimal
import java.time.Instant

internal data class Account(
        val id: String,
        val sortCode: String,
        val accountNumber: String,
        val openedAt: Instant,
        val balance: BigDecimal,
        val version: Long
)