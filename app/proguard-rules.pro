# Adere Secure Vault - Production ProGuard / R8 Keep Rules

# Room Database Keep Rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# Domain Models & Payload Serialization (JSON / Moshi / Reflection)
-keepclassmembers class com.example.adere.domain.model.** { *; }
-keep class com.example.adere.domain.model.** { *; }
-keepclassmembers class com.example.adere.data.local.** { *; }

# Cryptography & Security Engine
-keepclassmembers class com.example.adere.core.crypto.** { *; }
-keep class com.example.adere.core.crypto.** { *; }
-keepclassmembers class com.example.adere.core.backup.** { *; }
-keep class com.example.adere.core.backup.** { *; }

# Biometric & Keystore
-keep class androidx.biometric.** { *; }

# Google Mobile Ads (AdMob) & User Messaging Platform (UMP) Keep Rules
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }
-keep class com.google.android.ump.** { *; }
-keep interface com.google.android.ump.** { *; }
-keepclassmembers class com.example.adere.core.ads.** { *; }
-keep class com.example.adere.core.ads.** { *; }

# Kotlin Coroutines & Flow
-keepclassmembers class kotlinx.coroutines.** { *; }

# Preserve line numbers and annotations for debugging
-keepattributes SourceFile,LineNumberTable,*Annotation*
-renamesourcefileattribute SourceFile
