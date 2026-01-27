plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.codewithdipesh.kanasensei.ui.resources"
        generateResClass = always
    }
}

// Workaround for com.android.kotlin.multiplatform.library plugin not setting up
// the output directory for Compose Multiplatform resource copying task
val composeAssetsDir = layout.buildDirectory.dir("generated/compose/resourceGenerator/androidAssets/androidMain")

afterEvaluate {
    tasks.matching { it.name == "copyAndroidMainComposeResourcesToAndroidAssets" }.configureEach {
        // Use reflection to set the outputDirectory property since the class is internal
        val outputDirProperty = this::class.java.methods.find { it.name == "getOutputDirectory" }
        if (outputDirProperty != null) {
            val directoryProperty = outputDirProperty.invoke(this) as org.gradle.api.file.DirectoryProperty
            directoryProperty.set(composeAssetsDir)
        }
    }

    // Make sure Android compile tasks depend on compose resource generation
    tasks.matching { it.name.contains("merge") && it.name.contains("Assets") }.configureEach {
        dependsOn(tasks.matching { it.name == "copyAndroidMainComposeResourcesToAndroidAssets" })
    }

    // Register the generated assets directory with Android
    extensions.findByType(com.android.build.api.dsl.LibraryExtension::class.java)?.let { android ->
        android.sourceSets.getByName("main").assets.srcDir(composeAssetsDir)
    }
}

kotlin {

    androidLibrary {
        namespace = "com.codewithdipesh.kanasensei.ui"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "sharedKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here
                // Use 'api' to export these to modules that depend on sharedcore:ui
                api(libs.jetbrains.compose.ui)
                api(libs.jetbrains.compose.foundation)
                api(libs.jetbrains.compose.material3)
                api(libs.jetbrains.compose.runtime)
                api(libs.jetbrains.compose.components)

            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
        }

        androidMain {
            dependencies {
                // Android KTX & Appcompat
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.appcompat)
                implementation(libs.material)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }

}