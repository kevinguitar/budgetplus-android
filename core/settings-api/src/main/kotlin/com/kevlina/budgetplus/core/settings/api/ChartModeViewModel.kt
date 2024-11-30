package com.kevlina.budgetplus.core.settings.api

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ChartMode {
    BarChart, PieChart
}

val ChartMode.icon: ImageVector
    get() = when (this) {
        ChartMode.BarChart -> Icons.Rounded.BarChart
        ChartMode.PieChart -> Icons.Rounded.PieChart
    }

@Singleton
class ChartModeViewModel @Inject constructor(
    preferenceHolder: PreferenceHolder,
    private val tracker: Tracker,
) {

    private var chartModeCache by preferenceHolder.bindObject(ChartMode.BarChart)

    private val _chartMode = MutableStateFlow(chartModeCache)
    val chartMode get() = _chartMode

    fun setChartMode(mode: ChartMode) {
        chartModeCache = mode
        _chartMode.value = mode
        tracker.logEvent(
            event = "chart_mode_changed",
            params = bundle {
                putString("chart_mode", when (mode) {
                    ChartMode.BarChart -> "bar_chart"
                    ChartMode.PieChart -> "pie_chart"
                })
            }
        )
    }
}