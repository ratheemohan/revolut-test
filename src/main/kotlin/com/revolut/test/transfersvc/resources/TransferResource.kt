package com.revolut.test.transfersvc.resources

import com.revolut.test.transfersvc.api.model.TransferRequest
import com.revolut.test.transfersvc.api.model.TransferResult
import com.revolut.test.transfersvc.resources.TransferResource.Companion.TRANSFER_RESOURCE_PATH
import com.revolut.test.transfersvc.service.TransferService
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path(TRANSFER_RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
class TransferResource(private val transferService: TransferService) {

    companion object {
        const val TRANSFER_RESOURCE_PATH: String = "/transfers"
    }

    @POST
    fun transfer(@Valid transferRequest: TransferRequest): TransferResult {
        return transferService.transfer(transferRequest)
    }
}