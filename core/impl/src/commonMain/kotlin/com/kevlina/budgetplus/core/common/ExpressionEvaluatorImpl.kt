package com.kevlina.budgetplus.core.common

import com.kevlina.budgetplus.core.common.ExpressionEvaluator.Result
import com.notkamui.keval.Keval
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
class ExpressionEvaluatorImpl : ExpressionEvaluator {

    override fun evaluate(expression: String): Result {
        return try {
            Result.Success(Keval.eval(expression))
        } catch (e: Exception) {
            Result.Error(e.message.orEmpty())
        }
    }
}