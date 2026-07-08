# ── Memento-Sol ProGuard ──

# Keep Room entities
-keep class com.memento.sol.** { *; }

# General
-dontwarn javax.naming.**
-dontwarn lombok.Generated
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Remove verbose logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}