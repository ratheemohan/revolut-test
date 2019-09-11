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
 * Service to transfer funds source to target account.
 */
interface TransferService {

    fun transfer(transferRequest: TransferRequest): TransferResult

}

internal class DefaultTransferService(
        private val dbi: DBI,
        private val idGenerator: IdGenerator,
        private val timeService: TimeService,
        private val accountRepository: AccountRepository,
        private val transactionRepository: TransactionRepository
) : TransferService {

    companion object {
        const val sourceAccountNotFound: String = "source.account.not-found"
        const val targetAccountNotFound: String = "target.account.not-found"
        const val sourceAccountInSufficientFunds: String = "source.account.insufficient-funds"
        const val optimisticLocking: String = "optimistic.locking"

        val logger = logger()
    }

    override fun transfer(transferRequest: TransferRequest): TransferResult {
        logger.info("Processing transfer request $transferRequest")
        return try {
            dbi.inTransaction { _: Handle, _: TransactionStatus ->
                val sourceAccount = validateSourceAccount(accountRepository, transferRequest)

                val targetAccount = accountRepository.find(transferRequest.target)
                        ?: throw InvalidTargetAccountException(targetAccountNotFound)

                val sourceAccountAfterDebit = sourceAccount.debit(transferRequest.amount)
                val targetAccountAfterCredit = targetAccount.credit(transferRequest.amount)

                updateAccount(sourceAccountAfterDebit, accountRepository)
                updateAccount(targetAccountAfterCredit, accountRepository)

                //record outgoing transaction on sourceAccount
                transactionRepository.insert(
                        buildTransaction(sourceAccount.id, OUT, transferRequest)
                )

                //Incoming transaction in targetAccount
                transactionRepository.insert(
                        buildTransaction(targetAccount.id, IN, transferRequest)
                )

                logger.debug("Account transfer source=${transferRequest.source.accountNumber} target=${transferRequest.target.accountNumber} is successful")
                TransferSuccessful(transferRequest.requestId)
            }
        } catch (exception: CallbackFailedException) {
            return handleTransferException(exception, transferRequest)
        }
    }

    private fun validateSourceAccount(repo: AccountRepository, request: TransferRequest): Account {
        val sourceAccount: Account = repo.find(request.source)
                ?: throw InvalidSourceAccountException(sourceAccountNotFound)

        if (sourceAccount.hasEnoughFunds(request.amount).not()) {
            throw InSufficientFundsException(sourceAccountInSufficientFunds)
        }

        return sourceAccount
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
                description = transferRequest.description,
                issuedAt = timeService.now(),
                transferRequestId = transferRequest.requestId
        )
    }

    private fun handleTransferException(exception: CallbackFailedException, transferRequest: TransferRequest): TransferFailure {
        val cause: Throwable? = exception.cause
        if (cause != null && cause is TransferFailureException) {
            logger.error("Transfer request failed with reason=${cause.reason}")
            val errorDetail = ErrorDetail(code = cause.reason)
            return TransferFailure(transferRequest.requestId, errorDetail)
        }
        throw exception
    }
}

sealed class TransferFailureException(val reason: String) : RuntimeException(reason)
data class InvalidSourceAccountException(val error: String) : TransferFailureException(error)
data class InvalidTargetAccountException(val error: String) : TransferFailureException(error)
data class InSufficientFundsException(val error: String) : TransferFailureException(error)
data class OptimisticLockingException(val error: String) : TransferFailureException(error)