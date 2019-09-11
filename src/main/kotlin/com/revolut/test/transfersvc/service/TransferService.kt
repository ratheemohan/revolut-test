package com.revolut.test.transfersvc.service

import com.revolut.test.transfersvc.api.model.*
import com.revolut.test.transfersvc.domain.*
import com.revolut.test.transfersvc.domain.TransactionType.IN
import com.revolut.test.transfersvc.domain.TransactionType.OUT
import com.revolut.test.transfersvc.persistence.AccountRepository
import com.revolut.test.transfersvc.persistence.TransactionRepository
import com.revolut.test.transfersvc.util.IdGenerator
import com.revolut.test.transfersvc.util.TimeService
import com.revolut.test.transfersvc.util.logger
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import org.skife.jdbi.v2.TransactionStatus
import org.skife.jdbi.v2.exceptions.CallbackFailedException

/**
 * Service to transfer funds from an account to other.
 */
interface TransferService {

    fun transfer(transferRequest: TransferRequest): TransferResult

}

internal class DefaultTransferService(
        private val dbi: DBI,
        private val idGenerator: IdGenerator,
        private val timeService: TimeService
) : TransferService {

    companion object {
        const val fromAccountNotFound: String = "from.account.not-found"
        const val toAccountNotFound: String = "to.account.not-found"
        const val fromAccountInSufficientFunds: String = "from.account.insufficient-funds"
        const val optimisticLocking: String = "optimistic.locking"

        val logger = logger()
    }

    override fun transfer(transferRequest: TransferRequest): TransferResult {
        logger.info("Processing transfer request $transferRequest")
        return try {
            dbi.inTransaction { handle: Handle, _: TransactionStatus ->
                val accountRepository: AccountRepository = handle.attach(AccountRepository::class.java)

                val fromAccount: Account = accountRepository.find(transferRequest.from)
                        ?: throw InvalidFromAccountException(fromAccountNotFound)

                if (fromAccount.hasEnoughFunds(transferRequest.amount).not()) {
                    throw InSufficientFundsException(fromAccountInSufficientFunds)
                }

                val toAccount: Account = accountRepository.find(transferRequest.to)
                        ?: throw InvalidToAccountException(toAccountNotFound)

                val fromAccountAfterDebit: Account = fromAccount.debit(transferRequest.amount)
                val toAccountAfterCredit: Account = toAccount.credit(transferRequest.amount)

                updateAccount(fromAccountAfterDebit, accountRepository)
                updateAccount(toAccountAfterCredit, accountRepository)

                val transactionRepository = handle.attach(TransactionRepository::class.java)

                //record outgoing transaction on fromAccount
                transactionRepository.insert(
                        buildTransaction(fromAccount.id, OUT, transferRequest)
                )

                //Incoming transaction in toAccount
                transactionRepository.insert(
                        buildTransaction(toAccount.id, IN, transferRequest)
                )

                logger.debug("Account transfer from=${transferRequest.from.accountNumber} to=${transferRequest.to.accountNumber} is successfull")
                TransferSuccessful("Success")
            }
        } catch (exception: CallbackFailedException) {
            val cause: Throwable? = exception.cause
            if (cause != null && cause is TransferFailureException) {
                logger.error("Transfer request failed with reason=${cause.reason}")
                val errorDetail = ErrorDetail(code = cause.reason)
                return TransferFailure(errorDetail)
            }
            throw exception
        }
    }

    private fun updateAccount(account: Account, accountRepository: AccountRepository) {
        val updateCount = accountRepository.updateWithVersion(account)

        if (updateCount != 1) {
            throw OptimisticLockingException(optimisticLocking)
        }
    }

    private fun buildTransaction(accountId: String, type: TransactionType, transferRequest: TransferRequest): Transaction {
        return Transaction(
                id = idGenerator.generateId(),
                accountId = accountId,
                amount = transferRequest.amount,
                type = type,
                reference = transferRequest.reference,
                issuedAt = timeService.now()
        )
    }
}

sealed class TransferFailureException(val reason: String) : RuntimeException(reason)
data class InvalidFromAccountException(val error: String) : TransferFailureException(error)
data class InvalidToAccountException(val error: String) : TransferFailureException(error)
data class InSufficientFundsException(val error: String) : TransferFailureException(error)
data class OptimisticLockingException(val error: String) : TransferFailureException(error)