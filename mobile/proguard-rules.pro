# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line numbers for Crashlytics
-keepattributes SourceFile,LineNumberTable

# Strip println and print calls
-assumenosideeffects class kotlin.io.ConsoleKt {
    public static void print(...);
    public static void println(...);
}

# Strip Napier logging calls in release
# We keep 'e' (error) just in case, but since no Antilog is installed in release,
# it won't print anyway. Stripping v, d, i, w for better performance.
-assumenosideeffects class io.github.aakira.napier.Napier {
    public static void v(...);
    public static void d(...);
    public static void i(...);
    public static void w(...);
}

# Keep Compose related classes to avoid issues with minification
-keep class androidx.compose.runtime.Recomposer { *; }
-keep class androidx.compose.ui.platform.** { *; }

# Firebase / Google Identity
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
