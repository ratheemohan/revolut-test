package com.revolut.test.transfersvc

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.test.transfersvc.config.TransferServiceConfig
import com.revolut.test.transfersvc.resources.TransferResource
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

internal class TransferService : Application<TransferServiceConfig>() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>){
            TransferService().run(*args)
        }
    }

    override fun run(config: TransferServiceConfig, environment: Environment) {
        environment.jersey().register(TransferResource())
    }

    override fun getName(): String {
        return "transfer-service"
    }

    override fun initialize(bootstrap: Bootstrap<TransferServiceConfig>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
    }
}