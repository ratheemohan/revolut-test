package com.revolut.test.transfersvc

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.test.transfersvc.config.TransferServiceConfig
import com.revolut.test.transfersvc.persistence.AccountRepository
import com.revolut.test.transfersvc.persistence.TransactionRepository
import com.revolut.test.transfersvc.resources.TransferResource
import com.revolut.test.transfersvc.service.DefaultTransferService
import com.revolut.test.transfersvc.util.DefaultTimeService
import com.revolut.test.transfersvc.util.LiquibaseChangeLogExecutor
import com.revolut.test.transfersvc.util.UUIDGenerator
import io.dropwizard.Application
import io.dropwizard.configuration.ResourceConfigurationSourceProvider
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.skife.jdbi.v2.DBI


class TransferApplication : Application<TransferServiceConfig>() {

    companion object {
        //to be able run on java 11
        @JvmStatic
        fun main(args: Array<String>) {
                TransferApplication().run("server", "/config.yaml")
            }
    }

    override fun run(config: TransferServiceConfig, environment: Environment) {
        val dbi: DBI = DBIFactory().build(environment, config.dataSourceFactory, "dbi")
        val accountRepository: AccountRepository = dbi.onDemand(AccountRepository::class.java)
        val transactionRepository: TransactionRepository = dbi.onDemand(TransactionRepository::class.java)
        val transferService = DefaultTransferService(dbi, UUIDGenerator, DefaultTimeService, accountRepository, transactionRepository)

        environment.run {

            jersey().register(TransferResource(transferService))

            lifecycle().manage(LiquibaseChangeLogExecutor(dbi.open().connection, config.liquibaseChangeLog))
        }
    }

    override fun getName(): String {
        return "transfer-service"
    }

    override fun initialize(bootstrap: Bootstrap<TransferServiceConfig>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
        bootstrap.configurationSourceProvider = ResourceConfigurationSourceProvider()
    }
}