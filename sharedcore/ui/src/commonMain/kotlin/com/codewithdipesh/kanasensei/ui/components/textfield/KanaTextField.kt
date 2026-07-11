package com.codewithdipesh.kanasensei.ui.components.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.ui.theme.KanaColors

@Composable
fun KanaTextField(
    modifier: Modifier = Modifier,
    readOnly : Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,
    textColor: Color = KanaColors.onEntranceBackground,
    dividerColor: Color = KanaColors.entranceSurface
) {

    Column(
        modifier = modifier
    ){
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 28.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            readOnly = readOnly,
            cursorBrush = SolidColor(textColor.copy(0.3f)),
            modifier = modifier.fillMaxWidth()
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = dividerColor
        )
    }
}