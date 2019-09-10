package com.revolut.test.transfersvc.validation

import java.math.BigDecimal
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.reflect.KClass

@Target(FIELD, PROPERTY)
@Retention(RUNTIME)
@Constraint(validatedBy = [PrecisionValidator::class])
annotation class Precision(
        val value: Int,
        val message: String = "{com.revolut.test.transfersvc.validation.Precision.message}",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class PrecisionValidator : ConstraintValidator<Precision, BigDecimal> {

    private var precision: Int = 0

    override fun initialize(constraintAnnotation: Precision) {
        precision = constraintAnnotation.value
    }

    override fun isValid(value: BigDecimal?, context: ConstraintValidatorContext): Boolean {
        return value == null || value.scale() <= precision
    }
}