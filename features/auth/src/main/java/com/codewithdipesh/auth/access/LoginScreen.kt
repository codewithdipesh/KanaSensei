package com.codewithdipesh.auth.access

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.codewithdipesh.data.model.auth.AuthResult
import com.codewithdipesh.ui.R
import com.codewithdipesh.ui.components.textfield.KanaTextField
import com.codewithdipesh.ui.components.buttons.AppButton
import com.codewithdipesh.ui.components.textfield.KanaBoxTextField
import com.codewithdipesh.ui.theme.KanaSenseiTypography

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
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Don't worry kōhai , U r One step away to start",
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(80.dp))
        Text(
            text = "Log in",
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(40.dp))
        //email
        Text(
            text = "Email",
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
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
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
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
            backgroundColor = MaterialTheme.colorScheme.onBackground,
            labelColor = MaterialTheme.colorScheme.tertiary,
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
                color = MaterialTheme.colorScheme.onBackground.copy(0.3f)
            )
            Text(
                text = " Or ",
                style = KanaSenseiTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.3f)
                )
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground.copy(0.3f)
            )
        }
        Spacer(Modifier.height(10.dp))
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            label = "Sign in with Google",
            onClick = onGoogleSignIn,
            clickable = authStatus != AuthResult.Loading,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            labelColor = MaterialTheme.colorScheme.onBackground,
            iconRes = R.drawable.google_icon
        )
        Spacer(Modifier.height(80.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Don't have an account?",
                style = KanaSenseiTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                    fontSize = 15.sp,
                )
            )
            Text(
                text = "Sign Up",
                style = KanaSenseiTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
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
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
            )
        }
    }
}