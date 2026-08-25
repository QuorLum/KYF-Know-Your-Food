# =====================================================================
# KYF — Know Your Food — ProGuard / R8 Rules
# =====================================================================

# ─── Room Database ───────────────────────────────────────────────────
-keep class com.kyf.knowyourfood.data.local.entity.** { *; }
-keep class com.kyf.knowyourfood.data.local.dao.** { *; }

# ─── Kotlin Serialization ───────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.kyf.knowyourfood.data.model.**$$serializer { *; }
-keepclassmembers class com.kyf.knowyourfood.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.kyf.knowyourfood.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep the AI domain model used by Gemini deserialization
-keep class com.kyf.knowyourfood.domain.ai.RecognizedFoodItem { *; }

# ─── OkHttp ─────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ─── Google Generative AI SDK ────────────────────────────────────────
-keep class com.google.ai.client.generativeai.** { *; }

# ─── ML Kit Barcode Scanning ────────────────────────────────────────
-keep class com.google.mlkit.** { *; }

# ─── Coil Image Loading ────────────────────────────────────────────
-keep class coil.** { *; }

# ─── Prevent stripping of Compose ────────────────────────────────────
-keep class androidx.compose.** { *; }

# ─── Security: Obfuscate BuildConfig fields (API keys) ──────────────
# R8 will obfuscate field names, making it harder to extract keys
# via reverse engineering. Note: keys in BuildConfig are still
# accessible at runtime; for production, use Android Keystore or
# a backend proxy for API calls.
