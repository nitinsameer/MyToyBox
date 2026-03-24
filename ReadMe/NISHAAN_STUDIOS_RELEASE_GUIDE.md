# Nishaan Studios â€” Android App Release Guide
**Reference document by Nitin & Ishaan | Created: March 2026**
> Use this as a checklist for every new app published under Nishaan Studios.

---

## 1. Accounts & One-Time Setup

- **Google Play Developer Account:** play.google.com/console Â· Account: Nishaan Studios Â· $25 one-time fee already paid
- **Google Account:** Use the same Google account for Play Console, Firebase, and Android Studio
- **Firebase Account:** console.firebase.google.com Â· Each app needs its own project Â· Free Spark plan is enough

---

## 2. Project Setup in Android Studio

### New project settings
```
Template:    Empty Activity (Compose)
Language:    Kotlin
Min SDK:     API 26 (Android 8.0)
Package:     com.nishaanstudios.APPNAME
```

### themes.xml (values folder)
```xml
<style name="Theme.App" parent="Theme.Material3.Light.NoActionBar"/>
```

### themes.xml (values-night folder â€” create manually)
```xml
<style name="Theme.App" parent="Theme.Material3.Dark.NoActionBar"/>
```

### file_paths.xml (res/xml â€” required for camera)
```xml
<paths>
  <external-files-path name="images" path="Pictures/"/>
</paths>
```

---

## 3. Firebase Setup

1. console.firebase.google.com â†’ Add project â†’ Enable Analytics
2. Add Android app â†’ enter package name com.nishaanstudios.APPNAME
3. Download google-services.json â†’ place in app/ folder (NOT inside src/)
4. **Without google-services.json the app WILL crash on launch**

### Project-level plugins
```kotlin
id("com.google.gms.google-services") version "4.4.1" apply false
id("com.google.firebase.crashlytics") version "2.9.9" apply false
```

### Firebase dependencies
```kotlin
implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
implementation("com.google.firebase:firebase-crashlytics")
implementation("com.google.firebase:firebase-analytics")
```

---

## 4. Keystore & Signing

> **CRITICAL:** Never delete the .jks file. Never lose the password. Without it you CANNOT update the app on Play Store ever again. Back up to Google Drive!

### Generate keystore
```
Build â†’ Generate Signed Bundle / APK â†’ Android App Bundle â†’ Create new keystore
Path:     D:\keys\APPNAME\my-APPNAME.jks
Alias:    nishaanstudios
Validity: 25 years
```

### keystore.properties (project root â€” NEVER commit to git)
```properties
KEYSTORE_PATH=D\:\\keys\\APPNAME\\my-APPNAME.jks
KEYSTORE_PASSWORD=yourpassword
KEY_ALIAS=nishaanstudios
KEY_PASSWORD=yourpassword
```

### .gitignore â€” always add
```
keystore.properties
*.jks
google-services.json
/build
```

---

## 5. build.gradle.kts Template (app level)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
android {
    namespace = "com.nishaanstudios.APPNAME"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.nishaanstudios.APPNAME"
        minSdk = 26; targetSdk = 35
        versionCode = 1; versionName = "1.0"
    }
    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("KEYSTORE_PATH") as String? ?: "")
            storePassword = project.findProperty("KEYSTORE_PASSWORD") as String? ?: ""
            keyAlias = project.findProperty("KEY_ALIAS") as String? ?: ""
            keyPassword = project.findProperty("KEY_PASSWORD") as String? ?: ""
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug { applicationIdSuffix = ".debug"; isDebuggable = true }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
    splits { abi { isEnable = false } }  // MUST be false for AAB
}
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

---

## 6. proguard-rules.pro Template

```pro
-keep class com.nishaanstudios.** { *; }
-keep class androidx.room.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**
-keep class kotlin.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**
-keep class coil.** { *; }
-dontwarn coil.**
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class timber.log.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keepattributes Signature,*Annotation*,SourceFile,LineNumberTable,Exceptions
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class androidx.security.crypto.** { *; }
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
# Firebase Tink (REQUIRED)
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn com.google.crypto.tink.**
-keep class com.google.crypto.tink.** { *; }
```

---

## 7. Production Release Steps

1. Bump versionCode by 1 and update versionName in build.gradle.kts
2. Build â†’ Clean Project
3. Build â†’ Generate Signed Bundle / APK â†’ Android App Bundle â†’ Release
4. Output: app/release/app-release.aab
5. Upload to Play Console â†’ Production â†’ Create new release

---

## 8. Play Store Upload Steps

### Required assets
- App icon: 512Ã—512px PNG
- Feature graphic: 1024Ã—500px JPG or PNG
- Screenshots: min 2, phone 1080Ã—1920px
- Short description: max 80 characters
- Full description: max 4000 characters

### Mandatory policy steps
- Content rating questionnaire
- Target audience (avoid under-13 â€” triggers strict Kids policy)
- Data safety form
- All must be green before publish

---

## 9. Troubleshooting

| Error | Fix |
|-------|-----|
| ClassNotFoundException on launch | AndroidManifest.xml wrong package name â€” use full path com.nishaanstudios.APPNAME.MainActivity |
| Compose Compiler plugin required | Add kotlin.plugin.compose to both gradle files. Remove composeOptions block. |
| Android resource linking failed | themes.xml parent must be Theme.Material3.Light.NoActionBar |
| ViewModel Redeclaration | Delete app/build/generated/source/kapt/debug/ â†’ Clean â†’ Rebuild |
| R8 missing classes Firebase Tink | Add 5 Tink dontwarn/keep rules to proguard-rules.pro |
| Multiple shrunk-resources AAB conflict | splits { abi { isEnable = false } } â†’ Clean â†’ rebuild AAB |
| SecurityException camera denied | Use ActivityResultContracts.RequestPermission() before launching camera |
| Unresolved reference firebase | Add google-services.json to app/ folder or remove Firebase deps |
| Red underlines in proguard-rules.pro | NOT an error â€” IDE limitation only. Build will succeed. |

---

## 10. Checklist Before Every Release

### Code
- [ ] versionCode incremented by 1
- [ ] versionName updated
- [ ] No hardcoded passwords in code
- [ ] keystore.properties in .gitignore
- [ ] google-services.json in .gitignore
- [ ] *.jks in .gitignore

### Build
- [ ] Build â†’ Clean Project done
- [ ] Tested on physical device
- [ ] splits abi isEnable = false confirmed
- [ ] AAB generated (not APK)
- [ ] No crash on fresh install
- [ ] All permissions working

### Play Store
- [ ] Screenshots updated if UI changed
- [ ] Release notes written
- [ ] Data safety form up to date
- [ ] Target API 34+ confirmed

### Key file locations
- google-services.json â†’ app/
- keystore.properties â†’ project root
- my-app.jks â†’ D:\keys\APPNAME\
- app-release.aab â†’ app/release/

---

*Nishaan Studios â€” Built with love by Nitin & Ishaan*
*First app: My Toy Box â€” Published March 2026*
