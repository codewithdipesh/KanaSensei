package com.codewithdipesh.kanasensei.ui.components.progressbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

@Composable
expect fun AppLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = KanaColors.primary,
    trackColor: Color = KanaColors.secondary
)