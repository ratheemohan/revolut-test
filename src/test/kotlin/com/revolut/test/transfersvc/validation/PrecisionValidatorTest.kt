package com.revolut.test.transfersvc.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import javax.validation.Validation
import javax.validation.Validator

internal class PrecisionValidatorTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `should allow null`() {
        val testClass = TestClass()
        val violations = validator.validate(testClass)

        assertThat(violations).isEmpty()
    }

    @Test
    fun `should allow lower precision`() {
        val testClass = TestClass(BigDecimal("10.12"))
        val violations = validator.validate(testClass)

        assertThat(violations).isEmpty()
    }

    @Test
    fun `should allow equal precision`() {
        val testClass = TestClass(BigDecimal("10.1234"))
        val violations = validator.validate(testClass)

        assertThat(violations).isEmpty()
    }

    @Test
    fun `should not allow higher precision`() {
        val testClass = TestClass(BigDecimal("12.34567"))
        val violations = validator.validate(testClass)

        assertThat(violations).hasSize(1)
        val violation = violations.stream().findAny().get()
        assertThat(violation.message).isEqualTo("Invalid precision")
    }

}

private data class TestClass(@field:Precision(4) val amount: BigDecimal? = null)