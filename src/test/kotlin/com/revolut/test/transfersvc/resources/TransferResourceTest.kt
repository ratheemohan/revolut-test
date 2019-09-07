package com.revolut.test.transfersvc.resources

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.ws.rs.core.Response

/**
 * Unit tests for [TransferResource]
 */
@ExtendWith(DropwizardExtensionsSupport::class)
internal class TransferResourceTest {

    private val resources = ResourceExtension.builder().addResource(TransferResource()).build()

    @Test
    fun `hello world`(){
        val response: Response? = resources.target(TransferResource.HELLO_WORLD_PATH).request().get()

        assertThat(response).isNotNull
        assertThat(response!!.statusInfo).isEqualTo(Response.Status.OK)
        assertThat(response.readEntity(String::class.java)).isEqualTo("Hello World!")
    }
}
