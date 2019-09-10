package com.revolut.test.transfersvc.setup

import com.revolut.test.transfersvc.domain.Account
import com.revolut.test.transfersvc.fixtures.Fixtures
import com.revolut.test.transfersvc.persistence.AccountRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.skife.jdbi.v2.Handle
import java.math.BigDecimal
import java.util.*

open class BaseTestSetup {

    //test accounts
    protected val janeAccount: Account = Fixtures.account(UUID.randomUUID().toString(), Fixtures.Jane_Account_Sort_Code, Fixtures.Jane_Account_Number, BigDecimal.valueOf(20))
    protected val bobAccount: Account = Fixtures.account(UUID.randomUUID().toString(), Fixtures.Bob_Account_Sort_Code, Fixtures.Bob_Account_Number, BigDecimal.valueOf(40))

    protected val testDb: TestDb = TestDb()


    @BeforeEach
    fun setUp() {
        testDb.dbi.useHandle {
            insertAccount(it, janeAccount)
            insertAccount(it, bobAccount)
        }
    }

    @AfterEach
    fun clearUp(){
        testDb.dbi.useHandle { h ->
            deleteAccountsAndTransactions(h)
        }
    }

    @AfterAll
    fun tearDown() {
        testDb.ds.close()
    }


    private fun insertAccount(handle: Handle, account: Account) {
        val accountRepo: AccountRepository = handle.attach(AccountRepository::class.java)
        accountRepo.insert(account)
    }

    private fun deleteAccountsAndTransactions(handle: Handle) {
        handle.execute("delete from transactions")
        handle.execute("delete from accounts")
    }
}