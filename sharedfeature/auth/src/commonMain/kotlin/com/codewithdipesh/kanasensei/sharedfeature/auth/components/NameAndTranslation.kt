package com.codewithdipesh.kanasensei.sharedfeature.auth.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.codewithdipesh.kanasensei.ui.components.buttons.KanaIconButton
import com.codewithdipesh.kanasensei.ui.components.textfield.KanaTextField
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.icon_sound_on
import kotlinx.coroutines.delay

@Composable
fun NameAndTranslation(
    modifier: Modifier = Modifier,
    value : String,
    onValueChange : (String) -> Unit,
    onPlayJapaneseName : (String) -> Unit,
    showTranslation : Boolean,
    isTranslating : Boolean,
    translatedValue : String?=null
) {
    Column(
        horizontalAlignment = Alignment.Start
    ){
        Text(
            text = "What should we call You?",
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(50.dp))
        KanaTextField(
            modifier = modifier
                .fillMaxWidth(),
            value = value,
            readOnly = showTranslation,
            onValueChange = onValueChange
        )
        Spacer(Modifier.height(50.dp))
        //translated word and sound button
        if(showTranslation && translatedValue != null){
            var visibleChars by rememberSaveable { mutableIntStateOf(0) }
            var showSound by rememberSaveable { mutableStateOf(false) }

            LaunchedEffect(translatedValue) {
                visibleChars = 0
                translatedValue.indices.forEach { index ->
                    delay(100L)
                    visibleChars = index + 1
                }
                showSound = true
            }

            //japanese trans and sound
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = translatedValue.take(visibleChars),
                    style = TextStyle(
                        fontSize = 34.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                AnimatedVisibility(visible = isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 1.5.dp
                    )
                }
                AnimatedVisibility(visible = showSound) {
                    KanaIconButton(
                        iconRes = Res.drawable.icon_sound_on,
                        size = 36.dp,
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            onPlayJapaneseName(translatedValue)
                        }
                    )
                }
            }
        }
    }
}