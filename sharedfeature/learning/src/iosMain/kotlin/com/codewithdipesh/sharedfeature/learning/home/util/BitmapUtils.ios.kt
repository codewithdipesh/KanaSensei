package com.codewithdipesh.sharedfeature.learning.home.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toByteArray(): ByteArray {
    val skiaBitmap = this.asSkiaBitmap()
    val image = Image.makeFromBitmap(skiaBitmap)
    val encoded = image.encodeToData(EncodedImageFormat.JPEG, 80)
    return encoded?.bytes ?: byteArrayOf()
}
