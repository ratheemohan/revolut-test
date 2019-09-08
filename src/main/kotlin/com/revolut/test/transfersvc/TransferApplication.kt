package com.revolut.test.transfersvc

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.test.transfersvc.config.TransferServiceConfig
import com.revolut.test.transfersvc.resources.TransferResource
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

internal class TransferApplication : Application<TransferServiceConfig>() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>){
            TransferApplication().run(*args)
        }
    }

    override fun run(config: TransferServiceConfig, environment: Environment) {
        val dbiFactory = DBIFactory()
        val jdbi = dbiFactory.build(environment, config.dataSourceFactory, "dbi")

        environment.jersey().register(TransferResource())
    }

    override fun getName(): String {
        return "transfer-service"
    }

    override fun initialize(bootstrap: Bootstrap<TransferServiceConfig>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
        bootstrap.addBundle(object: MigrationsBundle<TransferServiceConfig>() {

            override fun getDataSourceFactory(configuration: TransferServiceConfig): DataSourceFactory {
                return configuration.dataSourceFactory
            }

            override fun getMigrationsFileName(): String {
                return "db-master-changelog.xml"
            }
        })
    }
}