package com.revolut.test.transfersvc.service

import com.revolut.test.transfersvc.api.model.*
import com.revolut.test.transfersvc.domain.Account
import com.revolut.test.transfersvc.fixtures.Fixtures.Bob_Account_Number
import com.revolut.test.transfersvc.fixtures.Fixtures.Bob_Account_Sort_Code
import com.revolut.test.transfersvc.fixtures.Fixtures.Jane_Account_Number
import com.revolut.test.transfersvc.fixtures.Fixtures.Jane_Account_Sort_Code
import com.revolut.test.transfersvc.fixtures.Fixtures.defaultTransferRequest
import com.revolut.test.transfersvc.persistence.AccountRepository
import com.revolut.test.transfersvc.setup.BaseTestSetup
import com.revolut.test.transfersvc.util.DefaultIdGenerator
import com.revolut.test.transfersvc.util.DefaultTimeService
import com.revolut.test.transfersvc.util.IdGenerator
import com.revolut.test.transfersvc.util.TimeService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.skife.jdbi.v2.Handle
import java.math.BigDecimal

@TestInstance(PER_CLASS)
internal class TransferServiceTest : BaseTestSetup() {

    private val idGenerator: IdGenerator = DefaultIdGenerator()
    private val timeService: TimeService = DefaultTimeService()
    private val transferService = DefaultTransferService(testDb.dbi, idGenerator, timeService)

    @Test
    fun `should do transfer when has enough funds`() {
        val transferResult: TransferResult = transferService.transfer(defaultTransferRequest(BigDecimal.valueOf(13)))

        assertThat(transferResult).isEqualTo(TransferSuccessful("Success"))
        testDb.dbi.useHandle { handle ->
            val accountRepository: AccountRepository = handle.attach(AccountRepository::class.java)
            val janeAccount: Account = accountRepository.find(sortCode = Jane_Account_Sort_Code, accountNumber = Jane_Account_Number)
            val bobAccount: Account = accountRepository.find(sortCode = Bob_Account_Sort_Code, accountNumber = Bob_Account_Number)

            assertThat(janeAccount.balance).isEqualByComparingTo("7")
            assertThat(janeAccount.version).isEqualTo(2)
            assertThat(bobAccount.balance).isEqualByComparingTo("53")
            assertThat(bobAccount.version).isEqualTo(2)
        }
    }

    @Test
    fun `should create transactions when transferred`() {
        val transferResult: TransferResult = transferService.transfer(defaultTransferRequest(BigDecimal.valueOf(13.500)))

        assertThat(transferResult).isEqualTo(TransferSuccessful("Success"))

        testDb.dbi.useHandle { h ->
            val janeTx: MutableMap<String, Any> = h.createQuery("select * from transactions where account_id = :accountId")
                    .bind("accountId", janeAccount.id)
                    .first()
            assertThat(janeTx["amount"]).isEqualTo(BigDecimal("13.500"))
            assertThat(janeTx["type"]).isEqualTo("OUT")
            assertThat(janeTx["reference"]).isEqualTo("Thanks")

            val bobTx: MutableMap<String, Any> = h.createQuery("select * from transactions where account_id = :accountId")
                    .bind("accountId", bobAccount.id)
                    .first()
            assertThat(bobTx["amount"]).isEqualTo(BigDecimal("13.500"))
            assertThat(bobTx["type"]).isEqualTo("IN")
            assertThat(bobTx["reference"]).isEqualTo("Thanks")
        }
    }

    @Test
    fun `should fail when from account doesn't have sufficient funds`() {
        val transferResult: TransferResult = transferService.transfer(defaultTransferRequest(BigDecimal.valueOf(25)))

        assertThat(transferResult).isEqualTo(TransferFailure(ErrorDetail("from.account.insufficient-funds", "Transfer Failed")))

        testDb.dbi.useHandle { handle ->
            assertThat(transactionCount(handle)).isEqualTo(0)

            val accountRepository: AccountRepository = handle.attach(AccountRepository::class.java)
            val janeAccount: Account = accountRepository.find(sortCode = Jane_Account_Sort_Code, accountNumber = Jane_Account_Number)
            val bobAccount: Account = accountRepository.find(sortCode = Bob_Account_Sort_Code, accountNumber = Bob_Account_Number)

            assertThat(janeAccount.balance).isEqualByComparingTo("20")
            assertThat(janeAccount.version).isEqualTo(1)
            assertThat(bobAccount.balance).isEqualByComparingTo("40")
            assertThat(bobAccount.version).isEqualTo(1)
        }
    }

    @Test
    fun `should fail when from account does not exist`() {
        val withNonExistentFromAccount: TransferRequest = defaultTransferRequest(BigDecimal.valueOf(20))
                .copy(from = SortCodeAccountNumber("missing", "12345678"))

        val transferResult: TransferResult = transferService.transfer(withNonExistentFromAccount)

        assertThat(transferResult).isEqualTo(TransferFailure(ErrorDetail("from.account.not-found", "Transfer Failed")))
        testDb.dbi.useHandle { h ->
            assertThat(transactionCount(h)).isEqualTo(0)

            val repo: AccountRepository = h.attach(AccountRepository::class.java)
            //find jane account using correct sort code and account number
            val janeAccount: Account = repo.find(sortCode = Jane_Account_Sort_Code, accountNumber = Jane_Account_Number)

            //balance should remain the same
            assertThat(janeAccount.balance).isEqualByComparingTo("20")
        }
    }

    @Test
    fun `should fail when to account does not exist`() {
        val withNonExistentToAccount: TransferRequest = defaultTransferRequest(BigDecimal.valueOf(20))
                .copy(to = SortCodeAccountNumber("missing", "87654321"))

        val transferResult: TransferResult = transferService.transfer(withNonExistentToAccount)

        assertThat(transferResult).isEqualTo(TransferFailure(ErrorDetail("to.account.not-found", "Transfer Failed")))
        testDb.dbi.useHandle { h ->
            assertThat(transactionCount(h)).isEqualTo(0)

            val repo: AccountRepository = h.attach(AccountRepository::class.java)
            //find jane account and see the balance doesn't change
            val janeAccount: Account = repo.find(sortCode = Jane_Account_Sort_Code, accountNumber = Jane_Account_Number)

            //balance should remain the same
            assertThat(janeAccount.balance).isEqualByComparingTo("20")
        }
    }

    private fun transactionCount(handle: Handle): Int {
        return handle.createQuery("select count(*) from transactions")
                .map { _, r, _ -> r.getInt(1) }
                .first()
    }
}
