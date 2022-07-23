package com.kevingt.moneybook.book.bubble

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import com.kevingt.moneybook.book.bubble.vm.BubbleDest
import com.kevingt.moneybook.book.bubble.vm.BubbleTextDirection
import com.kevingt.moneybook.book.bubble.vm.BubbleViewModel
import com.kevingt.moneybook.ui.LocalAppColors

@Composable
fun Bubble() {

    val viewModel = hiltViewModel<BubbleViewModel>()

    val destination by viewModel.destination.collectAsState()
    val dest = destination

    AnimatedVisibility(
        visible = dest != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        if (dest != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = viewModel::clearDest
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(HollowCircleShape(dest = dest))
                        .background(color = LocalAppColors.current.dark.copy(alpha = 0.8F))
                )

                Text(
                    text = stringResource(id = dest.textRes),
                    color = LocalAppColors.current.dark,
                    modifier = Modifier
                        .align(
                            when (dest.textDirection) {
                                BubbleTextDirection.TopStart, BubbleTextDirection.BottomStart -> Alignment.TopStart
                                BubbleTextDirection.TopEnd, BubbleTextDirection.BottomEnd -> Alignment.TopEnd
                            }
                        )
                        .offset(
                            x = 0.dp,
                            y = with(LocalDensity.current) {
                                when (dest.textDirection) {
                                    BubbleTextDirection.TopStart, BubbleTextDirection.TopEnd -> {
                                        dest.offset.y.toDp() - dest.size.height.toDp()
                                    }
                                    BubbleTextDirection.BottomStart, BubbleTextDirection.BottomEnd -> {
                                        dest.offset.y.toDp() + dest.size.height.toDp()
                                    }
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

class HollowCircleShape(private val dest: BubbleDest) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
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
            addOval(
                Rect(
                    offset = dest.offset,
                    size = dest.size.toSize()
                )
            )
        }

        return Outline.Generic(Path().apply {
            op(screenRectPath, ovalPath, PathOperation.Difference)
        })
    }
}