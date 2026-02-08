package com.kevlina.budgetplus.core.common

import com.notkamui.keval.keval
import dev.zacsweers.metro.Inject

@Inject
class ExpressionEvaluator {

    /**
     * @return the result of evaluating the expression.
     */
    fun evaluate(expression: String): Result {
        return try {
            Result.Success(expression.keval())
        } catch (e: Exception) {
            Result.Error(e.message.orEmpty())
        }
    }

    sealed interface Result {
        data class Success(val value: Double) : Result
        data class Error(val message: String) : Result
    }
}