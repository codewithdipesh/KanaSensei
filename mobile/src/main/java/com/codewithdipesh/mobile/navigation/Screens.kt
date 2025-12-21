package com.codewithdipesh.kanasensei.navigation

sealed class Screen(val route: String){
    object HomeGraph: Screen("home_graph"){
        object Home: Screen("home_graph/kana_table")
        object KanaTable: Screen("home_graph/kana_table")
    }
    object AuthGraph: Screen("auth_graph"){
        object SplashScreen : Screen("auth_graph/splash_screen")
        object WelcomeScreen : Screen("auth_graph/welcome_screen")
        object OnboardingScreen : Screen("auth_graph/onboarding_screen")
        object LoginScreen : Screen("auth_graph/login_screen")
        object SignUpScreen : Screen("auth_graph/signup_screen")
    }

}