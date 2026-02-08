package com.codewithdipesh.kanasensei.navigation

sealed class Screen(val route: String) {

    object AuthGraph : Screen("auth_graph") {
        object SplashScreen : Screen("auth_graph/splash_screen")
        object WelcomeScreen : Screen("auth_graph/welcome_screen")
        object OnboardingScreen : Screen("auth_graph/onboarding_screen")
        object LoginScreen : Screen("auth_graph/login_screen")
        object SignUpScreen : Screen("auth_graph/signup_screen")
    }

    object HomeGraph : Screen("home_graph") {
        object Learning : Screen("home_graph/learning_home")
    }
}