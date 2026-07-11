package com.codewithdipesh.kanasensei.ui.components.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Platform handle to a native web view — Android [android.webkit.WebView], iOS `WKWebView`.
 *
 * Mirrors the [com.codewithdipesh.kanasensei.ui.components.soundPlayer.AudioManager] pattern:
 * create one with [rememberWebViewController], then place a [WebView] composable that binds to it.
 * Calls made before the view is laid out are remembered — the latest request wins.
 */
expect class WebViewController {

    // Render a raw HTML document
    fun loadHtml(html: String)

    // Load a remote page by URL.
    fun loadUrl(url: String)

    // Reload whatever was last loaded.
    fun reload()
}

// What the controller has been asked to display. The hosting [WebView] applies the latest one.
internal sealed interface WebRequest {
    data class Html(val html: String) : WebRequest
    data class Url(val url: String) : WebRequest
}

@Composable
expect fun rememberWebViewController(): WebViewController

//Hosts the platform web view and keeps it in sync with [controller].
@Composable
expect fun WebView(
    controller: WebViewController,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
)