# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.demo.rickmorty.data.remote.dto.** { *; }
-keepclassmembers class kotlinx.serialization.json.** { *; }
