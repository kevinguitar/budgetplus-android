package com.kevingt.moneybook.ui.tour

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.ui.LocalAppColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TourBubble(
    key: String,
    text: String,
    coordinates: Pair<Dp, Dp>
) {

    val viewModel = hiltViewModel<TourViewModel>()

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        delay(500)
        visible = true
        delay(2000)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = Modifier.offset(
            x = coordinates.first,
            y = coordinates.second
        )
    ) {

        Column {

            Spacer(
                modifier = Modifier
                    .size(width = 16.dp, height = 8.dp)
                    .background(
                        color = LocalAppColors.current.dark,
                        shape = TriangleShape
                    )
            )

            Text(
                text = text,
                color = LocalAppColors.current.light,
                modifier = Modifier
                    .background(
                        color = LocalAppColors.current.dark,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            )
        }
    }
}

private val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
}