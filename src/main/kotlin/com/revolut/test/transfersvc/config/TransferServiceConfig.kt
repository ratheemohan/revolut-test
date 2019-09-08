package com.revolut.test.transfersvc.config

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory

class TransferServiceConfig(
        @JsonProperty("db") val dataSourceFactory: DataSourceFactory,
        val liquibaseChangeLog: String = "db-master-changelog.xml"
) : Configuration()