package com.codewithdipesh.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.data.model.user.MotivationSource
import com.codewithdipesh.ui.components.buttons.OptionButton
import com.codewithdipesh.ui.theme.KanaSenseiTypography

@Composable
fun MotivationSourceQuestion(
    modifier: Modifier = Modifier,
    selectedOption : MotivationSource?,
    onOptionClicked : (MotivationSource) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start
    ){
        Text(
            text = "What motivates u most to learn Japanese?",
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Start
            )
        )
        Spacer(Modifier.height(50.dp))

        MotivationSource.getAll().forEach {
            OptionButton(
                modifier = Modifier.fillMaxWidth(),
                label = it.displayName(),
                onClick = { onOptionClicked(it) },
                isSelected = selectedOption == it
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}
