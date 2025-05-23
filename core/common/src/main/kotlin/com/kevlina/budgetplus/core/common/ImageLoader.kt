package com.kevlina.budgetplus.core.common

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val coilLoader = Coil.imageLoader(context)

    suspend fun loadBitmap(url: String?): Bitmap? {
        url ?: return null

        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        return coilLoader.execute(request).drawable?.toBitmap()
    }
}