package com.revolut.test.transfersvc.setup

import com.revolut.test.transfersvc.util.LiquibaseChangeLogExecutor
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.dropwizard.jdbi.args.InstantArgumentFactory
import io.dropwizard.jdbi.args.InstantMapper
import io.dropwizard.jdbi.args.OptionalArgumentFactory
import org.skife.jdbi.v2.DBI

class TestDb(private val liquibaseChangeLog: String, private val jdbcUrl: String) {

    constructor() : this("db-master-changelog.xml", "jdbc:h2:mem:test")

    val ds: HikariDataSource = createDataSource().also { runLiquibase(it) }
    val dbi: DBI = createHikariConnectionPool(ds)

    private fun createDataSource(): HikariDataSource {
        val cfg = HikariConfig()
        cfg.jdbcUrl = jdbcUrl
        return HikariDataSource(cfg)
    }

    private fun createHikariConnectionPool(dataSource: HikariDataSource): DBI {
        val cfg = HikariConfig()
        cfg.jdbcUrl = jdbcUrl
        // need a pool for the in-memory db to survive until the next connection is opened
        val dbi = DBI(dataSource)

        // would be nice if I could use the configuration from dropwizard (DBIFactory.configure)
        // for that I need to boot up the whole dropwizard server which I didn't want to do
        // so registering mappers that I actually use manually here
        dbi.registerColumnMapper(InstantMapper())
        dbi.registerArgumentFactory(InstantArgumentFactory())
        dbi.registerArgumentFactory(OptionalArgumentFactory(""))

        return dbi
    }

    private fun runLiquibase(dataSource: HikariDataSource) {
        LiquibaseChangeLogExecutor(dataSource.connection, liquibaseChangeLog).start()
    }
}