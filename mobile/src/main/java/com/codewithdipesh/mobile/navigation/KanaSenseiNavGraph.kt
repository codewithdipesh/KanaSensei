package com.codewithdipesh.kanasensei.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.model.auth.AuthResult
import com.codewithdipesh.kanasensei.navigation.Screen.HomeGraph.Lesson.ARG_LESSON_ID
import com.codewithdipesh.kanasensei.sharedfeature.auth.AuthViewModel
import com.codewithdipesh.kanasensei.sharedfeature.auth.access.LoginScreen
import com.codewithdipesh.kanasensei.sharedfeature.auth.access.SignUpScreen
import com.codewithdipesh.kanasensei.sharedfeature.auth.onboarding.OnboardingScreen
import com.codewithdipesh.kanasensei.sharedfeature.auth.welcome.SplashScreen
import com.codewithdipesh.kanasensei.sharedfeature.auth.welcome.WelcomeScreen
import com.codewithdipesh.sharedfeature.learning.home.uistates.LearningEvent
import com.codewithdipesh.sharedfeature.learning.home.LearningHomeScreen
import com.codewithdipesh.sharedfeature.learning.home.LearningViewModel
import com.codewithdipesh.sharedfeature.learning.home.components.LessonCompleteDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.codewithdipesh.kanasensei.ui.theme.KanaColors
import com.codewithdipesh.sharedfeature.learning.lesson.LessonScreen
import com.codewithdipesh.sharedfeature.learning.lesson.LessonViewModel
import com.codewithdipesh.sharedfeature.learning.lesson.model.LessonCompletionResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


