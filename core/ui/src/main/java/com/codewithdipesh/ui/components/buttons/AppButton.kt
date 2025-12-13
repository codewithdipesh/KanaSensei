package com.codewithdipesh.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codewithdipesh.ui.theme.KanaSenseiTypography

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    label: String,
    clickable: Boolean = true,
    onClick: () -> Unit,
    backgroundColor : Color = MaterialTheme.colorScheme.onBackground,
    labelColor: Color = MaterialTheme.colorScheme.onPrimary,
    isRoundedCorner : Boolean = true,
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if(isRoundedCorner) 13.dp else 0.dp))
            .background( backgroundColor.copy(
                if(clickable) 1f
                else 0.7f
            ) )
            .clickable { if(clickable) onClick() },
        contentAlignment = Alignment.Center
    ){
        Text(
            text = label,
            style = KanaSenseiTypography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = labelColor
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}