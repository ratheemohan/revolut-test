package com.revolut.test.transfersvc.domain

import java.math.BigDecimal
import java.time.Instant

internal class Transaction(
        val id: String,
        val accountId: String,
        val amount: BigDecimal,
        val transactionType: TransactionType,
        val issuedAt: Instant
)

enum class TransactionType {
    IN, OUT
}