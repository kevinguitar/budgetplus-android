package com.kevlina.budgetplus.feature.add.record.fixtures

import androidx.annotation.VisibleForTesting
import com.kevlina.budgetplus.feature.add.record.ExpressionEvaluator
import com.kevlina.budgetplus.feature.add.record.ExpressionEvaluator.Result

@VisibleForTesting
class FakeExpressionEvaluator(
    private val fakeResult: Result = Result.Success(1.0),
) : ExpressionEvaluator {
    override fun evaluate(expression: String): Result = fakeResult
}