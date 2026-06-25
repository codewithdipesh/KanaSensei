package com.codewithdipesh.sharedfeature.learning.lesson.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.core.model.content.Character
import com.codewithdipesh.kanasensei.core.model.content.KanaStrokes
import com.codewithdipesh.kanasensei.core.model.content.QuizDetail
import com.codewithdipesh.kanasensei.core.model.content.QuizQuestionType
import com.codewithdipesh.kanasensei.core.model.content.QuizQuestionType.*
import com.codewithdipesh.kanasensei.core.model.content.QuizResponseMode.*
import com.codewithdipesh.kanasensei.ui.components.buttons.AppButton
import com.codewithdipesh.kanasensei.ui.components.buttons.QuizChoiceButton
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun QuizView(
    details : QuizDetail,
    kana : Character,
    strokes: KanaStrokes,
    onQuizComplete : (Boolean) -> Unit, //correct or wrong submit
    onContinue : () -> Unit,
    onTirtlePlay : () -> Unit,
    onPlay : () -> Unit,
    modifier: Modifier = Modifier
){
    var answered by remember(details){ mutableStateOf(false) }

    //first time play by default if its audio
    LaunchedEffect(details){
        if(details.questionType == audio){
            delay(800.milliseconds)
            onPlay()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = details.question,
            style = TextStyle(
                color = KanaColors.onLearningBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(Modifier.height(24.dp))
        when(details.questionType){
            audio -> {
                TopElement(
                    onPlay = onPlay,
                    onTirtlePlay = onTirtlePlay
                )
                Spacer(Modifier.height(210.dp))
            }
            romaji -> {
                //nothing to show
                Spacer(Modifier.height(270.dp))
            }
            svg -> TODO()
            word -> TODO()
            QuizQuestionType.kana -> {
                //nothing to show
                Spacer(Modifier.height(270.dp))
            }
        }

        when(details.answerType){
            mcq -> {
                MCQView(
                    options = details.options,
                    correctOption = details.correctOption,
                    clickable = !answered,
                    onClick = {
                        onQuizComplete(it)
                        answered = true
                    }
                )
            }
            typing -> TODO()
            drawing -> TODO()
            ordering -> TODO()
            matching -> TODO()
        }

        Spacer(Modifier.height(150.dp))

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            label = "Continue",
            clickable = answered,
            onClick = onContinue,
            backgroundColor = KanaColors.onLearningBackground,
            labelColor = Color.White
        )

    }
}

@Composable
fun MCQView(
    options : List<String>,
    clickable : Boolean ,
    correctOption : Int, //as 1 index base,
    onClick : (Boolean) -> Unit
){
    val rows = options.chunked(2)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ){
        rows.forEachIndexed { rowIndex, rowOptions ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                rowOptions.forEachIndexed { optionIndexInRow, option ->

                    val actualIndex = rowIndex * 2 + optionIndexInRow

                    QuizChoiceButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        label = option,
                        clickable = clickable,
                        onClick = {
                            onClick( actualIndex  == correctOption )
                        },
                        isCorrect = actualIndex == correctOption
                    )
                }
            }
        }
    }
}