package com.kevlina.budgetplus.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.theme.LocalAppColors

@Composable
fun RecordTypeTab(
    selected: RecordType,
    onTypeSelected: (RecordType) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {

        TypePill(
            text = stringResource(id = R.string.record_expense),
            isSelected = selected == RecordType.Expense,
            onClick = { onTypeSelected(RecordType.Expense) }
        )

        Spacer(
            modifier = Modifier
                .size(width = 2.dp, height = 24.dp)
                .background(
                    color = LocalAppColors.current.dark,
                    shape = CircleShape
                )
        )

        TypePill(
            text = stringResource(id = R.string.record_income),
            isSelected = selected == RecordType.Income,
            onClick = { onTypeSelected(RecordType.Income) }
        )
    }
}

@Composable
private fun TypePill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            LocalAppColors.current.dark
        } else {
            LocalAppColors.current.light
        },
        label = "bgColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            LocalAppColors.current.light
        } else {
            LocalAppColors.current.dark
        },
        label = "textColor"
    )

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = bgColor,
        modifier = Modifier.heightIn(min = AppTheme.minSurfaceSize),
    ) {

        Text(
            text = text,
            color = textColor,
            fontSize = FontSize.SemiLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(120.dp)
                .padding(vertical = 12.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun RecordTypeTab_Preview() = AppTheme {
    RecordTypeTab(selected = RecordType.Expense, onTypeSelected = {})
}