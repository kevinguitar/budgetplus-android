# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep remote data classes
-keep class com.kevlina.budgetplus.data.remote.** { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable
# Optional: Keep custom exceptions.
-keep public class * extends java.lang.Exception

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile