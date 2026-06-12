package com.codewithdipesh.sharedfeature.learning.lesson.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.components.webview.WebView
import com.codewithdipesh.kanasensei.ui.components.webview.rememberWebViewController
import com.codewithdipesh.kanasensei.ui.theme.KanaColors


@Composable
fun InfoView(
    content: String,
    modifier: Modifier = Modifier,
    onContinue : () -> Unit
) {
    val controller = rememberWebViewController()

    // Re-render only when the content actually changes.
    LaunchedEffect(content) {
        controller.loadHtml(buildInfoHtml(content))
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            Box(modifier = Modifier.weight(1f)) {
                WebView(
                    controller = controller,
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = Color.Transparent,
                )
            }
            AppButton(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                label = "Continue",
                onClick = onContinue,
                backgroundColor = KanaColors.background,
                labelColor = Color.White
            )
        }
    }
}

//Wraps raw [content] in a responsive HTML document themed to match the lesson screen.
private fun buildInfoHtml(content: String): String {
    val text = KanaColors.onLearningBackground.toCssHex()
    val accent = KanaColors.learningSurface.toCssHex()

    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <style>
            html, body {
              margin: 0;
              padding: 0;
              background: transparent;
            }

            body {
              color: $text;
              font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
              font-size: 18px;
              line-height: 1.6;
              padding: 24px;
              word-wrap: break-word;
              overflow-wrap: break-word;
            }

            h1, h2, h3 {
              font-weight: 700;
              line-height: 1.3;
            }

            a {
              color: $accent;
            }

            ul, ol {
              padding-left: 1.25em;
            }

            code {
              background: ${accent}33;
              border-radius: 6px;
              padding: 2px 6px;
              font-size: 0.9em;
            }
          </style>
        </head>
        <body>
          $content
        </body>
        </html>
    """.trimIndent()
}

//Compose [Color] → CSS hex string (`#rrggbb`)
private fun Color.toCssHex(): String {
    fun channel(value: Float): String =
        ((value * 255f).toInt().coerceIn(0, 255)).toString(16).padStart(2, '0')
    return "#${channel(red)}${channel(green)}${channel(blue)}"
}