const val LESSON_COMPLETION_RESULT = "lessonCompletion"

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KanaColors.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.AuthGraph.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            authGraph(navController)
            homeGraph(navController)
        }

        // Network snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = KanaColors.errorContainer,
                contentColor = KanaColors.onErrorContainer,
                actionColor = KanaColors.error
            )
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
       composable(
           route = Screen.AuthGraph.SplashScreen.route,
           exitTransition = { fadeOut(tween(500)) }
       ) {
           val parentEntry = remember {
               navController.getBackStackEntry(Screen.AuthGraph.route)
           }
           val viewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

           val user by viewModel.user.collectAsStateWithLifecycle()
           val isAuthChecked by viewModel.isAuthChecked.collectAsStateWithLifecycle()

           LaunchedEffect(isAuthChecked) {
               if (isAuthChecked) {
                   delay(1500) // Show splash for at least 1.5s
                   if (user != null) {
                       navController.navigate(Screen.HomeGraph.Learning.route) {
                           popUpTo(Screen.AuthGraph.route) { inclusive = true }
                       }
                   } else {
                       navController.navigate(Screen.AuthGraph.WelcomeScreen.route) {
                           popUpTo(Screen.AuthGraph.route) { inclusive = true }
                       }
                   }
               }
           }

           SplashScreen()
       }

       composable(
           route = Screen.AuthGraph.WelcomeScreen.route,
           enterTransition = {
               fadeIn(animationSpec = tween(700))
           }
       ) {

           WelcomeScreen(
               onOnboard = { navController.navigate(Screen.AuthGraph.OnboardingScreen.route) },
               onLogin = { navController.navigate(Screen.AuthGraph.LoginScreen.route) }
           )
       }

       composable(Screen.AuthGraph.OnboardingScreen.route) {
           val scope = rememberCoroutineScope()
           val parentEntry = remember {
               navController.getBackStackEntry(Screen.AuthGraph.route)
           }
           val viewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

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
                           if(result == SnackbarResult.ActionPerformed) {
                               if(currentPage == 2 || currentPage == 3) {
                                   viewModel.fetchTranslation()
                               }
                           }
                       }
                   }
               }
           }

           Box(
               modifier = Modifier
                   .fillMaxSize()
                   .background(KanaColors.learningBackground)
           ) {
               OnboardingScreen(
                   onNavigateBack = { navController.popBackStack() },
                   onNavigateNext = { navController.navigate(Screen.AuthGraph.SignUpScreen.route) },
                   selectedMotivatedSource = selectedMotivatedSource,
                   onChangeMotivatedSource = { viewModel.setMotivationSource(it) },
                   name = name,
                   onChangeName = { viewModel.setUserName(it) },
                   onPlayJapaneseName = { viewModel.speakJapaneseName(it) },
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
           val parentEntry = remember {
               navController.getBackStackEntry(Screen.AuthGraph.route)
           }
           val viewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
           val context = LocalContext.current
           val snackbarHostState = remember { SnackbarHostState() }
           val keyboardController = LocalSoftwareKeyboardController.current
           val focusManager = LocalFocusManager.current

           val authState by viewModel.authState.collectAsStateWithLifecycle()
           val email = authState.email
           val password = authState.password
           val status = authState.status

           // Navigate to Home on successful auth
           LaunchedEffect(status) {
               if (status is AuthResult.Success) {
                   navController.navigate(Screen.HomeGraph.Learning.route) {
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

           Scaffold(
               snackbarHost = {
                   SnackbarHost(snackbarHostState) { data ->
                       Snackbar(
                           snackbarData = data,
                           containerColor = KanaColors.errorContainer,
                           contentColor = KanaColors.onErrorContainer
                       )
                   }
               }
           ) { paddingValues ->
               Box(modifier = Modifier
                   .background(KanaColors.learningBackground)
                   .padding(paddingValues)
               ) {
                   SignUpScreen(
                       email = email,
                       password = password,
                       onEmailchanged = { viewModel.setEmail(it) },
                       onPasswordchanged = { viewModel.setPassword(it) },
                       authStatus = status,
                       onSignUpClick = { viewModel.register() },
                       onGoogleSignIn = { viewModel.startGoogleSignIn(context) },
                       onLoginClick = { navController.navigate(Screen.AuthGraph.LoginScreen.route) }
                   )
               }
           }
       }

       composable(Screen.AuthGraph.LoginScreen.route) {
           val parentEntry = remember {
               navController.getBackStackEntry(Screen.AuthGraph.route)
           }
           val viewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
           val context = LocalContext.current
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
                   navController.navigate(Screen.HomeGraph.Learning.route) {
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

           Scaffold(
               snackbarHost = {
                   SnackbarHost(snackbarHostState) { data ->
                       Snackbar(
                           snackbarData = data,
                           containerColor = KanaColors.errorContainer,
                           contentColor = KanaColors.onErrorContainer
                       )
                   }
               }
           ) { paddingValues ->
               Box(modifier = Modifier
                   .background(KanaColors.learningBackground)
                   .padding(paddingValues)
               ) {
                   LoginScreen(
                       email = email,
                       password = password,
                       onEmailchanged = { viewModel.setEmail(it) },
                       onPasswordchanged = { viewModel.setPassword(it) },
                       authStatus = status,
                       onLoginClick = { viewModel.login() },
                       onGoogleSignIn = { viewModel.startGoogleSignIn(context) },
                       onSignUpClick = {
                           if(motivationSource != null && name.isNotEmpty()){
                               navController.navigate(Screen.AuthGraph.SignUpScreen.route)
                           }
                           else{
                               navController.navigate(Screen.AuthGraph.OnboardingScreen.route)
                           }
                       }
                   )
               }
           }
       }



   }
}

fun NavGraphBuilder.homeGraph(
    navController: NavController
) {
    navigation(
        route = Screen.HomeGraph.route,
        startDestination = Screen.HomeGraph.Learning.route
    ) {
        composable(Screen.HomeGraph.Learning.route) { entry ->
            val parentEntry = remember {
                navController.getBackStackEntry(Screen.HomeGraph.route)
            }
            val viewModel: LearningViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            val currentUser by viewModel.user.collectAsStateWithLifecycle()
            val isUserLoaded by viewModel.isUserLoaded.collectAsStateWithLifecycle()

            // Wait for user to load, then check if logged in
            if (isUserLoaded && currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.AuthGraph.route) {
                        popUpTo(Screen.HomeGraph.Learning.route) { inclusive = true }
                    }
                }
                return@composable
            }

            // Show nothing while loading user
            if (!isUserLoaded) {
                return@composable
            }

            val user by viewModel.user.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            // Collect events for errors and notifications
            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is LearningEvent.Error -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = event.message,
                                    withDismissAction = true
                                )
                            }
                        }
                        is LearningEvent.LessonCompleted -> {
                            if (event.chapterCompleted) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Chapter completed! Moving to next chapter.",
                                        withDismissAction = true
                                    )
                                }
                            }
                        }
                        is LearningEvent.SyncCompleted -> {
                           //show nothing
                        }

                        is LearningEvent.Message -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = event.message,
                                    withDismissAction = true
                                )
                            }
                        }
                    }
                }
            }

            // Show error from uiState
            LaunchedEffect(uiState.error) {
                uiState.error?.let { error ->
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = error,
                            actionLabel = "Retry",
                            withDismissAction = true
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.refreshFromCloud()
                        }
                        viewModel.clearError()
                    }
                }
            }

            // Lesson-completion popup, delivered as a nav result from the lesson screen.
            val savedStateHandle = entry.savedStateHandle
            val pendingCompletionJson by savedStateHandle.getStateFlow<String?>(LESSON_COMPLETION_RESULT, null)
                .collectAsStateWithLifecycle()

            var activeCompletion by remember { mutableStateOf<LessonCompletionResult?>(null) }

            LaunchedEffect(pendingCompletionJson) {
                pendingCompletionJson?.let {
                    activeCompletion = Json.decodeFromString<LessonCompletionResult>(it)
                    savedStateHandle.remove<String>(LESSON_COMPLETION_RESULT)
                }
            }

            LearningHomeScreen(
                isLoading = uiState.isLoading,
                chapters = uiState.chapters,
                selectedLesson = uiState.selectedLesson,
                onLessonStart = { lessonWithProgress ->
                    //navigate to lesson details screen
                    navController.navigate(
                        Screen.HomeGraph.Lesson.createRoute(lessonWithProgress.lesson.id)
                    )
                },
                onLessonSelect = { lesson ->
                    if (uiState.selectedLesson != lesson){
                        viewModel.selectLesson(lesson)
                    }else{
                        viewModel.selectLesson(null)
                    }

                },
                showGrievienceForm = uiState.showGrievienceForm,
                grievienceState = uiState.grievienceState,
                onGrievienceClick = { viewModel.showGrievienceForm(true) },
                onGrievienceTitleChange = viewModel::updateGrievienceTitle,
                onGrievienceDescriptionChange = viewModel::updateGrievienceDescription,
                onGrievienceMediaSelected = viewModel::addGrievienceMedia,
                onGrievienceMediaRemove = viewModel::removeGrievienceMedia,
                onGrievienceConfirm = viewModel::submitGrievience,
                onGrievienceDismiss = { viewModel.showGrievienceForm(false) },
                pendingCompletion = activeCompletion,
                snackBarHost = {
                    SnackbarHost(snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = KanaColors.errorContainer,
                            contentColor = KanaColors.onErrorContainer,
                            actionColor = KanaColors.error
                        )
                    }
                }
            )

            // Completion popup. Dismissing it clears activeCompletion, which un-gates the
            // tile's tick on the home screen so it animates in.
            activeCompletion?.let { result ->
                LessonCompleteDialog(
                    isFirstLesson = result.newChapterOrder == 1 && result.newLessonOrder == 1,
                    username = user?.name,
                    lessonDetails = result.shortDescription,
                    onContinue = { activeCompletion = null }
                )
            }
        }

        composable(
            route =Screen.HomeGraph.Lesson.route,
            arguments = listOf(
                navArgument(ARG_LESSON_ID) { type = NavType.StringType }
            )
        ){backstackEntry ->
            val lessonId = if(backstackEntry.arguments != null) backstackEntry.arguments!!.getString(ARG_LESSON_ID) else ""
            val parentEntry = remember {
                navController.getBackStackEntry(Screen.HomeGraph.route)
            }
            val viewModel: LessonViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            val state by viewModel.state.collectAsStateWithLifecycle()

            // Guards against firing completion more than once
            var completing by remember { mutableStateOf(false) }

            val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                viewModel.load(lessonId!!)
            }
            //ISLOADING AND NO INTERNET then navigate back
            LaunchedEffect(networkStatus, state.isLoading) {
                if (
                    (networkStatus != ConnectivityObserver.Status.Available && state.isLoading) ||
                    (!state.isLoading && networkStatus != ConnectivityObserver.Status.Available && state.kanas.isEmpty()) //screen is lesson but kana is empty and
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Please turn your Internet on to load lesson content",
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        )
                        navController.popBackStack()
                    }
                }
            }

            // Lesson finished + progress persisted: hand the result back to home as a nav
            // result (JSON, so it's Bundle-safe and survives process death), then pop.
            LaunchedEffect(Unit) {
                viewModel.completionEvent.collect { result ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(LESSON_COMPLETION_RESULT, Json.encodeToString(result))
                    navController.popBackStack()
                }
            }

            // Completion failed: let the user retry by tapping Continue again.
            LaunchedEffect(Unit) {
                viewModel.error.collect { message ->
                    completing = false
                    println("LessonScreen: completion error -> $message")
                }
            }


            LessonScreen(
                isLoading = state.isLoading,
                error = state.error,
                lessonPages = state.pages,
                kanas = state.kanas,
                kanaById = state.kanaById,
                strokesById = state.strokesById,
                selectedPage = state.selectedPage,
                lessonTitle = state.lesson?.title?: "",
                totalPage = state.totalPage,
                currentPageNumber = state.currPage,
                onClose = { navController.popBackStack() },
                onContinue = {
                    if (!viewModel.next() && !completing) {
                        if(state.isCompleted){
                            navController.popBackStack()
                        }else{
                            completing = true
                            viewModel.completeCurrentLesson(
                                lessonId = lessonId!!,
                                chapterId = state.lesson?.chapterId ?: ""
                            )
                        }
                    }
                },
                snackBarHost = {
                    SnackbarHost(snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = KanaColors.errorContainer,
                            contentColor = KanaColors.onErrorContainer,
                            actionColor = KanaColors.error
                        )
                    }
                }
            )

        }
    }
}