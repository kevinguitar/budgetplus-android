package com.kevlina.budgetplus.feature.speak.record.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.lottie.rememberColorProperty
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text

@Composable
internal fun SpeakToRecordDialog() {
    Dialog(
        // Dismiss in controlled via the parent's tap events
        onDismissRequest = { }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(180.dp)
                .background(
                    color = LocalAppColors.current.light,
                    shape = CircleShape
                ),
        ) {
            val imgRecording by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_recording))

            val primaryColor = LocalAppColors.current.dark
            val dynamicProperties = rememberLottieDynamicProperties(
                rememberColorProperty(color = primaryColor, "**", "Rectangle 1", "Fill 1"),
            )

            LottieAnimation(
                composition = imgRecording,
                iterations = LottieConstants.IterateForever,
                contentScale = ContentScale.FillWidth,
                dynamicProperties = dynamicProperties,
                modifier = Modifier.size(width = 120.dp, height = 60.dp)
            )

            Text(
                text = stringResource(R.string.record_speech_recognition_format),
                textAlign = TextAlign.Center,
                fontSize = FontSize.Small,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
private fun SpeakToRecordDialog_Preview() = AppTheme {
    SpeakToRecordDialog()
}