package com.revolut.test.transfersvc.api.model

sealed class TransferResult
data class TransferSuccessful(val message: String) : TransferResult()
data class TransferFailure(val errorDetail: ErrorDetail) : TransferResult()

data class ErrorDetail(val code: String, val message: String = "Transfer Failed")