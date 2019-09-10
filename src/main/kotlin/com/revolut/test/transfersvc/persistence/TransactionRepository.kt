package com.revolut.test.transfersvc.persistence

import com.revolut.test.transfersvc.domain.Transaction
import com.revolut.test.transfersvc.domain.TransactionType
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.sqlobject.BindBean
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet

@RegisterMapper(TransactionRowMapper::class)
internal interface TransactionRepository {
    @SqlUpdate("insert into transactions (id, account_id, amount, type, reference, issued_at) values (:id, :accountId, :amount, :type, :reference, :issuedAt)")
     fun insert(@BindBean tx: Transaction)
}

internal class TransactionRowMapper : ResultSetMapper<Transaction> {

    override fun map(index: Int, rs: ResultSet, ctx: StatementContext): Transaction {
        return Transaction(
                id = rs.getString("id"),
                accountId = rs.getString("account_id"),
                amount = rs.getBigDecimal("amount"),
                type = TransactionType.valueOf(rs.getString("type")),
                issuedAt = rs.getTimestamp("issued_at").toInstant(),
                reference = rs.getString("reference")
        )
    }
}