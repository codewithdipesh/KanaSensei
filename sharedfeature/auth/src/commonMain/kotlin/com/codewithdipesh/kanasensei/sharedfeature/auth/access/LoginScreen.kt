package com.codewithdipesh.kanasensei.sharedfeature.auth.access

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.auth.AuthResult
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.components.progressbar.AppLoadingIndicator
import com.codewithdipesh.kanasensei.ui.components.textfield.KanaBoxTextField
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.google_icon
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    onEmailchanged: (String) -> Unit,
    onPasswordchanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    authStatus : AuthResult,
    onGoogleSignIn : () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KanaColors.entranceBackground)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Don't worry kōhai , U r One step away to start",
            style = MaterialTheme.typography.displaySmall.copy(
                color = KanaColors.onEntranceBackground,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(80.dp))
        Text(
            text = "Log in",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Medium,
                color = KanaColors.onEntranceBackground,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(40.dp))
        //email
        Text(
            text = "Email",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = KanaColors.onEntranceBackground,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(8.dp))
        KanaBoxTextField(
            value = email,
            onValueChange = onEmailchanged,
            label = "Enter your email",
            readOnly = authStatus == AuthResult.Loading,
            isPassword = false,
            showTrailingIcon = false
        )
        Spacer(Modifier.height(20.dp))
        //password
        Text(
            text = "Password",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = KanaColors.onEntranceBackground,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(8.dp))
        KanaBoxTextField(
            value = password,
            onValueChange = onPasswordchanged,
            label = "Enter your password",
            readOnly = authStatus == AuthResult.Loading,
            isPassword = true,
            showTrailingIcon = true
        )
        Spacer(Modifier.height(40.dp))

        //buttons
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            label = "Log in",
            onClick = onLoginClick,
            clickable = authStatus != AuthResult.Loading,
            backgroundColor = KanaColors.onEntranceBackground,
            labelColor = KanaColors.entranceBackground,
        )
        Spacer(Modifier.height(10.dp))
        //divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = KanaColors.onEntranceBackground.copy(0.3f)
            )
            Text(
                text = " Or ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = KanaColors.onEntranceBackground.copy(0.3f)
                )
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = KanaColors.onEntranceBackground.copy(0.3f)
            )
        }
        Spacer(Modifier.height(10.dp))
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            label = "Sign in with Google",
            onClick = onGoogleSignIn,
            clickable = authStatus != AuthResult.Loading,
            backgroundColor = KanaColors.entranceSurface.copy(alpha = 0.2f),
            labelColor = KanaColors.onEntranceBackground,
            iconRes = Res.drawable.google_icon
        )
        Spacer(Modifier.height(80.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = KanaColors.onEntranceBackground.copy(0.5f),
                )
            )
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = KanaColors.onEntranceBackground,
                ),
                modifier = Modifier.clickable{
                    onSignUpClick()
                }
            )
        }
    }

    if(authStatus == AuthResult.Loading){
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            AppLoadingIndicator(
                color = KanaColors.onBackground,
                trackColor = KanaColors.background,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
            )
        }
    }
}
