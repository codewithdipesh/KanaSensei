package com.codewithdipesh.kanasensei.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.codewithdipesh.auth.AuthViewModel
import com.codewithdipesh.auth.access.LoginScreen
import com.codewithdipesh.auth.access.SignUpScreen
import com.codewithdipesh.auth.onboarding.OnboardingScreen
import com.codewithdipesh.auth.welcome.SplashScreen
import com.codewithdipesh.auth.welcome.WelcomeScreen
import com.codewithdipesh.data.connectivity.ConnectivityObserver
import com.codewithdipesh.data.model.auth.AuthResult
import com.codewithdipesh.kanasensei.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun NavApp(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    connectivityObserver: ConnectivityObserver = koinInject()
) {
    val networkStatus by connectivityObserver.observe()
        .collectAsStateWithLifecycle(initialValue = ConnectivityObserver.Status.Available)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(networkStatus) {
        when(networkStatus) {
            ConnectivityObserver.Status.Unavailable,
            ConnectivityObserver.Status.Lost -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "You're offline",
                        withDismissAction = true
                    )
                }
            }
            ConnectivityObserver.Status.Available -> {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    actionColor = MaterialTheme.colorScheme.error
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.AuthGraph.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ){
                authGraph(navController)
                homeGraph(navController)
            }
        }
    }
}

