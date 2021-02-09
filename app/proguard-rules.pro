# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.snaggly.ksw_toolkit.core.config.ConfigData {*;}
-keep class com.snaggly.ksw_toolkit.core.config.custom.** {*;}
-keep class com.snaggly.ksw_toolkit.core.config.beans.** {*;}
-keep class com.snaggly.ksw_toolkit.util.enums.** {*;}
-keep class com.snaggly.ksw_toolkit.util.mcu.McuCommandsEnum {*;}
-keep class com.wits.pms.* {*;}
-keep class com.wits.pms.statuscontrol.* {*;}
-keep class projekt.auto.mcu.** {*;}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}