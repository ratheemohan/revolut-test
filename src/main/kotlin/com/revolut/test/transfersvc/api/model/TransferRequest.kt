package com.revolut.test.transfersvc.api.model

import com.revolut.test.transfersvc.validation.Precision
import org.hibernate.validator.constraints.NotEmpty
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.Min

data class TransferRequest(
        @field:Valid
        val from: SortCodeAccountNumber,

        @field:Valid
        val to: SortCodeAccountNumber,

        @field:Min(0)
        @field:Precision(value = 2)
        val amount: BigDecimal,

        val reference: String
)

data class SortCodeAccountNumber(
        @field:NotEmpty(message = "sort code must not be empty")
        val sortCode: String,

        @field:NotEmpty(message = "account number must not be empty")
        val accountNumber: String
)