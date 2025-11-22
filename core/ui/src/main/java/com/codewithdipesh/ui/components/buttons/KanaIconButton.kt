package com.codewithdipesh.ui.components.buttons


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KanaIconButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    size : Dp = 63.dp,
    iconSize : Dp = size/3.5f,
    onClick: () -> Unit,
    iconColor : Color,
    backgroundColor : Color,
    isRoundedCorner : Boolean = true,
){
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(if(isRoundedCorner) size/4.5f else 0.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ){
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}