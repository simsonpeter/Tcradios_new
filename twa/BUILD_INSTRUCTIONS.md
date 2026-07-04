# Bubblewrap TWA Build Instructions

This Android app is a **Bubblewrap Trusted Web Activity (TWA)** project. You do
not need Android Studio to build it, and you do not need a local Android setup if
you use the GitHub Actions workflow. Bubblewrap still uses Android command-line
build tools underneath, so a local build needs Java + Android SDK, while the
cloud workflow installs those for you.

## Recommended: Build with GitHub Actions

Use the repository workflow from GitHub:

1. Open **Actions** in GitHub.
2. Run **Build TCRadios APK / AAB** manually, or push to `main`.
3. Download the generated APK/AAB artifacts.

This is the best path when you do not have an Android environment locally.

## Local Bubblewrap Build

Only use this if your machine already has Java and Android SDK command-line
tools installed.

```bash
npm install -g @bubblewrap/cli
cd twa

# Build from twa-manifest.json
bubblewrap build
```

Bubblewrap reads `twa/twa-manifest.json` and builds the TWA project. The native
Android Auto media service is part of this generated TWA project and is kept in:

```text
twa/app/src/main/java/com/jayathasoft/tcradios/app/AndroidAutoMediaService.java
twa/app/src/main/AndroidManifest.xml
twa/app/src/main/res/xml/automotive_app_desc.xml
```

## Local prerequisites, if building on your computer

1. **JDK 17**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk
   ```

2. **Android SDK Command Line Tools**
   - Set `ANDROID_HOME`.
   - Add `$ANDROID_HOME/platform-tools` and Android command-line tools to `PATH`.

3. **Bubblewrap CLI**
   ```bash
   npm install -g @bubblewrap/cli
   ```

## Gradle fallback

Bubblewrap uses Gradle internally. If needed, you can build the generated
project directly:

```bash
cd twa
./gradlew assembleRelease
```

## Android Studio

Android Studio is optional. It can open the `twa` folder for debugging, but it is
not required for the normal Bubblewrap/GitHub Actions build path.

## Signing manually

If you built an unsigned APK, sign it manually:

```bash
# Sign the APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore jayathasoft.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  jayathasoft-keystore

# Align the APK (optional but recommended)
zipalign -v 4 \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  app-release-signed.apk
```

## Output Location

After building, the signed APK will be at:
- `app/build/outputs/apk/release/app-release-signed.apk` (if signed during build)
- Or manually signed: `app-release-signed.apk`

## Version Information

- **Version Code**: 30 (incremented for native Android Auto media service)
- **Version Name**: "Android Auto Native Media"
- **Package ID**: com.jayathasoft.tcradios.app

## Android Auto Changes

This build includes fixes for Android Auto:
- ✅ Native MediaBrowserServiceCompat for Android Auto discovery
- ✅ Native MediaSessionCompat transport controls
- ✅ Direct native radio stream playback for Android Auto
- ✅ Background playback state maintenance
- ✅ Automotive descriptor declares **media only** so Android Auto lists radios
  instead of notification/messages

## Troubleshooting

### Java not found
```bash
sudo apt install openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$PATH:$JAVA_HOME/bin
```

### Android SDK / ANDROID_HOME not found

Use GitHub Actions, or install Android SDK command-line tools locally and set:

```bash
export ANDROID_HOME=/path/to/android-sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### Bubblewrap/Gradle build fails
```bash
bubblewrap doctor
./gradlew clean
./gradlew --refresh-dependencies
bubblewrap build
```

### Keystore Issues
- Ensure `jayathasoft.keystore` exists in the `twa` directory
- You'll need the keystore password and alias password to sign the APK

## Next Steps

1. Install the new APK on your Android device
2. Test Android Auto functionality
3. Upload to Google Play Console if ready for release
