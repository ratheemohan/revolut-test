package com.revolut.test.transfersvc.domain

import java.math.BigDecimal
import java.time.Instant

class Transaction(
        val id: String,
        val accountId: String,
        val amount: BigDecimal,
        val type: TransactionType,
        val issuedAt: Instant,
        val reference: String
)

enum class TransactionType {
    IN, OUT
}