package com.kevlina.budgetplus.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.data.remote.RecordType

@Composable
fun RecordTypeTab(
    selected: RecordType,
    onTypeSelected: (RecordType) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        TypePill(
            text = stringResource(id = R.string.record_expense),
            isSelected = selected == RecordType.Expense,
            onClick = { onTypeSelected(RecordType.Expense) }
        )

        Spacer(
            modifier = Modifier
                .size(2.dp, 24.dp)
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
        if (isSelected) {
            LocalAppColors.current.dark
        } else {
            LocalAppColors.current.light
        }
    )

    val textColor by animateColorAsState(
        if (isSelected) {
            LocalAppColors.current.light
        } else {
            LocalAppColors.current.dark
        }
    )

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = bgColor,
    ) {

        AppText(
            text = text,
            color = textColor,
            fontSize = FontSize.SemiLarge,
            fontWeight = FontWeight.Medium,
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
    RecordTypeTab(selected = RecordType.Expense) {}
}