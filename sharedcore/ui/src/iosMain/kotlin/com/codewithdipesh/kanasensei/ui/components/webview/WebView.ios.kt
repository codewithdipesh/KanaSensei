package com.codewithdipesh.kanasensei.ui.components.webview

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@androidx.compose.runtime.Composable
actual fun rememberWebViewController(): WebViewController {
    TODO("Not yet implemented")
}

@androidx.compose.runtime.Composable
actual fun WebView(
    controller: WebViewController,
    modifier: Modifier,
    backgroundColor: Color
) {
}

actual class WebViewController {
    actual fun loadHtml(html: String) {
    }

    actual fun loadUrl(url: String) {
    }

    actual fun reload() {
    }
}