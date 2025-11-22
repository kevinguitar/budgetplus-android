package com.kevlina.budgetplus.feature.push.notifications

import com.google.auth.ApiKeyCredentials
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

internal class Translator @Inject constructor(
    @Named("google_api_key") googleApiKey: String
) {

    private val translationService by lazy {
        TranslateOptions.newBuilder()
            .setCredentials(ApiKeyCredentials.create(googleApiKey))
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