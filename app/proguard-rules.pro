# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep remote data classes
-keep class com.kevlina.budgetplus.core.data.remote.** { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable
# Optional: Keep custom exceptions.
-keep public class * extends java.lang.Exception

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Firebase auth
# https://github.com/firebase/firebase-android-sdk/issues/2124#issuecomment-920922929
-keep public class com.google.firebase.** {*;}
-keep class com.google.android.gms.internal.** {*;}
-keepclasseswithmembers class com.google.firebase.FirebaseException

# Required rules for r8
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.apiguardian.api.API$Status
-dontwarn org.apiguardian.api.API
-dontwarn com.google.protobuf.AbstractMessage$Builder
-dontwarn com.google.protobuf.AbstractMessage$BuilderParent
-dontwarn com.google.protobuf.AbstractMessage
-dontwarn com.google.protobuf.Descriptors$Descriptor
-dontwarn com.google.protobuf.Descriptors$FieldDescriptor
-dontwarn com.google.protobuf.Descriptors$FileDescriptor
-dontwarn com.google.protobuf.ExtensionRegistry
-dontwarn com.google.protobuf.GeneratedMessage$GeneratedExtension
-dontwarn com.google.protobuf.GeneratedMessage
-dontwarn com.google.protobuf.GeneratedMessageV3$Builder
-dontwarn com.google.protobuf.GeneratedMessageV3$BuilderParent
-dontwarn com.google.protobuf.GeneratedMessageV3$FieldAccessorTable
-dontwarn com.google.protobuf.GeneratedMessageV3
-dontwarn com.google.protobuf.MapEntry
-dontwarn com.google.protobuf.MapField
-dontwarn com.google.protobuf.Message
-dontwarn com.google.protobuf.MessageOrBuilder
-dontwarn com.google.protobuf.ProtocolMessageEnum
-dontwarn com.google.protobuf.RepeatedFieldBuilderV3
-dontwarn com.google.protobuf.SingleFieldBuilderV3
-dontwarn com.google.protobuf.TypeRegistry$Builder
-dontwarn com.google.protobuf.TypeRegistry
-dontwarn com.google.protobuf.UnknownFieldSet
-dontwarn com.google.protobuf.util.Durations
-dontwarn com.google.protobuf.util.JsonFormat$Printer
-dontwarn com.google.protobuf.util.JsonFormat
-dontwarn com.google.protobuf.util.Timestamps
-dontwarn org.eclipse.jetty.alpn.ALPN$ClientProvider
-dontwarn org.eclipse.jetty.alpn.ALPN$Provider
-dontwarn org.eclipse.jetty.alpn.ALPN$ServerProvider
-dontwarn org.eclipse.jetty.alpn.ALPN
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ClientProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$Provider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ServerProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego
-dontwarn reactor.blockhound.integration.BlockHoundIntegration