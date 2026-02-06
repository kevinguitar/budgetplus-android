package com.kevlina.budgetplus.feature.add.record

interface ExpressionEvaluator {

    /**
     * @return the result of evaluating the expression.
     */
    fun evaluate(expression: String): Result

    sealed interface Result {
        data class Success(val value: Double) : Result
        data class Error(val message: String) : Result
    }
}