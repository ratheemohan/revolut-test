package com.revolut.test.transfersvc.service

import com.revolut.test.transfersvc.api.model.TransferRequest
import com.revolut.test.transfersvc.api.model.TransferResult
import com.revolut.test.transfersvc.api.model.TransferSuccessful
import com.revolut.test.transfersvc.util.logger
import org.skife.jdbi.v2.DBI

interface TransferService {

    fun transfer(transferRequest: TransferRequest): TransferResult

}

internal class DefaultTransferService(private val dbi: DBI) : TransferService {

    companion object {
        val log = logger()
    }

    override fun transfer(transferRequest: TransferRequest): TransferResult {
        log.info("Processing transfer request $transferRequest")
//        return dbi.inTransaction { conn: Handle, status: TransactionStatus ->
//            TODO()
//        }

        return TransferSuccessful("Success")

    }

}