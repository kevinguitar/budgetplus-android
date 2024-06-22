package com.kevlina.budgetplus.feature.overview.ui

import android.view.MotionEvent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.roundUpRatioText
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun PieChart(
    modifier: Modifier = Modifier,
    totalPrice: Double,
    recordGroups: Map<String, List<Record>>,
    onClick: (category: String) -> Unit,
) {

    val recordGroupPercents = remember(recordGroups) {
        recordGroups.mapValues { (_, group) ->
            group.sumOf { it.price } / totalPrice
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        color = LocalAppColors.current.dark,
        fontSize = FontSize.Small
    )

    var isDrawn by rememberSaveable(totalPrice, recordGroups) { mutableStateOf(false) }
    val animateAngle by animateIntAsState(
        targetValue = if (isDrawn) 360 else 0,
        label = "animateAngle"
    )

    var tapPosition by remember { mutableStateOf<Offset?>(null) }
    var pressPosition by remember { mutableStateOf<Offset?>(null) }

    val pressEffectPx = with(LocalDensity.current) { 4.dp.toPx() }

    LaunchedEffect(totalPrice, recordGroups) {
        isDrawn = true
    }

    Canvas(
        modifier = modifier
            .padding(12.dp)
            .clip(CircleShape)
            .aspectRatio(1F)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { tapPosition = it })
            }
            .pointerInteropFilter {
                if (it.action == MotionEvent.ACTION_UP) {
                    pressPosition = null
                } else {
                    pressPosition = Offset(it.x, it.y)
                }
                true
            }
    ) {
        var startAngle = 270f

        recordGroupPercents.onEachIndexed { index, (category, percent) ->
            val angle = (animateAngle * percent).toFloat()
            val isPressed = pressPosition?.isWithIn(startAngle, angle) ?: false

            // Consume the tap action
            tapPosition?.let { tap ->
                if (tap.isWithIn(startAngle, angle)) {
                    onClick(category)
                    tapPosition = null
                }
            }

            val arcSize = if (isPressed) {
                size
            } else {
                Size(size.width - pressEffectPx * 2, size.height - pressEffectPx * 2)
            }

            val topLeft = if (isPressed) {
                Offset.Zero
            } else {
                Offset(pressEffectPx, pressEffectPx)
            }

            drawArc(
                color = overviewColors[index % overviewColors.size],
                startAngle = startAngle,
                sweepAngle = angle,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )

            if (animateAngle >= 180) {
                val text = "$category\n${(percent * A_HUNDRED).roundUpRatioText}%"
                val textMeasure = textMeasurer.measure(text, style = textStyle)

                val textRadius = center.x * 0.6
                val textAngle = startAngle + (angle / 2)

                val deltaX = textRadius * cos(textAngle.toRadians())
                val deltaY = textRadius * sin(textAngle.toRadians())

                drawText(
                    textMeasurer = textMeasurer,
                    text = "$category\n${(percent * A_HUNDRED).roundUpRatioText}%",
                    style = textStyle,
                    topLeft = Offset(
                        x = center.x + deltaX.toFloat() - (textMeasure.size.width / 2),
                        y = center.y + deltaY.toFloat() - (textMeasure.size.height / 2)
                    )
                )
            }

            startAngle += angle
        }
    }
}

context(DrawScope)
private fun Offset.isWithIn(
    startAngle: Float,
    sweepAngle: Float,
): Boolean {
    val center = size.width / 2

    // Calculate angle of tap relative to center
    val angle = atan2(y - center, x - center) * 180f / PI
    val normalizedAngle = (angle + 360) % 360
    val normalizedStartAngle = startAngle % 360

    return normalizedAngle in normalizedStartAngle..(normalizedStartAngle + sweepAngle)
}

private fun Float.toRadians(): Double = this / 180.0 * PI

@Preview
@Composable
private fun PieChart_Preview() = AppTheme {
    PieChart(
        modifier = Modifier.size(300.dp),
        totalPrice = OverviewListUiState.totalPricePreview,
        recordGroups = OverviewListUiState.recordGroupsPreview,
        onClick = {}
    )
}