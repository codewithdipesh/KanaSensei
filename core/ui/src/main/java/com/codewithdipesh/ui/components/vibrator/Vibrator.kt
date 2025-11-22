package com.codewithdipesh.ui.components.vibrator

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberVibrator(): Vibrator {
    val context = LocalContext.current
    return remember {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}


@RequiresPermission(Manifest.permission.VIBRATE)
fun Vibrator.correctHaptic() {
    if (Build.VERSION.SDK_INT >= 26) {
        vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrate(40)
    }
}

@RequiresPermission(Manifest.permission.VIBRATE)
fun Vibrator.wrongHaptic() {
    if (Build.VERSION.SDK_INT >= 26) {
        vibrate(
            VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        vibrate(120)
    }
}

