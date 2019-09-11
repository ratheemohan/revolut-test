package com.revolut.test.transfersvc.resources

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.revolut.test.transfersvc.api.model.AccountIdentity
import com.revolut.test.transfersvc.api.model.ErrorDetail
import com.revolut.test.transfersvc.api.model.TransferFailure
import com.revolut.test.transfersvc.api.model.TransferState.COMPLETED
import com.revolut.test.transfersvc.api.model.TransferState.FAILED
import com.revolut.test.transfersvc.api.model.TransferSuccessful
import com.revolut.test.transfersvc.fixtures.Fixtures.defaultTransferRequest
import com.revolut.test.transfersvc.resources.TransferResource.Companion.TRANSFER_RESOURCE_PATH
import com.revolut.test.transfersvc.service.TransferService
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE
import javax.ws.rs.core.Response


@ExtendWith(DropwizardExtensionsSupport::class)
class TransferResourceTest {

    private val transferService: TransferService = mock()

    private val rule = ResourceExtension.builder()
            .setMapper(ObjectMapper().registerModule(KotlinModule()))
            .addProvider(TransferResource(transferService))
            .build()

    @Test
    fun `should return transfer successful response`() {
        whenever(transferService.transfer(any())).thenReturn(TransferSuccessful(requestId = "1"))

        val response: Response = rule.client().target(TRANSFER_RESOURCE_PATH)
                .request()
                .post(Entity.entity(defaultTransferRequest(), APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(200)

        with(response.readEntity(TransferSuccessful::class.java)){
            assertThat(requestId).isEqualTo("1")
            assertThat(state).isEqualTo(COMPLETED)
        }
    }

    @Test
    fun `should return transfer fail response`() {
        val request = defaultTransferRequest()
        val expectedError = ErrorDetail(code = "source.account.insufficient-funds")

        whenever(transferService.transfer(any())).thenReturn(TransferFailure(request.requestId, expectedError))

        val response: Response = rule.client().target(TRANSFER_RESOURCE_PATH)
                .request()
                .post(Entity.entity(defaultTransferRequest(), APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(500)

        with(response.readEntity(TransferFailure::class.java)){
            assertThat(requestId).isEqualTo(request.requestId)
            assertThat(state).isEqualTo(FAILED)
            assertThat(errorDetail).isEqualTo(expectedError)
        }
    }

    @Test
    fun `should fail validation on amount precision`() {
        //allowed precision is 3
        val invalidAmount: BigDecimal = BigDecimal.valueOf(20.12344)
        val response: Response = rule.client().target(TRANSFER_RESOURCE_PATH)
                .request()
                .post(Entity.entity(defaultTransferRequest().copy(amount = invalidAmount), APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(422)
    }

    @Test
    fun `should fail validation on account number missing`() {
        val inValidSourceAccountNumber = AccountIdentity(sortCode = "400411", accountNumber = "")
        val invalidTransferRequest = defaultTransferRequest().copy(source = inValidSourceAccountNumber)

        val response: Response = rule.client().target(TRANSFER_RESOURCE_PATH)
                .request()
                .post(Entity.entity(invalidTransferRequest, APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(422)
    }
}


