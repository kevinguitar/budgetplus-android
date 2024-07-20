package com.kevlina.budgetplus.feature.insider.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.millisToDateTime
import com.kevlina.budgetplus.core.common.shortFormat
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import java.util.concurrent.TimeUnit

@Composable
internal fun UserCard(
    user: User,
) {

    val createOn = remember(user) {
        user.createdOn?.millisToDateTime?.shortFormat
    }

    val lastActiveOn = remember(user) {
        user.lastActiveOn?.millisToDateTime?.shortFormat
    }

    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clip(AppTheme.cardShape)
            .background(LocalAppColors.current.lightBg)
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = user.name.orEmpty(),
                    fontSize = FontSize.SemiLarge,
                    fontWeight = FontWeight.SemiBold
                )

                user.language?.let { language ->
                    Spacer(modifier = Modifier
                        .size(2.dp)
                        .clip(CircleShape)
                        .background(LocalAppColors.current.dark)
                    )

                    Text(text = stringResource(id = when (language) {
                        "zh-tw" -> R.string.insider_language_zh_tw
                        "zh-cn" -> R.string.insider_language_zh_cn
                        "ja" -> R.string.insider_language_ja
                        else -> R.string.insider_language_en
                    }))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (createOn != null) {
            Text(
                text = stringResource(id = R.string.insider_user_created_on, createOn)
            )
        }

        if (lastActiveOn != null && lastActiveOn != createOn) {
            Text(
                text = stringResource(id = R.string.insider_user_last_active_on, lastActiveOn)
            )
        }
    }
}

@Preview
@Composable
private fun UserCard_Preview() = AppTheme {
    UserCard(user = User(
        name = "Kevin Chiu",
        createdOn = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1),
        lastActiveOn = System.currentTimeMillis(),
        language = "zh-tw"
    ))
}