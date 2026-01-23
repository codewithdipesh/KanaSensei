package com.codewithdipesh.kanasensei.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography

@Composable
fun OptionButton(
    modifier: Modifier = Modifier,
    label: String,
    isSelected : Boolean,
    onClick: () -> Unit,
    textAlignment: Alignment = Alignment.CenterStart,
    backgroundColor : Color = MaterialTheme.colorScheme.primary,
    isRoundedCorner : Boolean = true,
){

    val backgroundColor: Color = when {
        isSelected -> backgroundColor
        else -> Color.Transparent
    }

    val textColor: Color = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onBackground
    }

    val cornerRadius = if (isRoundedCorner) 13.dp else 0.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 1.5.dp,
                color = if(isSelected) backgroundColor else MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(backgroundColor)
            .clickable {
                onClick()
            },
        contentAlignment = textAlignment
    ){
        Text(
            text = label,
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = textColor
            ),
            modifier = Modifier.padding(20.dp)
        )
    }
}