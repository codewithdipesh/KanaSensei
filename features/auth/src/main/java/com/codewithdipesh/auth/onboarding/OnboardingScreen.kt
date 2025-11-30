package com.codewithdipesh.auth.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.auth.components.MotivationSourceQuestion
import com.codewithdipesh.auth.components.NameAndTranslation
import com.codewithdipesh.ui.R
import com.codewithdipesh.ui.components.KanaTextField
import com.codewithdipesh.ui.components.buttons.AppButton
import com.codewithdipesh.ui.components.buttons.KanaIconButton
import com.codewithdipesh.ui.components.progressbar.HorizontalProgressBar
import com.codewithdipesh.ui.theme.KanaSenseiTypography
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPage: Int,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    HorizontalProgressBar(
                        size = totalPage,
                        currentPosition = currentPage
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ){ it ->
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 24.dp)
                    .padding(top = 30.dp)
            ){
                when(currentPage){
                    1 -> {
                        MotivationSourceQuestion(
                            onOptionClicked = { } //todo
                        )
                    }
                    2 -> {
                        NameAndTranslation(
                            modifier = Modifier,
                            value = "",
                            onValueChange = { },
                            showTranslation = true,
                            translatedValue = null
                        )
                    }
                }
            }

            //buttons
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 35.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    //back button
                    KanaIconButton(
                        iconRes = R.drawable.navigate_back_icon,
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                        size = 63.dp,
                        iconColor = MaterialTheme.colorScheme.onBackground,
                        onClick = { /* Handle back button click */ }
                    )
                    AppButton(
                        label = "Continue",
                        onClick = { /* Handle continue button click */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}



