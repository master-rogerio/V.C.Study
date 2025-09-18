# Compose
-keep class androidx.compose.** { *; }
-keep class androidx.compose.ui.platform.** { *; }

# Resolver problemas de hover
-keep class androidx.compose.ui.platform.AndroidComposeView { *; }
-keep class androidx.compose.ui.platform.AndroidComposeView$* { *; }

# ViewTranslation
-dontwarn android.view.translation.ViewTranslationCallback
-dontwarn android.view.translation.ViewTranslation
-keep class android.view.translation.** { *; }
-dontwarn androidx.compose.ui.platform.AndroidComposeView$ViewTranslationCallback


# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}


