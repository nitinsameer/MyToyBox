# 🔥 Firebase Setup Guide for MyToyBox

## Step-by-Step Firebase Configuration

### **Step 1: Create Firebase Project**

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Create a project"
3. Enter Project Name: **"MyToyBox"**
4. Accept terms and create

### **Step 2: Register Android App in Firebase**

1. Click "Add app" or the Android icon
2. Fill in App Details:
   - **Package name**: `com.toybox.app`
   - **App nickname**: `MyToyBox`
   - **SHA-1 certificate hash** (see Step 3)
3. Click "Register app"

### **Step 3: Get SHA-1 Certificate Hash**

Run this command in your project directory:

```bash
# For Windows (PowerShell):
cd C:\Users\nitin\AndroidStudioProjects\MyToyBox
./gradlew signingReport

# For Mac/Linux:
./gradlew signingReport
```

You'll see output like:
```
Variant: debug
Config: debug
Store: C:\Users\...\.android\debug.keystore
Alias: AndroidDebugKey
MD5: ...
SHA1: AA:BB:CC:DD:... (COPY THIS)
SHA-256: ...
```

**Copy the SHA1 value** and paste it into Firebase Console.

### **Step 4: Download google-services.json**

1. After registering the app, Firebase will generate `google-services.json`
2. Click "Download google-services.json"
3. Move the file to: `app/google-services.json`

```
MyToyBox/
└── app/
    └── google-services.json  ← Place here
```

### **Step 4.5: Re-enable Firebase Plugins** ⭐ IMPORTANT

The Firebase plugins are commented out in `app/build.gradle.kts` to avoid build errors. After you place the real `google-services.json`, uncomment them:

In `app/build.gradle.kts`, change:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-kapt")
    // Firebase plugins were commented out temporarily
    // id("com.google.gms.google-services")
    // id("com.google.firebase.crashlytics")
}
```

To:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-kapt")
    id("com.google.gms.google-services")  // Uncomment this
    id("com.google.firebase.crashlytics") // Uncomment this
}
```

Then rebuild:
```bash
./gradlew clean build
```

### **Step 5: Enable Firebase Services**

In Firebase Console:

1. **Go to "Build" menu**
2. **Click "Crashlytics"** → Enable (auto-enabled with plugin)
3. **Click "Analytics"** → Enable
4. **Click "Cloud Messaging"** (optional, for push notifications later)

### **Step 6: Get Release SHA-1 for Play Store**

Before submitting to Play Store, you need the release SHA-1:

```bash
# Generate a release keystore (run once)
keytool -genkey -v -keystore toybox-release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias toybox

# Get SHA-1 from release keystore
keytool -list -v -keystore toybox-release.keystore -alias toybox

# Copy the SHA1 value and add it to Firebase Console
```

**Save this file safely**: `toybox-release.keystore`

In Firebase Console:
- Go to Project Settings
- Add SHA-1 certificate from release keystore

### **Step 7: Update build.gradle with Signing Config**

Add to `app/build.gradle.kts`:

```kotlin
android {
    // ... existing config ...
    
    signingConfigs {
        create("release") {
            storeFile = file("../toybox-release.keystore")
            storePassword = "YOUR_PASSWORD"  // Same password you set above
            keyAlias = "toybox"
            keyPassword = "YOUR_PASSWORD"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### **Step 8: Verify Integration**

1. Build and run the app:
```bash
./gradlew assembleDebug
```

2. Install and run on device/emulator
3. Check Firebase Console → Crashlytics for real-time data
4. Trigger a test crash to verify:

In `MainActivity.kt`, add a test button in debug mode:
```kotlin
Button(onClick = { throw Exception("Test crash") }) {
    Text("Test Crash")
}
```

### **Step 9: Clean Up Test Crash**

Remove the test crash button before submission.

---

## ✅ Firebase Setup Checklist

- [ ] Firebase project created
- [ ] Android app registered in Firebase
- [ ] SHA-1 debug certificate added to Firebase
- [ ] `google-services.json` downloaded and placed in `app/` folder
- [ ] Crashlytics enabled in Firebase Console
- [ ] Analytics enabled in Firebase Console
- [ ] Release keystore generated (`toybox-release.keystore`)
- [ ] Release SHA-1 added to Firebase Console
- [ ] Signing config added to `build.gradle.kts`
- [ ] Debug build tested with crash reporting
- [ ] Test crash verified in Firebase Crashlytics

---

## 🚀 After Firebase Setup

1. Build release APK:
```bash
./gradlew assembleRelease
```

2. You're ready for Play Store submission!

---

## 🔒 Security Notes

- **Never commit keystore files** to git
- **Never share passwords** publicly
- **Never commit google-services.json** if it contains sensitive keys
- Add to `.gitignore`:
```
# Firebase
google-services.json
*.keystore

# Gradle
*.gradle.kts.orig
local.properties
```

---

## 📞 Troubleshooting

### App not sending crash reports?
- Check Internet permission in AndroidManifest.xml ✅ Already added
- Verify `google-services.json` is in correct location
- Check Firebase Console → Project Settings → Apps

### Build fails with "google-services not found"?
- Ensure `google-services.json` is in `app/` folder
- Run: `./gradlew clean build`

### SHA-1 mismatch error?
- You're using wrong keystore
- Debug: Use debug.keystore SHA-1
- Release: Use toybox-release.keystore SHA-1
- Add both to Firebase Console



