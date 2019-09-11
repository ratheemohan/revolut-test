package com.revolut.test.transfersvc.api.model

import com.revolut.test.transfersvc.validation.Precision
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

data class TransferRequest(
        @field:NotEmpty(message = "requestId must not be empty")
        @field:Length(max = 36)
        val requestId: String,

        @field:Valid
        val source: AccountIdentity,

        @field:Valid
        val target: AccountIdentity,

        @field:Min(0)
        @field:Precision(value = 3)
        val amount: BigDecimal,

        @field:Length(max = 255)
        val description: String
)

data class AccountIdentity(
        @field:NotEmpty(message = "sort code must not be empty")
        val sortCode: String,

        @field:NotEmpty(message = "account number must not be empty")
        val accountNumber: String
)