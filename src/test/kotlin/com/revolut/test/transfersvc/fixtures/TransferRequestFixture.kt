package com.revolut.test.transfersvc.fixtures

import com.revolut.test.transfersvc.api.model.SortCodeAccountNumber
import com.revolut.test.transfersvc.api.model.TransferRequest
import java.math.BigDecimal

fun defaultTransferRequest(): TransferRequest {
    val fromAccount = SortCodeAccountNumber("40-00-00", "12345678")
    val toAccount = SortCodeAccountNumber("40-00-01", "87654321")
    return TransferRequest(
            from = fromAccount,
            to = toAccount,
            amount = BigDecimal.valueOf(20)
    )
}
