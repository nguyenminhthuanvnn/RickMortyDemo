# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.demo.rickmorty.data.remote.dto.** { *; }

# Moshi rules
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.squareup.moshi.**
