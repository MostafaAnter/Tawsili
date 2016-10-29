# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/mostafa_anter/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# volley
-keep class com.android.volley.**{*;}
-dontwarn com.android.volley.**
-keep class cn.pedant.SweetAlert.**{*;}
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
# keep search view
-keep class android.support.v7.widget.SearchView { *; }
# for squer libs
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
#event bus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }