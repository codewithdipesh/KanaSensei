package com.codewithdipesh.sharedfeature.learning.home.components

import com.codewithdipesh.kanasensei.ui.resources.Res
import com.codewithdipesh.kanasensei.ui.resources.prop_2
import com.codewithdipesh.kanasensei.ui.resources.prop_3
import com.codewithdipesh.kanasensei.ui.resources.prop_4
import com.codewithdipesh.kanasensei.ui.resources.prop__
import org.jetbrains.compose.resources.DrawableResource


private val snakeCurve = listOf(
    1f,
    0.50f,
    0f ,//peak
    0.50f
)

val SNAKE_CURVE_SIZE = 3

fun calculateTileOffset(index: Int): Float {
    val pos = index % snakeCurve.size
    return snakeCurve[pos]
}

private val props = listOf(
    Res.drawable.prop_2,
    Res.drawable.prop__,
    Res.drawable.prop_3,
    Res.drawable.prop_4
)

fun calculatePropOffset(index: Int): Float? {
    if ((index - 2) % 6 != 0) return null // skip

    val k = (index - 2) / 6
    return if (k % 2 == 0) 1f else 0f
}


fun getProp(index: Int): DrawableResource {
    val k = (index - 2) / 6
    return props[k % props.size]
}

fun getPropOffsetFix(index: Int): Int {
    val k = (index - 2) / 6
    return if (k % 2 == 0) 40 else -16
}
