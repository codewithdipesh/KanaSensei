package com.codewithdipesh.kanasensei.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.poppins_bold
import com.codewithdipesh.kanasensei.ui.resources.poppins_medium
import com.codewithdipesh.kanasensei.ui.resources.poppins_regular
import org.jetbrains.compose.resources.Font

@Composable
fun poppinsFontFamily() = FontFamily(
    Font(Res.font.poppins_regular, FontWeight.Normal),
    Font(Res.font.poppins_medium, FontWeight.Medium),
    Font(Res.font.poppins_bold, FontWeight.Bold)
)

// Factory functions that take the fontFamily as a parameter to avoid composable overhead
private fun h1(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp)
private fun h2(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 32.sp)
private fun h3(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 28.sp)
private fun h4(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp)
private fun h5(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp)
private fun h6(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp)

private fun t1(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 20.sp)
private fun t2(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.sp)
private fun t3(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)

private fun b1(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 24.sp)
private fun b2(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)
private fun b3(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp)
private fun b4(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 20.sp)
private fun b5(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 10.sp, lineHeight = 16.sp)

private fun caption(font: FontFamily) = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp)

@Composable
fun kanaSenseiTypography(): Typography {
    val font = poppinsFontFamily()
    return Typography(
        displayLarge = h1(font),
        displayMedium = h2(font),
        displaySmall = h3(font),
        headlineLarge = h4(font),
        headlineMedium = h5(font),
        headlineSmall = h6(font),
        titleLarge = t1(font),
        titleMedium = t2(font),
        titleSmall = t3(font),
        bodyLarge = b1(font),
        bodyMedium = b2(font),
        bodySmall = b3(font),
        labelLarge = b4(font),
        labelMedium = b5(font),
        labelSmall = caption(font)
    )
}