@file:JvmName("WebViewAndroid")
package com.codewithdipesh.kanasensei.ui.components.webview

import android.annotation.SuppressLint
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView as AndroidWebView

actual class WebViewController {

    // Observable so the hosting WebView composable recomposes and re-applies on every change.
    internal var request by mutableStateOf<WebRequest?>(null)
        private set

    // Bumped by reload() purely to force a recomposition that re-applies the current request.
    internal var reloadTick by mutableStateOf(0)
        private set

    actual fun loadHtml(html: String) {
        request = WebRequest.Html(html)
    }

    actual fun loadUrl(url: String) {
        request = WebRequest.Url(url)
    }

    actual fun reload() {
        reloadTick++
    }
}

@Composable
actual fun rememberWebViewController(): WebViewController = remember { WebViewController() }

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun WebView(
    controller: WebViewController,
    modifier: Modifier,
    backgroundColor: Color,
) {
    // Read state in the composable body so changes trigger AndroidView's update below.
    val request = controller.request
    @Suppress("UNUSED_VARIABLE") val reloadTick = controller.reloadTick

    AndroidView(
        modifier = modifier,
        factory = { context ->
            AndroidWebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
            }
        },
        update = { view ->
            view.setBackgroundColor(backgroundColor.toArgb())
            when (val r = request) {
                is WebRequest.Html ->
                    view.loadDataWithBaseURL(null, r.html, "text/html", "utf-8", null)
                is WebRequest.Url -> view.loadUrl(r.url)
                null -> Unit
            }
        }
    )
}