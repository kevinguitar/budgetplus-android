package com.kevlina.budgetplus.core.common

import com.kevlina.budgetplus.core.common.ExpressionEvaluator.Result
import com.notkamui.keval.keval
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
class ExpressionEvaluatorImpl : ExpressionEvaluator {

    override fun evaluate(expression: String): Result {
        return try {
            Result.Success(expression.keval())
        } catch (e: Exception) {
            Result.Error(e.message.orEmpty())
        }
    }
}