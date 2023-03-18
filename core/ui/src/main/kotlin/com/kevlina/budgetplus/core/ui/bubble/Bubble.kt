package com.kevlina.budgetplus.core.ui.bubble

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.Text

@Composable
fun Bubble() {

    val viewModel = hiltViewModel<BubbleViewModel>()

    val destination by viewModel.destination.collectAsState()
    var isBubbleVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = destination) {
        isBubbleVisible = destination != null
    }

    AnimatedVisibility(
        visible = isBubbleVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        val dest = destination
        if (dest != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            isBubbleVisible = false
                            viewModel.dismissBubble()
                        }
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(HollowShape(dest = dest))
                        .background(color = LocalAppColors.current.dark.copy(alpha = 0.8F))
                )

                Text(
                    text = stringResource(id = dest.textRes),
                    modifier = Modifier
                        .align(
                            when (dest.textDirection) {
                                BubbleTextDirection.TopStart, BubbleTextDirection.BottomStart -> Alignment.TopStart
                                BubbleTextDirection.TopEnd, BubbleTextDirection.BottomEnd -> Alignment.TopEnd
                                BubbleTextDirection.TopCenter, BubbleTextDirection.BottomCenter -> Alignment.TopCenter
                            }
                        )
                        .offset(
                            x = 0.dp,
                            y = with(LocalDensity.current) {
                                when (dest.textDirection) {
                                    BubbleTextDirection.TopStart,
                                    BubbleTextDirection.TopEnd,
                                    BubbleTextDirection.TopCenter,
                                    -> dest.offset.y.toDp() - dest.size.height.toDp()

                                    BubbleTextDirection.BottomStart,
                                    BubbleTextDirection.BottomEnd,
                                    BubbleTextDirection.BottomCenter,
                                    -> dest.offset.y.toDp() + dest.size.height.toDp()
                                }
                            }
                        )
                        .padding(all = 8.dp)
                        .background(
                            color = LocalAppColors.current.light,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(all = 16.dp)
                )

            }
        }
    }
}

class HollowShape(private val dest: BubbleDest) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {

        val screenRectPath = Path().apply {
            addRect(
                Rect(
                    offset = Offset.Zero,
                    size = size
                )
            )
        }

        val ovalPath = Path().apply {

            val rect = Rect(
                offset = dest.offset,
                size = dest.size.toSize()
            )

            when (val shape = dest.shape) {
                BubbleShape.Circle -> addOval(rect)
                is BubbleShape.RoundedRect -> addRoundRect(
                    RoundRect(rect, CornerRadius(shape.corner))
                )
            }
        }

        return Outline.Generic(Path().apply {
            op(screenRectPath, ovalPath, PathOperation.Difference)
        })
    }
}