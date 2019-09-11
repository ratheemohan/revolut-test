package com.revolut.test.transfersvc.persistence

import com.revolut.test.transfersvc.api.model.AccountIdentity
import com.revolut.test.transfersvc.domain.Account
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.BindBean
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet

@RegisterMapper(AccountRowMapper::class)
internal interface AccountRepository {

    @SqlQuery("select * from accounts where sort_code = :sc and account_number = :accnum")
    fun find(@Bind("sc") sortCode: String, @Bind("accnum") accountNumber: String): Account

    @SqlUpdate("insert into accounts (id, version, sort_code, account_number, balance, opened_at) values (:id, :version, :sortCode, :accountNumber, :balance, :openedAt)")
    fun insert(@BindBean account: Account)

    @SqlQuery("select * from accounts where sort_code = :sortCode and account_number = :accountNumber")
    fun find(@BindBean key: AccountIdentity): Account?

    @SqlUpdate("update accounts set version = version+1, balance = :balance where id = :id and version = :version")
    fun updateWithVersion(@BindBean account: Account): Int
}

internal class AccountRowMapper : ResultSetMapper<Account> {
    override fun map(index: Int, rs: ResultSet, ctx: StatementContext): Account {
        return Account(
                id = rs.getString("id"),
                sortCode = rs.getString("sort_code"),
                accountNumber = rs.getString("account_number"),
                balance = rs.getBigDecimal("balance"),
                openedAt = rs.getTimestamp("opened_at").toInstant(),
                version = rs.getLong("version")
        )
    }
}