package com.notkamui.keval

import kotlin.math.E
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cbrt
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

// Copied from https://github.com/notKamui/Keval/tree/main
// iOS target isn't working: https://github.com/notKamui/Keval/issues/60
/**
 * This class is used to build a Keval instance with custom operators, functions, and constants.
 */
class KevalBuilder internal constructor(
    baseResources: Map<String, KevalOperator> = mapOf()
) {
    private val resources: MutableMap<String, KevalOperator> = baseResources.toMutableMap()

    /**
     * Includes the default resources (operators, functions, constants) to the current Keval instance.
     */
    fun includeDefault(): KevalBuilder = apply {
        resources += DEFAULT_RESOURCES
    }

    /**
     * Defines a function for the current Keval instance.
     *
     * @param definition A lambda function that configures a FunctionBuilder instance.
     */
    fun function(definition: FunctionBuilder.() -> Unit): KevalBuilder = apply {
        val fn = FunctionBuilder().apply(definition)
        validateFunction(fn.name, fn.arity, fn.implementation)
        resources[fn.name!!] = KevalFunction(fn.arity, fn.implementation!!)
    }

    /**
     * Builds the Keval instance with the defined resources.
     *
     * @return A Keval instance.
     */
    fun build(): Keval = Keval(resources)

    private fun validateFunction(name: String?, arity: Int?, implementation: Any?) {
        requireNotNull(name) { "name is not set" }
        requireNotNull(implementation) { "implementation is not set" }
        require(arity == null || arity >= 0) { "function's arity must always be positive or 0" }
        require(name.isFunctionOrConstantName()) { "a function's name cannot start with a digit and must contain only letters, digits or underscores: $name" }
    }

    private fun String.isFunctionOrConstantName() =
        isNotEmpty() && this[0] !in '0'..'9' && !contains("[^a-zA-Z0-9_]".toRegex())

    companion object {

        val DEFAULT_RESOURCES: Map<String, KevalOperator> = mapOf(
            // binary operators
            "+" to KevalBothOperator(
                KevalBinaryOperator(2, true) { a, b -> a + b },
                KevalUnaryOperator(true) { it }
            ),
            "-" to KevalBothOperator(
                KevalBinaryOperator(2, true) { a, b -> a - b },
                KevalUnaryOperator(true) { -it }
            ),

            "/" to KevalBinaryOperator(3, true) { a, b ->
                if (b == 0.0) throw KevalZeroDivisionException()
                a / b
            },
            "%" to KevalBinaryOperator(3, true) { a, b ->
                if (b == 0.0) throw KevalZeroDivisionException()
                a % b
            },
            "^" to KevalBinaryOperator(4, false) { a, b -> a.pow(b) },
            "*" to KevalBinaryOperator(3, true) { a, b -> a * b },

            // unary operators
            "!" to KevalUnaryOperator(false) {
                if (it < 0) throw KevalInvalidArgumentException("factorial of a negative number")
                if (floor(it) != it) throw KevalInvalidArgumentException("factorial of a non-integer")
                var result = 1.0
                for (i in 2..it.toInt()) {
                    result *= i
                }
                result
            },

            // functions
            "neg" to KevalFunction(1) { -it[0] },
            "abs" to KevalFunction(1) { it[0].absoluteValue },
            "sqrt" to KevalFunction(1) { sqrt(it[0]) },
            "cbrt" to KevalFunction(1) { cbrt(it[0]) },
            "exp" to KevalFunction(1) { exp(it[0]) },
            "ln" to KevalFunction(1) { ln(it[0]) },
            "log10" to KevalFunction(1) { log10(it[0]) },
            "log2" to KevalFunction(1) { log2(it[0]) },
            "sin" to KevalFunction(1) { sin(it[0]) },
            "cos" to KevalFunction(1) { cos(it[0]) },
            "tan" to KevalFunction(1) { tan(it[0]) },
            "asin" to KevalFunction(1) { asin(it[0]) },
            "acos" to KevalFunction(1) { acos(it[0]) },
            "atan" to KevalFunction(1) { atan(it[0]) },
            "ceil" to KevalFunction(1) { ceil(it[0]) },
            "floor" to KevalFunction(1) { floor(it[0]) },
            "round" to KevalFunction(1) { round(it[0]) },

            // constants
            "PI" to KevalConstant(PI),
            "e" to KevalConstant(E)
        )

        /**
         * Builder representation of a function.
         *
         * @property name The identifier which represents the function.
         * @property arity The arity of the function (how many arguments it takes). If null, the function is variadic
         * @property implementation The actual implementation of the function.
         */
        data class FunctionBuilder(
            var name: String? = null,
            var arity: Int? = null,
            var implementation: ((DoubleArray) -> Double)? = null,
        )
    }
}
