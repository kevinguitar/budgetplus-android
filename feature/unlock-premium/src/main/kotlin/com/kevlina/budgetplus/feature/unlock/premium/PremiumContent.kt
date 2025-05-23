package com.kevlina.budgetplus.feature.unlock.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.containerPadding

@Composable
fun PremiumContent(
    premiumPricing: String?,
    getPremium: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = LocalAppColors.current.light,
                shape = AppTheme.dialogShape
            )
            .verticalScroll(rememberScrollState())
            .containerPadding()
            .padding(16.dp)
    ) {

        InvestAnimation(modifier = Modifier.size(280.dp, 200.dp))

        Text(
            text = stringResource(id = R.string.premium_unlock),
            fontSize = FontSize.HeaderLarge,
            fontWeight = FontWeight.SemiBold
        )

        if (premiumPricing == null) {
            InfiniteCircularProgress(
                modifier = Modifier.size(32.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(id = R.string.premium_pricing, premiumPricing),
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.premium_description),
            fontSize = FontSize.SemiLarge,
            lineHeight = 32.sp,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            enabled = premiumPricing != null,
            onClick = getPremium
        ) {
            Text(
                text = stringResource(id = R.string.premium_unlock_cta),
                color = LocalAppColors.current.light,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}