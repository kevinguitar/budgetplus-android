package com.kevlina.budgetplus.book.category

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.LocalAppColors

@Composable
fun CategoryCell(
    category: String,
    isDragging: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {

    val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LocalAppColors.current.light,
        border = BorderStroke(1.dp, LocalAppColors.current.primaryLight),
        elevation = elevation,
        onClick = onClick,
        modifier = modifier
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = null,
                tint = LocalAppColors.current.dark,
                modifier = Modifier.size(20.dp)
            )

            AppText(
                text = category,
            )
        }
    }
}