package com.kevlina.budgetplus.feature.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.lottie.PremiumCrown
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick

private val cornerRadius = 8.dp

@Composable
internal fun SettingsItem(
    text: String,
    icon: ImageVector? = null,
    @DrawableRes iconRes: Int? = null,
    showCrownAnimation: Boolean = false,
    roundTop: Boolean = false,
    roundBottom: Boolean = false,
    action: (@Composable () -> Unit)? = null,
    verticalPadding: Dp = 16.dp,
    onClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (roundTop) 8.dp else 0.dp,
                bottom = if (roundBottom) 8.dp else 0.dp,
            )
            .clip(RoundedCornerShape(
                topStart = if (roundTop) cornerRadius else 0.dp,
                topEnd = if (roundTop) cornerRadius else 0.dp,
                bottomStart = if (roundBottom) cornerRadius else 0.dp,
                bottomEnd = if (roundBottom) cornerRadius else 0.dp,
            ))
            .background(LocalAppColors.current.lightBg)
            .rippleClick(onClick = onClick)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, top = verticalPadding, bottom = verticalPadding)
        ) {

            if (icon != null) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.dark),
                )
            }

            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.dark),
                )
            }

            if (showCrownAnimation) {
                PremiumCrown(modifier = Modifier.size(24.dp))
            }

            Text(
                text = text,
                color = LocalAppColors.current.dark,
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1F)
            )

            if (action == null) {
                Image(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalAppColors.current.dark),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(16.dp)
                )
            } else {
                action()
            }
        }

        if (!roundBottom) {
            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(color = LocalAppColors.current.light)
            )
        }
    }
}

@Preview
@Composable
private fun SettingsItem_Preview() = SettingsItem(
    icon = Icons.Rounded.Language,
    text = "語言",
    roundTop = true,
    roundBottom = true,
    onClick = {}
)