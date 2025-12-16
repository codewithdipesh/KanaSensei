package com.codewithdipesh.auth.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.ui.R
import com.codewithdipesh.ui.components.buttons.AppButton
import com.codewithdipesh.ui.theme.KanaSenseiTypography 

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { it ->
        //background
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = if(isDark) painterResource(id = R.drawable.splash_icon_dark)
                else painterResource(id = R.drawable.splash_icon_light),
                contentDescription = "splash Screen",
                modifier = Modifier.width(100.dp)
            )
        }
    }
}
