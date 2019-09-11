package com.revolut.test.transfersvc.api.model

import com.revolut.test.transfersvc.api.model.TransferState.COMPLETED
import com.revolut.test.transfersvc.api.model.TransferState.FAILED


sealed class TransferResult(open val requestId: String, val state: TransferState)
data class TransferSuccessful(override val requestId: String) : TransferResult(requestId, COMPLETED)
data class TransferFailure(override val requestId: String, val errorDetail: ErrorDetail) : TransferResult(requestId, FAILED)

data class ErrorDetail(val code: String, val message: String = "Transfer Failed")

enum class TransferState {
    COMPLETED, FAILED
}