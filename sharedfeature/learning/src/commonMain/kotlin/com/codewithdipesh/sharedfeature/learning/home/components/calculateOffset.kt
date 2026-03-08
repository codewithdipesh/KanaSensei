package com.codewithdipesh.sharedfeature.learning.home.components


private val snakeCurve = listOf(
    1f,
    0.50f,
    0f, //peak
    0.50f,
    1f
)

fun calculateOffset(index: Int): Float {
    val pos = index % snakeCurve.size
    return snakeCurve[pos]
}