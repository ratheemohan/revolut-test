package com.revolut.test.transfersvc.fixtures

import com.revolut.test.transfersvc.api.model.AccountIdentity
import com.revolut.test.transfersvc.api.model.TransferRequest
import com.revolut.test.transfersvc.domain.Account
import java.math.BigDecimal
import java.time.Instant
import java.util.*


object Fixtures {

    const val Jane_Account_Sort_Code: String = "400000"
    const val Jane_Account_Number: String = "12345678"
    const val Bob_Account_Sort_Code: String = "400001"
    const val Bob_Account_Number: String = "87654321"

    fun defaultTransferRequest(balance: BigDecimal = BigDecimal.valueOf(20)): TransferRequest {
        val janeAccount = AccountIdentity(Jane_Account_Sort_Code, Jane_Account_Number)
        val bobAccount = AccountIdentity(Bob_Account_Sort_Code, Bob_Account_Number)
        return TransferRequest(
                requestId = UUID.randomUUID().toString(),
                source = janeAccount,
                target = bobAccount,
                amount = balance,
                description = "Transfer from Jane to Bob"
        )
    }

    fun account(accountId: String, sortCode: String, accountNumber: String, balance: BigDecimal): Account {
        return Account(
                id = accountId,
                sortCode = sortCode,
                accountNumber = accountNumber,
                openedAt = Instant.now(),
                balance = balance,
                version = 1
        )
    }
}
