package com.revolut.test.transfersvc.util

import io.dropwizard.lifecycle.AutoCloseableManager
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.Connection

class LiquibaseChangeLogExecutor(
        private val connection: Connection,
        private val changelogFile: String = "db-master-changelog.xml"
) : AutoCloseableManager(connection) {

    @Throws(Exception::class)
    override fun start() {
        Liquibase(changelogFile, ClassLoaderResourceAccessor(), JdbcConnection(connection)).update("")
    }
}

