package com.kevlina.budgetplus.core.settings.api

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ChartMode {
    BarChart, PieChart
}

val ChartMode.icon: ImageVector
    get() = when (this) {
        ChartMode.BarChart -> Icons.Rounded.BarChart
        ChartMode.PieChart -> Icons.Rounded.PieChart
    }

@SingleIn(AppScope::class)
@Inject
class ChartModeViewModel(
    preferenceHolder: PreferenceHolder,
    private val tracker: Tracker,
) {

    private var chartModeCache by preferenceHolder.bindObject(ChartMode.BarChart)

    val chartMode: StateFlow<ChartMode>
        field = MutableStateFlow(chartModeCache)

    val chartModeAnalyticsName: String
        get() = when (chartMode.value) {
            ChartMode.BarChart -> "bar_chart"
            ChartMode.PieChart -> "pie_chart"
        }

    fun setChartMode(mode: ChartMode) {
        chartModeCache = mode
        chartMode.value = mode
        tracker.logEvent(
            event = "chart_mode_changed",
            params = bundle {
                putString("chart_mode", chartModeAnalyticsName)
            }
        )
    }
}