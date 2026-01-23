package com.kevlina.budgetplus.core.common

import android.content.Context
import android.graphics.Bitmap
import coil3.request.ImageRequest
import coil3.toBitmap
import dev.zacsweers.metro.Inject

@Inject
class ImageLoader(
    private val context: Context,
) {

    private val coilLoader = coil3.ImageLoader(context)

    suspend fun loadBitmap(url: String?): Bitmap? {
        url ?: return null

        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        return coilLoader.execute(request).image?.toBitmap()
    }
}