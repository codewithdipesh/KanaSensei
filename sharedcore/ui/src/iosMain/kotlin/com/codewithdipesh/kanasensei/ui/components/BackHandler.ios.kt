package com.codewithdipesh.kanasensei.ui.components

import androidx.compose.runtime.Composable

actual @Composable fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have a hardware back button. 
    // Back handling is usually done via navigation or gestures which are handled by the platform or a KMP navigation library.
}
