package com.codewithdipesh.kanasensei.sharedfeature.auth.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codewithdipesh.kanasensei.core.model.user.MotivationSource
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.components.buttons.KanaIconButton
import com.codewithdipesh.kanasensei.ui.components.progressbar.HorizontalProgressBar
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.navigate_back_icon
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.kanasensei.sharedfeature.auth.components.MotivationSourceQuestion
import com.codewithdipesh.kanasensei.sharedfeature.auth.components.NameAndTranslation

@Composable
fun OnboardingScreen(
    currentPage : Int,
    totalPage : Int,
    name : String,
    translatedName : String?,
    isTranslating : Boolean,
    selectedMotivatedSource : MotivationSource?,
    onChangeName : (String) -> Unit,
    onTranslate : () -> Unit,
    onChangeMotivatedSource : (MotivationSource) -> Unit,
    onChangePage : (Int) -> Unit,
    onNavigateNext : () -> Unit,
    onNavigateBack : () -> Unit,
    onPlayJapaneseName : (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KanaColors.entranceBackground)
            .systemBarsPadding()
    ){
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ){
            HorizontalProgressBar(
                size = totalPage,
                currentPosition = currentPage,
                selectedColor = KanaColors.onEntranceBackground,
                unSelectedColor = KanaColors.onEntranceBackground.copy(alpha = 0.2f)
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
                        isTranslating = isTranslating,
                        onPlayJapaneseName = onPlayJapaneseName
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
                    iconRes = Res.drawable.navigate_back_icon,
                    backgroundColor = KanaColors.entranceSurface.copy(alpha = 0.2f),
                    iconColor = KanaColors.onEntranceBackground,
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
                    backgroundColor = KanaColors.onEntranceBackground,
                    labelColor = KanaColors.entranceBackground,
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
