package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.kevlina.budgetplus.core.common.R

@Composable
fun AuthLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.img_auth),
        contentDescription = null,
        modifier = modifier
    )
}

@Preview
@Composable
private fun AuthLogo_Preview() = AuthLogo()