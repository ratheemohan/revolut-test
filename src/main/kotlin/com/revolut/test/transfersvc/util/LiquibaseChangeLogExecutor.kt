package com.revolut.test.transfersvc.util

import io.dropwizard.lifecycle.Managed
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.Connection
import java.util.function.Function
import java.util.function.Supplier

class LiquibaseChangeLogExecutor(
        private val connectionSupplier: Supplier<ClosableConnectionSupplier>,
        private val changelogFile: String = "db-master-changelog.xml"
) : Managed {

    interface ClosableConnectionSupplier : AutoCloseable, Supplier<Connection>

    companion object {
        fun <T : AutoCloseable> create(closable: T, connectionSupplier: Function<T, Connection>): ClosableConnectionSupplier {
            return object : ClosableConnectionSupplier {
                @Throws(Exception::class)
                override fun close() {
                    closable.close()
                }

                override fun get(): Connection {
                    return connectionSupplier.apply(closable)
                }
            }
        }
    }

    @Throws(Exception::class)
    override fun start() {
        this.connectionSupplier.get().use { connectionSupplier ->
            val liquibase = Liquibase(
                    changelogFile,
                    ClassLoaderResourceAccessor(),
                    JdbcConnection(connectionSupplier.get())
            )
            liquibase.update("")
        }
    }

    override fun stop() {}
}


//Kotlin standard lib doesn't have use on AutoCloseable
inline fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            close()
        } catch (closeException: Exception) {
            e.addSuppressed(closeException)
        }
        throw e
    } finally {
        if (!closed) {
            close()
        }
    }
}

