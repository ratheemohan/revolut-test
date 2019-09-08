package com.revolut.test.transfersvc

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.test.transfersvc.config.TransferServiceConfig
import com.revolut.test.transfersvc.resources.TransferResource
import com.revolut.test.transfersvc.service.DefaultTransferService
import com.revolut.test.transfersvc.util.LiquibaseMigrateOnBootstrap
import io.dropwizard.Application
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import java.util.function.Function
import java.util.function.Supplier

internal class TransferApplication : Application<TransferServiceConfig>() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TransferApplication().run(*args)
        }
    }

    override fun run(config: TransferServiceConfig, environment: Environment) {
        val dbiFactory = DBIFactory()
        val dbi = dbiFactory.build(environment, config.dataSourceFactory, "dbi")
        val transferService = DefaultTransferService(dbi)

        environment.run {

            jersey().register(TransferResource(transferService))

            lifecycle().manage(LiquibaseMigrateOnBootstrap(
                    Supplier { LiquibaseMigrateOnBootstrap.create(dbi.open(), Function { handle -> handle.connection }) },
                    config.liquibaseChangeLog
            ))
        }
    }

    override fun getName(): String {
        return "transfer-service"
    }

    override fun initialize(bootstrap: Bootstrap<TransferServiceConfig>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
    }
}