# TWA Build Instructions

## Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   # Ubuntu/Debian
   sudo apt update
   sudo apt install openjdk-11-jdk
   
   # Or install JDK 17 (recommended)
   sudo apt install openjdk-17-jdk
   ```

2. **Android SDK** (if not using Android Studio)
   - Download Android SDK Command Line Tools
   - Set `ANDROID_HOME` environment variable
   - Add `$ANDROID_HOME/platform-tools` and `$ANDROID_HOME/tools` to PATH

3. **Set JAVA_HOME**
   ```bash
   # Find Java installation
   sudo update-alternatives --config java
   
   # Set JAVA_HOME (add to ~/.bashrc or ~/.zshrc)
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64  # Adjust path as needed
   export PATH=$PATH:$JAVA_HOME/bin
   ```

## Building the Release APK

### Option 1: Using Gradle Wrapper (Recommended)

```bash
cd /home/simsonpeter/github/tcradios/Tcradios_new/twa

# Clean previous builds
./gradlew clean

# Build release APK (unsigned)
./gradlew assembleRelease

# The APK will be at: app/build/outputs/apk/release/app-release.apk
```

### Option 2: Using Android Studio

1. Open Android Studio
2. Open the `twa` folder as a project
3. Go to **Build → Generate Signed Bundle / APK**
4. Select **APK**
5. Choose the keystore file: `jayathasoft.keystore`
6. Enter keystore password and alias password
7. Select **release** build variant
8. Click **Finish**

### Option 3: Sign the APK Manually

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

- **Version Code**: 29 (incremented for Android Auto fixes)
- **Version Name**: "Android Auto"
- **Package ID**: com.jayathasoft.tcradios.app

## Android Auto Changes

This build includes fixes for Android Auto:
- ✅ Media Session playback state updates on play/pause
- ✅ Proper position state for Android Auto
- ✅ Enhanced Media Session handlers
- ✅ Background playback state maintenance

## Troubleshooting

### Java Not Found
```bash
# Install Java
sudo apt install openjdk-17-jdk

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$PATH:$JAVA_HOME/bin
```

### Gradle Build Fails
```bash
# Clean and rebuild
./gradlew clean
./gradlew --refresh-dependencies
./gradlew assembleRelease
```

### Keystore Issues
- Ensure `jayathasoft.keystore` exists in the `twa` directory
- You'll need the keystore password and alias password to sign the APK

## Next Steps

1. Install the new APK on your Android device
2. Test Android Auto functionality
3. Upload to Google Play Console if ready for release
