package com.codewithdipesh.sharedfeature.learning.home.uistates

import androidx.compose.ui.graphics.ImageBitmap

data class GrievienceState(
    val title : String = "",
    val description : String = "",
    val attachedMedia: List<ImageBitmap> = emptyList()
)
