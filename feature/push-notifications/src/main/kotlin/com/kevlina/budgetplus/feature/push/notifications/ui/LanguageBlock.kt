package com.kevlina.budgetplus.feature.push.notifications.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField

@Composable
internal fun LanguageBlock(
    @StringRes textRes: Int,
    title: String,
    onTitleUpdate: (String) -> Unit,
    description: String,
    onDescriptionUpdate: (String) -> Unit,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {

        Text(
            text = stringResource(id = textRes),
            fontSize = FontSize.Large,
            fontWeight = FontWeight.SemiBold
        )

        TextField(
            value = title,
            onValueChange = onTitleUpdate,
            title = stringResource(id = R.string.push_notif_push_title),
            singleLine = false,
            modifier = Modifier.height(80.dp)
        )

        TextField(
            value = description,
            onValueChange = onDescriptionUpdate,
            title = stringResource(id = R.string.push_notif_push_description),
            singleLine = false,
            modifier = Modifier.height(80.dp)
        )
    }
}

@Preview
@Composable
private fun LanguageBlock_Preview() = AppTheme {
    LanguageBlock(
        textRes = R.string.push_notif_language_zh_tw,
        title = "推播標題\nasdfasdf",
        onTitleUpdate = {},
        description = "記得目標，存款不停歇！記帳確實，未來更悠遊！將花費化為理財力！GO~",
        onDescriptionUpdate = {}
    )
}