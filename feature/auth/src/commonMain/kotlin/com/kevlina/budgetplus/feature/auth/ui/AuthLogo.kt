package com.kevlina.budgetplus.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.img_auth
import org.jetbrains.compose.resources.painterResource

@Composable
fun AuthLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.img_auth),
        contentDescription = null,
        modifier = modifier
    )
}

@Preview
@Composable
private fun AuthLogo_Preview() = AuthLogo()