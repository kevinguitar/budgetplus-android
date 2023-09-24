package com.kevlina.budgetplus.feature.push.notifications

import android.content.Context
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateOptions
import com.kevlina.budgetplus.core.common.R
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@Reusable
internal class Translator @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val translationService by lazy {
        @Suppress("DEPRECATION")
        TranslateOptions.newBuilder()
            .setApiKey(context.getString(R.string.google_cloud_translate_api_key))
            .build()
            .service
    }

    suspend fun translate(
        text: String,
        sourceLanCode: String?,
        targetLanCode: String,
    ): String = withContext(Dispatchers.IO) {
        try {
            translationService.translate(
                text,
                TranslateOption.sourceLanguage(sourceLanCode),
                TranslateOption.targetLanguage(targetLanCode)
            ).translatedText
        } catch (e: Exception) {
            Timber.e(e, "Translation failed, return original text as fallback.")
            text
        }
    }
}