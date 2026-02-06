package com.kevlina.budgetplus.feature.add.record

import com.kevlina.budgetplus.feature.add.record.ExpressionEvaluator.Result
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import net.objecthunter.exp4j.ExpressionBuilder

@ContributesBinding(AppScope::class)
class ExpressionEvaluatorImpl : ExpressionEvaluator {

    override fun evaluate(expression: String): Result {
        return try {
            val expression = ExpressionBuilder(expression).build()
            val validation = expression.validate()
            if (validation.isValid) {
                Result.Success(expression.evaluate())
            } else {
                Result.Error(validation.errors.joinToString())
            }
        } catch (e: Exception) {
            Result.Error(e.message.orEmpty())
        }
    }
}