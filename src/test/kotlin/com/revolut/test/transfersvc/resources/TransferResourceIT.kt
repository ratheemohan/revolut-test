package com.revolut.test.transfersvc.resources

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.test.transfersvc.TransferApplication
import com.revolut.test.transfersvc.domain.SortCodeAccountNumber
import com.revolut.test.transfersvc.domain.TransferRequest
import com.revolut.test.transfersvc.domain.TransferResult
import com.revolut.test.transfersvc.domain.TransferSuccessful
import com.revolut.test.transfersvc.resources.TransferResource.Companion.TRANSFER_RESOURCE_PATH
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE
import javax.ws.rs.core.Response

/**
 * Unit tests for [TransferResource]
 */
@ExtendWith(DropwizardExtensionsSupport::class)
internal class TransferResourceIT {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())
    private val configPath = ResourceHelpers.resourceFilePath("config-test.yaml")
    private val rule = DropwizardAppExtension(TransferApplication::class.java, configPath)

    @Test
    fun `should successfully transfer funds`() {
        val response: Response = rule.client().target("http://localhost:${rule.localPort}$TRANSFER_RESOURCE_PATH")
                .request()
                .post(Entity.entity(transferRequest(), APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(200)
        assertThat(deserializeResponse(response, TransferSuccessful::class.java)).isEqualTo(TransferSuccessful("Success!"))
    }

    private fun transferRequest(): TransferRequest {
        val fromAccount = SortCodeAccountNumber("40-00-00", "12345678")
        val toAccount = SortCodeAccountNumber("40-00-01", "87654321")
        return TransferRequest(
                from = fromAccount,
                to = toAccount,
                amount = BigDecimal.valueOf(20)
        )
    }
    //can't read response entity as TransferResult??
    private fun deserializeResponse(response: Response, responseClass: Class<out TransferResult>) : TransferResult {
        return objectMapper.readValue(response.readEntity(String::class.java), responseClass)
    }
}
