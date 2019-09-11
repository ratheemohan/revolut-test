package com.revolut.test.transfersvc.domain

import java.math.BigDecimal
import java.time.Instant

data class Account(
        val id: String,
        val sortCode: String,
        val accountNumber: String,
        val openedAt: Instant,
        val balance: BigDecimal,
        val version: Long
)

fun Account.hasEnoughFunds(amount: BigDecimal): Boolean {
    return this.balance >= amount
}

fun Account.debit(amount: BigDecimal): Account {
    return this.copy(balance = balance.subtract(amount))
}

fun Account.credit(amount: BigDecimal): Account {
    return this.copy(balance = balance.add(amount))
}