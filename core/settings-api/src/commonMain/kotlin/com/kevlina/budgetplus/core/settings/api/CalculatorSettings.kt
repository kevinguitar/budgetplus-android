package com.kevlina.budgetplus.core.settings.api

import androidx.datastore.preferences.core.stringPreferencesKey
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.local.Preference
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

/**
 * The character(s) emitted by the calculator's dual-purpose button that sits next to '1'.
 * For currencies with large, fraction-less amounts (e.g. Vietnamese đồng), users can switch
 * it to "00" for faster input.
 */
@Serializable
enum class CalculatorButtonType(val text: String) {
    Dot("."),
    DoubleZero("00"),
}

@SingleIn(AppScope::class)
@Inject
class CalculatorSettings(
    @AppCoroutineScope private val appScope: CoroutineScope,
    private val preference: Preference,
    private val tracker: Tracker,
) {
    private val buttonTypeKey = stringPreferencesKey("calculatorButtonType")

    val buttonType: StateFlow<CalculatorButtonType> = preference.of(
        key = buttonTypeKey,
        serializer = CalculatorButtonType.serializer(),
        default = CalculatorButtonType.Dot,
        scope = appScope
    )

    fun setButtonType(type: CalculatorButtonType) {
        appScope.launch {
            preference.update(buttonTypeKey, CalculatorButtonType.serializer(), type)
        }
        tracker.logEvent(
            event = "calculator_button_type_changed",
            params = mapOf("calculator_button_type" to when (type) {
                CalculatorButtonType.Dot -> "dot"
                CalculatorButtonType.DoubleZero -> "double_zero"
            })
        )
    }

    fun toggleButtonType() {
        setButtonType(
            when (buttonType.value) {
                CalculatorButtonType.Dot -> CalculatorButtonType.DoubleZero
                CalculatorButtonType.DoubleZero -> CalculatorButtonType.Dot
            }
        )
    }
}
