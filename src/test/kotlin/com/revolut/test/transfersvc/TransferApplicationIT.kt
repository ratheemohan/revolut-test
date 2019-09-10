package com.revolut.test.transfersvc

import com.revolut.test.transfersvc.fixtures.Fixtures.defaultTransferRequest
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE
import javax.ws.rs.core.Response

/**
 * Integration tests for [TransferApplication].
 * This is just to assert that the service starts up.
 */
@ExtendWith(DropwizardExtensionsSupport::class)
internal class TransferApplicationIT {

    private val configPath = ResourceHelpers.resourceFilePath("config-test.yaml")
    private val appExtension = DropwizardAppExtension(TransferApplication::class.java, configPath)

    @Test
    fun `should start the app`() {
        val response: Response = appExtension.client().target("http://localhost:${appExtension.localPort}/api")
                .request()
                .post(Entity.entity(defaultTransferRequest(), APPLICATION_JSON_TYPE))

        assertThat(response.status).isEqualTo(404)
    }
}
