package com.codewithdipesh.auth.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun WelcomeScreen(
    onOnboard : () -> Unit,
    onLogin : () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ){ it ->
        //background
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            //light
            Image(
                painter = painterResource(R.drawable.candle_icon),
                contentDescription = "japanese_light",
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
            )

            //flowers
            Image(
                painter = painterResource(R.drawable.sakura_icon),
                contentDescription = "sakura",
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            //a letter
            Icon(
                painter = painterResource(R.drawable.a_japanese),
                contentDescription = "japanese_letter",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.Center)
            )

            //fan
            Image(
                painter = painterResource(R.drawable.fan_icon),
                contentDescription = "sakura",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            )
        }

        //title & buttons
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 80.dp)
        ){
           //title and subtitle
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(0.7f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Learn kana the correct way",
                    style = KanaSenseiTypography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Start
                    )
                )
                Text(
                    text = "Bite-sized lessons, practice sessions, and stroke-tracking.",
                    style = KanaSenseiTypography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                )
            }
            //buttons
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppButton(
                    label = "Get Started",
                    onClick = { onOnboard() },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.onBackground,
                    labelColor = MaterialTheme.colorScheme.onSecondary
                )
                AppButton(
                    label = "Already a User?",
                    onClick = { onLogin() },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    labelColor = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
