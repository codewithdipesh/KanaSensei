plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.codewithdipesh.kanasensei.sharedfeature.auth"
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

    val xcfName = "sharedfeatureKit"

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
                api(project(":sharedcore:core"))
                api(project(":sharedcore:ui"))

                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.components)
                api(libs.androidx.lifecycle.viewmodel)

                implementation(libs.bundles.kmp.common)
                implementation(libs.coroutines.core)
                implementation(libs.serialization.json)
                implementation(libs.bundles.kmp.ktor)
                implementation(libs.koin.core)
                implementation(libs.napier)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.core.ktx)
                implementation(libs.coroutines.android)
                implementation(libs.koin.android)
                implementation(libs.androidx.activity.compose)
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

            }
        }
    }

}