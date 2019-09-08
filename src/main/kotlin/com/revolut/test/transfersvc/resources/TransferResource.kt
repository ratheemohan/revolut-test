package com.revolut.test.transfersvc.resources

import com.revolut.test.transfersvc.domain.TransferRequest
import com.revolut.test.transfersvc.domain.TransferResult
import com.revolut.test.transfersvc.domain.TransferSuccessful
import com.revolut.test.transfersvc.resources.TransferResource.Companion.TRANSFER_RESOURCE_PATH
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path(TRANSFER_RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
class TransferResource {

    companion object {
        const val TRANSFER_RESOURCE_PATH: String = "/transfers"
    }

    @POST
    fun transfer(@Valid transferRequest: TransferRequest): TransferResult {
        return TransferSuccessful("Success!")
    }
}