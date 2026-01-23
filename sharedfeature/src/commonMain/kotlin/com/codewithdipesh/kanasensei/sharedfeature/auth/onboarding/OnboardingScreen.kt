package com.codewithdipesh.kanasensei.sharedfeature.auth.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codewithdipesh.kanasensei.sharedfeature.auth.components.MotivationSourceQuestion
import com.codewithdipesh.kanasensei.sharedfeature.auth.components.NameAndTranslation
import com.codewithdipesh.data.model.user.MotivationSource
import com.codewithdipesh.ui.R
import com.codewithdipesh.ui.components.buttons.AppButton
import com.codewithdipesh.ui.components.buttons.KanaIconButton
import com.codewithdipesh.ui.components.progressbar.HorizontalProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onNavigateBack : () -> Unit,
    onNavigateNext : () -> Unit,
    onTranslate : () -> Unit,
    isTranslating : Boolean,
    selectedMotivatedSource : MotivationSource?,
    onChangeMotivatedSource : (MotivationSource) -> Unit,
    name: String,
    onChangeName : (String) -> Unit,
    translatedName : String?,
    currentPage : Int,
    onChangePage : (Int) -> Unit,
    totalPage : Int,
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ){
            HorizontalProgressBar(
                size = totalPage,
                currentPosition = currentPage
            )
            Spacer(Modifier.height(60.dp))

            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        // Forward → slide left
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    } else {
                        // Backward → slide right
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    }.using(
                        SizeTransform(clip = false)
                    )
                }
            ) { page ->
                when (page) {
                    1 -> MotivationSourceQuestion(
                        onOptionClicked = onChangeMotivatedSource,
                        selectedOption = selectedMotivatedSource
                    )
                    2, 3 -> NameAndTranslation(
                        modifier = Modifier,
                        value = name,
                        onValueChange = onChangeName,
                        showTranslation = page == 3,
                        translatedValue = translatedName,
                        isTranslating = isTranslating
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
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                //back button
                KanaIconButton(
                    iconRes = R.drawable.navigate_back_icon,
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    iconColor = MaterialTheme.colorScheme.onBackground,
                    onClick = {
                        if(currentPage == 1){
                            onNavigateBack()
                        }else{
                            onChangePage(currentPage - 1)
                        }
                    }
                )
                AppButton(
                    label = if(currentPage > 2) "\uD83C\uDF89 Finish" else "Continue",
                    onClick = {
                        if(currentPage > 2){
                            onNavigateNext()
                        }
                        else{
                            if(currentPage == 2){
                                onTranslate()
                            }
                            onChangePage(currentPage + 1)
                        }
                    },
                    clickable = when(currentPage){
                        1 -> selectedMotivatedSource != null
                        2 -> name.isNotEmpty()
                        3 -> !isTranslating
                        else -> true
                    }
                    ,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}



