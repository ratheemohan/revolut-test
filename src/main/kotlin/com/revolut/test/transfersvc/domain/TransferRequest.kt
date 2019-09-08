package com.revolut.test.transfersvc.domain

import java.math.BigDecimal

data class TransferRequest(
        val from: SortCodeAccountNumber,
        val to: SortCodeAccountNumber,
        val amount: BigDecimal
)

data class SortCodeAccountNumber(val sortCode: String, val accountNumber: String)