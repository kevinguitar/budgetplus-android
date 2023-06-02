package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.SmallFloatingActionButton

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BoxScope.SearchButton(
    isVisible: Boolean,
    navigator: Navigator,
) {

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd)
    ) {

        SmallFloatingActionButton(
            onClick = {
                navigator.navigate(HistoryDest.Search.route)
            }
        ) {

            Image(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                colorFilter = ColorFilter.tint(LocalAppColors.current.light)
            )
        }
    }
}