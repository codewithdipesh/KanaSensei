package com.codewithdipesh.kanasensei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.codewithdipesh.kanasensei.navigation.NavApp
import com.codewithdipesh.ui.theme.KanaSenseiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
            .setKeepOnScreenCondition { false }
        setContent {
            val navController = rememberNavController()
            KanaSenseiTheme {
                NavApp(navController)
            }
        }
    }
}