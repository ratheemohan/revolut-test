package com.revolut.test.transfersvc.resources

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.revolut.test.transfersvc.api.model.ErrorDetail
import com.revolut.test.transfersvc.api.model.TransferFailure
import com.revolut.test.transfersvc.api.model.TransferSuccessful
import com.revolut.test.transfersvc.fixtures.Fixtures.defaultTransferRequest
import com.revolut.test.transfersvc.resources.TransferResource.Companion.TRANSFER_RESOURCE_PATH
import com.revolut.test.transfersvc.service.TransferService
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
        whenever(transferService.transfer(any())).thenReturn(TransferSuccessful("Success"))

        val response: Response = rule.client().target(TRANSFER_RESOURCE_PATH)
                .request()
                .post(Entity.entity(defaultTransferRequest(), APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(200)
        assertThat(response.readEntity(TransferSuccessful::class.java)).isEqualTo(TransferSuccessful("Success"))
    }

    @Test
    fun `should return transfer fail response`(){
        val error = ErrorDetail(code = "from.account.insufficient-funds")
        whenever(transferService.transfer(any())).thenReturn(TransferFailure(error))

        val response: Response = rule.client().target(TRANSFER_RESOURCE_PATH)
                .request()
                .post(Entity.entity(defaultTransferRequest(), APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(400)
        assertThat(response.readEntity(TransferFailure::class.java)).isEqualTo(TransferFailure(error))
    }
}
