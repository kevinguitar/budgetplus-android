package com.kevlina.budgetplus.feature.push.notifications.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Switch
import com.kevlina.budgetplus.core.ui.Text

@Composable
internal fun SwitchBlock(
    title: String,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1F),
            fontSize = FontSize.SemiLarge,
            fontWeight = FontWeight.SemiBold
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckChanged,
        )
    }
}