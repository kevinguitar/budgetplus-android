package com.kevlina.budgetplus.auth.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors

@Composable
fun CurvedAuthTitle() {

    BoxWithConstraints {

        val title = stringResource(id = R.string.auth_welcome_title)
        val titleColor = LocalAppColors.current.light

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            drawIntoCanvas {
                val textPadding = 16.dp.toPx()
                val arcHeight = 240.dp.toPx()
                val arcWidth = maxWidth.toPx()
                Path()

                val path = Path().apply {
                    addArc(
                        oval = Rect(
                            left = 0f,
                            top = textPadding,
                            right = arcWidth,
                            bottom = arcHeight
                        ),
                        startAngleDegrees = 180F,
                        sweepAngleDegrees = 180F
                    )
                }
                it.nativeCanvas.drawTextOnPath(
                    title,
                    path.asAndroidPath(),
                    0f,
                    0f,
                    Paint().apply {
                        textSize = FontSize.HeaderXLarge.toPx()
                        color = titleColor.toArgb()
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }
    }
}