# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# 1. --- CRASHLYTICS & DEBUGGING ---
# Preserve line numbers and source file names for readable stack traces in Crashlytics
-keepattributes SourceFile,LineNumberTable

# 2. --- LOG STRIPPING (Optimization) ---
# Completely remove println and print calls from the release binary
-assumenosideeffects class kotlin.io.ConsoleKt {
    *** print(...);
    *** println(...);
}

# Strip Napier logging calls (except Error) for performance
-assumenosideeffects class io.github.aakira.napier.Napier {
    *** v(...);
    *** d(...);
    *** i(...);
    *** w(...);
}

# 3. --- JETPACK COMPOSE ---
# R8 (the modern ProGuard) handles most Compose rules automatically.
# We only keep the Recomposer as it's sometimes needed for reflection-based tools.
-keep class androidx.compose.runtime.Recomposer { *; }

# 4. --- KOTLINX SERIALIZATION ---
# Keep the generated serializers for all @Serializable classes
-keep @kotlinx.serialization.Serializable class **
-keepclassmembers class ** {
    *** Companion;
    *** $serializer;
}
# Keep the actual serializer classes
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keep class **$$serializer { *; }

# 5. --- FIRESTORE & MODELS ---
# Keep your data models exactly as they are (Firestore uses reflection)
# This rule covers all classes in 'model' packages across the project
-keep class **.model.** {
    public *;
    public <init>(...);
    <fields>;
}

# Preserve attributes used by Firestore and Reflection
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# 6. --- FIREBASE & GOOGLE SERVICES ---
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.firestore.**

# 7. --- KTOR & OKHTTP ---
# Ktor engine loading needs reflection
-keep class io.ktor.client.engine.android.** { *; }
-keep class io.ktor.client.engine.okhttp.** { *; }
# OkHttp internals
-keepattributes Signature, RuntimeVisibleSimpleAnnotations
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# 8. --- COROUTINES ---
# Prevent stripping of the Main dispatcher
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}

# 9. --- KOIN ---
# Keep Koin's internal proxy and scope logic
-keep class org.koin.core.scope.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.KoinInternalApi *;
}
