package com.revolut.test.transfersvc.resources

import com.revolut.test.transfersvc.api.model.TransferRequest
import com.revolut.test.transfersvc.api.model.TransferResult
import com.revolut.test.transfersvc.resources.TransferResource.Companion.TRANSFER_RESOURCE_PATH
import com.revolut.test.transfersvc.service.TransferService
import com.revolut.test.transfersvc.util.logger
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path(TRANSFER_RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
class TransferResource(private val transferService: TransferService) {

    companion object {
        val logger = logger()
        const val TRANSFER_RESOURCE_PATH: String = "/transfers"
    }

    @POST
    fun transfer(@Valid transferRequest: TransferRequest): TransferResult {
        logger.info("Received Transaction request $transferRequest")
        return transferService.transfer(transferRequest)
    }
}