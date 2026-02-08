package com.notkamui.keval

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

// Copied from https://github.com/notKamui/Keval/tree/main
// iOS target isn't working: https://github.com/notKamui/Keval/issues/60
/**
 * Main class for evaluating mathematical expressions.
 * It can be customized with additional operators, functions, and constants.
 */
class Keval internal constructor(private val resources: Map<String, KevalOperator>) {

    companion object {

        /**
         * Creates a new instance of [Keval] with the provided resources.
         *
         * @param generator A lambda function that configures a KevalBuilder instance.
         * @return The new instance of Keval.
         * @throws KevalDSLException If one of the fields isn't set properly.
         */
        @JvmStatic
        fun create(generator: KevalBuilder.() -> Unit = { includeDefault() }): Keval =
            KevalBuilder().apply(generator).build()

        /**
         * Evaluates a mathematical expression using the default resources.
         *
         * @param mathExpression The mathematical expression to evaluate.
         * @return The result of the evaluation.
         * @throws KevalInvalidSymbolException If there's an invalid operator in the expression.
         * @throws KevalInvalidExpressionException If the expression is invalid (i.e., mismatched parentheses).
         * @throws KevalZeroDivisionException If a division by zero occurs.
         */
        @JvmName("evaluate")
        @JvmStatic
        fun eval(
            mathExpression: String,
        ): Double = mathExpression.toAST(KevalBuilder.DEFAULT_RESOURCES).eval()
    }
}

/**
 * Evaluates a mathematical expression using the default resources.
 *
 * @receiver The mathematical expression to evaluate.
 * @return The result of the evaluation.
 * @throws KevalInvalidSymbolException If there's an invalid operator in the expression.
 * @throws KevalInvalidExpressionException If the expression is invalid (i.e., mismatched parentheses).
 * @throws KevalZeroDivisionException If a division by zero occurs.
 */
fun String.keval(): Double = Keval.eval(this)
