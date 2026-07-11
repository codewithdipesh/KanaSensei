package com.codewithdipesh.sharedfeature.learning.lesson.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import com.codewithdipesh.kanasensei.ui.components.textfield.KanaTextField
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.sharedfeature.learning.lesson.components.kana.QuizDrawingPad
import com.codewithdipesh.sharedfeature.learning.lesson.components.kana.rememberKanaPaths
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(10.dp))
        details.hint.let {
            Text(
                text = details.hint!!,
                style = TextStyle(
                    color = KanaColors.onLearningBackground.copy(0.85f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(Modifier.height(12.dp))
        when(details.questionType){
            audio , romaji -> {
                TopElement(
                    onPlay = onPlay,
                    onTirtlePlay = onTirtlePlay
                )
            }
            svg -> TODO()
            word -> TODO()
            QuizQuestionType.kana -> {
                //nothing to show
                Spacer(Modifier.height(32.dp))
            }
        }

        when(details.answerType){
            mcq -> {
                Spacer(Modifier.height(210.dp))
                MCQView(
                    options = details.options,
                    correctOption = details.correctOption,
                    clickable = !answered,
                    onClick = {
                        onQuizComplete(it)
                        answered = true
                    }
                )
                Spacer(Modifier.height(150.dp))
            }
            typing -> {
                Spacer(Modifier.height(210.dp))
                //todo
                Spacer(Modifier.height(150.dp))
            }
            drawing -> {
                val paths = rememberKanaPaths(strokes)
                Spacer(Modifier.height(100.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    contentAlignment = Alignment.Center
                ){
                    QuizDrawingPad(
                        strokes = strokes,
                        paths = paths,
                        clickable = !answered,
                        onComplete = { isCorrect ->
                            onQuizComplete(isCorrect)
                            if (isCorrect) answered = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
                Spacer(Modifier.height(100.dp))
            }
            ordering -> TODO()
            matching -> TODO()
        }



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