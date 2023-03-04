package com.kevlina.budgetplus.feature.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppText
import com.kevlina.budgetplus.core.ui.DropdownMenu
import com.kevlina.budgetplus.core.ui.DropdownMenuItem
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.LocalAppColors

@Composable
fun BookScreenMenu(
    navigator: Navigator,
) {

    val isPremium = false

    val icPremium by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ic_premium))

    var isMenuExpanded by remember { mutableStateOf(false) }

    Box {

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            offset = DpOffset(x = 4.dp, y = 0.dp),
            modifier = Modifier.background(color = LocalAppColors.current.light)
        ) {

            DropdownMenuItem(onClick = {
                isMenuExpanded = false
                if (isPremium) {
                    navigator.navigate(AddDest.BatchRecord.route)
                } else {
                    navigator.navigate(AddDest.UnlockPremium.route)
                }
            }) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    AppText(
                        text = stringResource(id = R.string.batch_record_title),
                        color = LocalAppColors.current.primarySemiDark,
                        fontSize = FontSize.SemiLarge
                    )

                    LottieAnimation(
                        composition = icPremium,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(24.dp)
                    )
                }
            }

            if (!isPremium) {
                DropdownMenuItem(onClick = {
                    isMenuExpanded = false
                    navigator.navigate(AddDest.UnlockPremium.route)
                }) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        AppText(
                            text = stringResource(id = R.string.premium_hide_ads),
                            color = LocalAppColors.current.primarySemiDark,
                            fontSize = FontSize.SemiLarge
                        )

                        LottieAnimation(
                            composition = icPremium,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(24.dp)
                        )
                    }
                }
            }
        }
    }
}