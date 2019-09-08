package com.revolut.test.transfersvc.service

import com.nhaarman.mockitokotlin2.mock
import com.revolut.test.transfersvc.api.model.TransferSuccessful
import com.revolut.test.transfersvc.fixtures.defaultTransferRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.skife.jdbi.v2.DBI

internal class TransferServiceTest {

    private val dbi: DBI = mock()
    private val transferService = DefaultTransferService(dbi)

    @Test
    fun `should do account transfer`() {
        val transferResult = transferService.transfer(defaultTransferRequest())

        assertThat(transferResult).isEqualTo(TransferSuccessful("Success"))
    }
}
