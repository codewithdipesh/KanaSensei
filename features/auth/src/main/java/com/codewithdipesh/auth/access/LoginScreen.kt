package com.codewithdipesh.auth.access

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.ui.components.KanaTextField
import com.codewithdipesh.ui.components.buttons.AppButton
import com.codewithdipesh.ui.theme.KanaSenseiTypography

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    onEmailchanged: (String) -> Unit,
    onPasswordchanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleSignIn : () -> Unit,
    onSignUpClick: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { it ->
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't worry kōhai , U r One step away to start",
                style = KanaSenseiTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Start
                )
            )
            Spacer(Modifier.height(50.dp))
            Text(
                text = "Log in",
                style = KanaSenseiTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Start
                )
            )
            Spacer(Modifier.height(25.dp))
            //email
            Text(
                text = "Email",
                style = KanaSenseiTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start
                )
            )
            Spacer(Modifier.height(8.dp))
            KanaTextField(
                value = email,
                onValueChange = onEmailchanged
            )
            Spacer(Modifier.height(20.dp))
            //password
            Text(
                text = "Email",
                style = KanaSenseiTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start
                )
            )
            Spacer(Modifier.height(8.dp))
            KanaTextField(
                value = password,
                onValueChange = onPasswordchanged
            )
            Spacer(Modifier.height(30.dp))

            //buttons
            AppButton(
                label = "Log in",
                onClick = onLoginClick,
                backgroundColor = MaterialTheme.colorScheme.onBackground,
                labelColor = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.height(16.dp))
            AppButton(
                label = "",
                onClick = onLoginClick,
                backgroundColor = MaterialTheme.colorScheme.onBackground,
                labelColor = MaterialTheme.colorScheme.onPrimary,
            )

        }
    }
}