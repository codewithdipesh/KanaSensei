package com.codewithdipesh.sharedfeature.learning.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import com.codewithdipesh.sharedfeature.learning.home.uistates.GrievienceState

@Composable
expect fun GreivianceForm(
    state: GrievienceState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMediaSelected: (ImageBitmap) -> Unit,
    onRemoveMedia: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
)
