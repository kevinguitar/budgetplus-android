package com.kevlina.budgetplus.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick

@Composable
internal fun FilterPill(
    modifier: Modifier = Modifier,
    state: FilterPillState,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                shape = AppTheme.cardShape,
                color = if (state.selection == null) {
                    LocalAppColors.current.primary
                } else {
                    LocalAppColors.current.dark
                }
            )
            .clip(AppTheme.cardShape)
            .padding(all = 8.dp)
            .rippleClick(onClick = state.onClick)
    ) {
        Text(
            text = state.selection ?: state.placeholder.orEmpty(),
            singleLine = true,
            color = LocalAppColors.current.light,
            modifier = Modifier.padding(start = 12.dp)
        )

        Icon(
            imageVector = Icons.Rounded.ArrowDropDown,
            contentDescription = null,
            tint = LocalAppColors.current.light,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Immutable
data class FilterPillState(
    val placeholder: String?,
    val selection: String?,
    val onClick: () -> Unit,
) {
    companion object {
        val preview = FilterPillState(
            placeholder = "All categories",
            selection = null,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun FilterPill_Preview() = AppTheme {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(all = 16.dp)
    ) {
        FilterPill(state = FilterPillState.preview)
        FilterPill(
            state = FilterPillState.preview.copy(
                placeholder = null,
                selection = "Today"
            )
        )
    }
}