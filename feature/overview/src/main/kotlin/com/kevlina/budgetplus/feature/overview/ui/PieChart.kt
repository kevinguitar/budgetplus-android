package com.kevlina.budgetplus.feature.overview.ui

import android.view.MotionEvent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
internal fun PieChart(
    modifier: Modifier = Modifier,
    totalPrice: Double,
    recordGroups: Map<String, List<Record>>,
    formatPrice: (Double) -> String,
    onClick: (category: String) -> Unit,
) {

    val recordGroupSums = remember(recordGroups) {
        recordGroups.mapValues { (_, group) ->
            group.sumOf { it.price }
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        color = LocalAppColors.current.dark,
        fontSize = FontSize.Normal,
        textAlign = TextAlign.Center
    )

    var isDrawn by rememberSaveable(totalPrice, recordGroups) { mutableStateOf(false) }
    val animateAngle by animateIntAsState(
        targetValue = if (isDrawn) 360 else 0,
        label = "animateAngle"
    )

    var tapPosition by remember { mutableStateOf<Offset?>(null) }
    var pressPosition by remember { mutableStateOf<Offset?>(null) }

    val pressEffectPx = with(LocalDensity.current) { 8.dp.toPx() }
    val disallowInterceptTouchEventRequest = remember { RequestDisallowInterceptTouchEvent() }

    LaunchedEffect(totalPrice, recordGroups) {
        isDrawn = true
    }

    Canvas(
        modifier = modifier
            .padding(8.dp)
            .clip(CircleShape)
            .aspectRatio(1F)
            .pointerInteropFilter(
                requestDisallowInterceptTouchEvent = disallowInterceptTouchEventRequest
            ) { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE,
                    MotionEvent.ACTION_HOVER_ENTER,
                    MotionEvent.ACTION_HOVER_MOVE,
                    -> {
                        disallowInterceptTouchEventRequest.invoke(true)
                        pressPosition = Offset(event.x, event.y)
                    }

                    MotionEvent.ACTION_UP -> {
                        disallowInterceptTouchEventRequest.invoke(false)
                        pressPosition = null
                        tapPosition = Offset(event.x, event.y)
                    }

                    else -> {
                        disallowInterceptTouchEventRequest.invoke(false)
                        pressPosition = null
                    }
                }
                true
            }
    ) {
        var startAngle = 270f

        recordGroupSums.onEachIndexed { index, (category, sum) ->
            val angle = (animateAngle * sum / totalPrice).toFloat()
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
                val text = "$category\n${formatPrice(sum)}"
                val textMeasure = textMeasurer.measure(text, style = textStyle)

                val textRadius = center.x * 0.6
                val textAngle = startAngle + (angle / 2)

                val deltaX = textRadius * cos(textAngle.toRadians())
                val deltaY = textRadius * sin(textAngle.toRadians())

                drawText(
                    textMeasurer = textMeasurer,
                    text = text,
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
    val normalizedStartAngle = (startAngle + 360) % 360

    val distanceToCenter = sqrt((x - center).pow(2) + (y - center).pow(2))

    return distanceToCenter <= center &&
        normalizedAngle in normalizedStartAngle..(normalizedStartAngle + sweepAngle)
}

private fun Float.toRadians(): Double = this / 180.0 * PI

@Preview
@Composable
private fun PieChart_Preview() = AppTheme {
    PieChart(
        modifier = Modifier.size(300.dp),
        totalPrice = OverviewListUiState.totalPricePreview,
        recordGroups = OverviewListUiState.recordGroupsPreview,
        formatPrice = { it.toString() },
        onClick = {}
    )
}