package com.codewithdipesh.sharedfeature.learning.home.components

@androidx.compose.runtime.Composable
actual fun GreivianceForm(
    state: com.codewithdipesh.sharedfeature.learning.home.uistates.GrievienceState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMediaSelected: (androidx.compose.ui.graphics.ImageBitmap) -> Unit,
    onRemoveMedia: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
}