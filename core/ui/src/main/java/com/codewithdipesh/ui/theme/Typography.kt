package com.codewithdipesh.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.codewithdipesh.ui.R


val SFProDisplay = FontFamily(
    listOf(
        Font(resId = R.font.sf_pro_display_regular, weight = FontWeight.Normal),
        Font(resId = R.font.sf_pro_display_medium, weight = FontWeight.Medium),
        Font(resId = R.font.sf_pro_display_bold, weight = FontWeight.Bold),
    )
)



fun h6Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

fun h5Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 20.sp
)

fun h4Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 24.sp
)

fun h3Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 28.sp
)

fun h2Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 32.sp
)

fun h1Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp
)

fun t1Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 20.sp
)

fun t2Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 18.sp
)


fun t3Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp
)


fun t4Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    lineHeight = 12.sp
)


fun b1Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 24.sp
)


fun b2Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp
)


fun b3Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp
)


fun b4Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 20.sp
)


fun b5Fun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    lineHeight = 16.sp
)

fun bRegularFun() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

fun captionResolver() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 18.sp
)


fun labelResolver() = TextStyle(
    fontFamily = SFProDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 14.sp
)

val KanaSenseiTypography = Typography(

    // Headlines
    displayLarge = h1Fun(),
    displayMedium = h2Fun(),
    displaySmall = h3Fun(),

    headlineLarge = h4Fun(),
    headlineMedium = h5Fun(),
    headlineSmall = h6Fun(),

    // Titles
    titleLarge = t1Fun(),
    titleMedium = t2Fun(),
    titleSmall = t3Fun(),

    // Body
    bodyLarge = b1Fun(),
    bodyMedium = b2Fun(),
    bodySmall = b3Fun(),

    // Labels
    labelLarge = b4Fun(),
    labelMedium = b5Fun(),
    labelSmall = captionResolver(),
)
