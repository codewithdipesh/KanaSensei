import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
    id("com.google.gms.google-services")
    alias(libs.plugins.firebase.crashlytics)
}

// Load local.properties
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.codewithdipesh.kanasensei"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.codewithdipesh.kanasensei"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read keys from local.properties (not exposed in version control)
        val webClientId = localProperties.getProperty("WEB_CLIENT_ID") ?: ""
        val telegramToken = localProperties.getProperty("TELEGRAM_TOKEN") ?: ""
        val chatId = localProperties.getProperty("CHAT_ID") ?: ""
        
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")
        buildConfigField("String", "TELEGRAM_TOKEN", "\"$telegramToken\"")
        buildConfigField("String", "CHAT_ID", "\"$chatId\"")
    }

    // Include compose resources from sharedcore:ui module
    sourceSets {
        getByName("main") {
            assets.srcDir(project(":sharedcore:ui").layout.buildDirectory.dir("generated/compose/resourceGenerator/androidAssets/androidMain"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

// Ensure compose resources from sharedcore:ui are generated before merging assets
afterEvaluate {
    tasks.matching { it.name.contains("merge") && it.name.contains("Assets") }.configureEach {
        dependsOn(":sharedcore:ui:copyAndroidMainComposeResourcesToAndroidAssets")
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)

    api(project(":sharedcore:core"))
    api(project(":sharedcore:ui"))
    api(project(":sharedfeature:auth"))
    api(project(":sharedfeature:learning"))

    implementation(libs.bundles.koin)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)

    implementation(libs.splash.screen)

    implementation(libs.androidx.credential.credentials)
    implementation(libs.google.identity)

    implementation(libs.napier)

    implementation(libs.serialization.json)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

}