fun NavGraphBuilder.authGraph(
   navController: NavController
) {
   navigation(
       route = Screen.AuthGraph.route,
       startDestination = Screen.AuthGraph.SplashScreen.route
   ) {
       composable(Screen.AuthGraph.SplashScreen.route) {
           val viewModel : AuthViewModel = koinViewModel()
           val user = viewModel.user

           LaunchedEffect(Unit) {
               delay(2000)
               if(user != null) {
                   navController.navigate(Screen.HomeGraph.Home.route) {
                       popUpTo(Screen.AuthGraph.route) { inclusive = true }
                   }
               }else{
                   navController.navigate(Screen.AuthGraph.WelcomeScreen.route) {
                       popUpTo(Screen.AuthGraph.route) { inclusive = true }
                   }
               }
           }

           SplashScreen()
       }

       composable(Screen.AuthGraph.WelcomeScreen.route) {

           WelcomeScreen(
               onOnboard = { navController.navigate(Screen.AuthGraph.OnboardingScreen.route) },
               onLogin = { navController.navigate(Screen.AuthGraph.LoginScreen.route) }
           )
       }

       composable(Screen.AuthGraph.OnboardingScreen.route) {
           val scope = rememberCoroutineScope()
           val viewModel : AuthViewModel = koinViewModel()

           val onBoardState by viewModel.onBoardingState.collectAsStateWithLifecycle()
           val selectedMotivatedSource = onBoardState.motivationSource
           val name = onBoardState.name
           val japaneseName = onBoardState.japaneseName
           val isTranslating = onBoardState.isTranslating

           val errorListener = viewModel.errorListener
           val snackbarHostState = remember { SnackbarHostState() }

           var currentPage by rememberSaveable { mutableIntStateOf(1) }
           val totalPage = 3

           BackHandler {
               if(currentPage == 1){
                   navController.popBackStack()
               }else{
                   currentPage--
               }
           }

           LaunchedEffect(errorListener) {
               errorListener.collect { errorMessage ->
                   if(errorMessage != null){
                       scope.launch {
                           val result = snackbarHostState.showSnackbar(
                               message = errorMessage,
                               actionLabel = if(errorMessage.contains("internet") ||
                                               errorMessage.contains("failed")) "Retry" else null,
                               withDismissAction = true
                           )
                           if(result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                               if(currentPage == 2 || currentPage == 3) {
                                   viewModel.fetchTranslation()
                               }
                           }
                       }
                   }
               }
           }

           Box(modifier = Modifier.fillMaxSize()) {
               OnboardingScreen(
                   onNavigateBack = { navController.popBackStack() },
                   onNavigateNext = { navController.navigate(Screen.AuthGraph.SignUpScreen.route) },
                   selectedMotivatedSource = selectedMotivatedSource,
                   onChangeMotivatedSource = { viewModel.setMotivationSource(it) },
                   name = name,
                   onChangeName = { viewModel.setUserName(it) },
                   translatedName = japaneseName,
                   currentPage = currentPage,
                   totalPage = totalPage,
                   onChangePage = { currentPage = it },
                   onTranslate = { viewModel.fetchTranslation() },
                   isTranslating = isTranslating,
               )
           }
       }

       composable(Screen.AuthGraph.SignUpScreen.route) {
           val viewModel : AuthViewModel = koinViewModel()
           val context = LocalContext.current
           val scope = rememberCoroutineScope()
           val snackbarHostState = remember { SnackbarHostState() }
           val keyboardController = LocalSoftwareKeyboardController.current
           val focusManager = LocalFocusManager.current

           val authState by viewModel.authState.collectAsStateWithLifecycle()
           val onBoardState by viewModel.onBoardingState.collectAsStateWithLifecycle()
           val email = authState.email
           val password = authState.password
           val status = authState.status
           val name = onBoardState.name
           val motivationSource = onBoardState.motivationSource

           // Navigate to Home on successful auth
           LaunchedEffect(status) {
               if (status is AuthResult.Success) {
                   navController.navigate(Screen.HomeGraph.Home.route) {
                       popUpTo(Screen.AuthGraph.route) { inclusive = true }
                   }
               }
           }

           // Collect error messages
           LaunchedEffect(Unit) {
               viewModel.errorListener.collect { errorMessage ->
                   if (errorMessage != null) {
                       keyboardController?.hide()
                       focusManager.clearFocus()
                       snackbarHostState.showSnackbar(
                           message = errorMessage,
                           withDismissAction = true
                       )
                   }
               }
           }

           fun handleGoogleSignIn() {
               scope.launch {
                   try {
                       val credentialManager = CredentialManager.create(context)

                       val googleIdOption = GetGoogleIdOption.Builder()
                           .setFilterByAuthorizedAccounts(false)
                           .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                           .build()

                       val request = GetCredentialRequest.Builder()
                           .addCredentialOption(googleIdOption)
                           .build()

                       val result = credentialManager.getCredential(
                           request = request,
                           context = context
                       )

                       val credential = result.credential
                       if (credential is GoogleIdTokenCredential) {
                           val googleIdToken = credential.idToken
                           viewModel.googleLogin(
                               idToken = googleIdToken,
                               name = name.ifEmpty { credential.displayName ?: "User" },
                               motivationSource = motivationSource?.displayName() ?: "Unknown"
                           )
                       }
                   } catch (e: GetCredentialException) {
                       keyboardController?.hide()
                       focusManager.clearFocus()
                       snackbarHostState.showSnackbar("Google Sign-In failed: ${e.message}")
                   } catch (e: Exception) {
                       keyboardController?.hide()
                       focusManager.clearFocus()
                       snackbarHostState.showSnackbar("Error: ${e.message}")
                   }
               }
           }

           Scaffold(
               snackbarHost = {
                   SnackbarHost(snackbarHostState) { data ->
                       Snackbar(
                           snackbarData = data,
                           containerColor = MaterialTheme.colorScheme.errorContainer,
                           contentColor = MaterialTheme.colorScheme.onErrorContainer
                       )
                   }
               }
           ) { paddingValues ->
               Box(modifier = Modifier.padding(paddingValues)) {
                   SignUpScreen(
                       email = email,
                       password = password,
                       onEmailchanged = { viewModel.setEmail(it) },
                       onPasswordchanged = { viewModel.setPassword(it) },
                       authStatus = status,
                       onSignUpClick = { viewModel.register() },
                       onGoogleSignIn = { handleGoogleSignIn() },
                       onLoginClick = { navController.navigate(Screen.AuthGraph.LoginScreen.route) }
                   )
               }
           }
       }

       composable(Screen.AuthGraph.LoginScreen.route) {
           val viewModel : AuthViewModel = koinViewModel()
           val context = LocalContext.current
           val scope = rememberCoroutineScope()
           val snackbarHostState = remember { SnackbarHostState() }
           val keyboardController = LocalSoftwareKeyboardController.current
           val focusManager = LocalFocusManager.current

           val authState by viewModel.authState.collectAsStateWithLifecycle()
           val onBoardState by viewModel.onBoardingState.collectAsStateWithLifecycle()
           val email = authState.email
           val password = authState.password
           val status = authState.status
           val name = onBoardState.name
           val motivationSource = onBoardState.motivationSource

           // Navigate to Home on successful auth
           LaunchedEffect(status) {
               if (status is AuthResult.Success) {
                   navController.navigate(Screen.HomeGraph.Home.route) {
                       popUpTo(Screen.AuthGraph.route) { inclusive = true }
                   }
               }
           }

           // Collect error messages
           LaunchedEffect(Unit) {
               viewModel.errorListener.collect { errorMessage ->
                   if (errorMessage != null) {
                       keyboardController?.hide()
                       focusManager.clearFocus()
                       snackbarHostState.showSnackbar(
                           message = errorMessage,
                           withDismissAction = true
                       )
                   }
               }
           }

           fun handleGoogleSignIn() {
               scope.launch {
                   try {
                       val credentialManager = CredentialManager.create(context)

                       val googleIdOption = GetGoogleIdOption.Builder()
                           .setFilterByAuthorizedAccounts(false)
                           .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                           .build()

                       val request = GetCredentialRequest.Builder()
                           .addCredentialOption(googleIdOption)
                           .build()

                       val result = credentialManager.getCredential(
                           request = request,
                           context = context
                       )

                       val credential = result.credential
                       if (credential is GoogleIdTokenCredential) {
                           val googleIdToken = credential.idToken
                           viewModel.googleLogin(
                               idToken = googleIdToken,
                               name = name.ifEmpty { credential.displayName ?: "User" },
                               motivationSource = motivationSource?.displayName() ?: "Unknown"
                           )
                       }
                   } catch (e: GetCredentialException) {
                       keyboardController?.hide()
                       focusManager.clearFocus()
                       snackbarHostState.showSnackbar("Google Sign-In failed: ${e.message}")
                   } catch (e: Exception) {
                       keyboardController?.hide()
                       focusManager.clearFocus()
                       snackbarHostState.showSnackbar("Error: ${e.message}")
                   }
               }
           }

           Scaffold(
               snackbarHost = {
                   SnackbarHost(snackbarHostState) { data ->
                       Snackbar(
                           snackbarData = data,
                           containerColor = MaterialTheme.colorScheme.errorContainer,
                           contentColor = MaterialTheme.colorScheme.onErrorContainer
                       )
                   }
               }
           ) { paddingValues ->
               Box(modifier = Modifier.padding(paddingValues)) {
                   LoginScreen(
                       email = email,
                       password = password,
                       onEmailchanged = { viewModel.setEmail(it) },
                       onPasswordchanged = { viewModel.setPassword(it) },
                       authStatus = status,
                       onLoginClick = { viewModel.login() },
                       onGoogleSignIn = { handleGoogleSignIn() },
                       onSignUpClick = { navController.navigate(Screen.AuthGraph.SignUpScreen.route) }
                   )
               }
           }
       }



   }
}

fun NavGraphBuilder.homeGraph(
    navController: NavController
){
    navigation(
        route = Screen.HomeGraph.route,
        startDestination = Screen.HomeGraph.Home.route
    ){
        composable(Screen.HomeGraph.Home.route){

        }
    }
}