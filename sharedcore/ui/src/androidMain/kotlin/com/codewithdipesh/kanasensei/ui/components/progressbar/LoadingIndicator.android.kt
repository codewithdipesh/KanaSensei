package com.codewithdipesh.kanasensei.ui.components.progressbar

import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun AppLoadingIndicator(
    modifier: Modifier,
    color: Color,
    trackColor: Color
) {
    ContainedLoadingIndicator(
        modifier = modifier,
        containerColor = trackColor,
        indicatorColor = color
    )
}