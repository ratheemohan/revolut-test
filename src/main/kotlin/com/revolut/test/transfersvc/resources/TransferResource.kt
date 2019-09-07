package com.revolut.test.transfersvc.resources

import com.revolut.test.transfersvc.resources.TransferResource.Companion.HELLO_WORLD_PATH
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path(HELLO_WORLD_PATH)
@Produces(MediaType.APPLICATION_JSON)
class TransferResource {

    companion object {
        const val HELLO_WORLD_PATH: String = "/helloworld"
    }

    //TODO :: remove this
    @GET
    fun hello(): String {
        return "Hello World!"
    }

}