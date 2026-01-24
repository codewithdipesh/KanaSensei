package com.codewithdipesh.kanasensei.ui.components.buttons


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTypography
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    label: String,
    clickable: Boolean = true,
    onClick: () -> Unit,
    iconRes: DrawableResource? = null,
    iconSize: Dp = 26.dp,
    backgroundColor : Color = MaterialTheme.colorScheme.onBackground,
    labelColor: Color = MaterialTheme.colorScheme.onSecondary,
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
        Row(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            if(iconRes != null){
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                )
            }
            Text(
                text = label,
                style = KanaSenseiTypography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = labelColor
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
