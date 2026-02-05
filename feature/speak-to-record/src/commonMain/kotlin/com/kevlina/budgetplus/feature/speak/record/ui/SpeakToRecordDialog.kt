package com.kevlina.budgetplus.feature.speak.record.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.record_speech_recognition_format
import com.kevlina.budgetplus.core.lottie.loadLottieSpec
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.ExperimentalCompottieApi
import io.github.alexzhirkevich.compottie.dynamic.rememberLottieDynamicProperties
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalCompottieApi::class)
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
            val imgRecording by rememberLottieComposition { loadLottieSpec("img_recording") }

            val primaryColor = LocalAppColors.current.dark
            val dynamicProperties = rememberLottieDynamicProperties {
                shapeLayer("**") {
                    fill("Rectangle 1", "Fill 1") {
                        color { primaryColor }
                    }
                }
            }

            Image(
                painter = rememberLottiePainter(
                    composition = imgRecording,
                    iterations = Compottie.IterateForever,
                    dynamicProperties = dynamicProperties
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(width = 120.dp, height = 60.dp)
            )

            Text(
                text = stringResource(Res.string.record_speech_recognition_format),
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