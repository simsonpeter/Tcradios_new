# TC RADIOS - TWA (Trusted Web Activity) Conversion Guide

This document provides a detailed step-by-step guide on how to convert the TC RADIOS web application into a Trusted Web Activity (TWA) and build an Android APK using Google Bubblewrap.

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Step 1: Update Web App Manifest](#step-1-update-web-app-manifest)
- [Step 2: Create TWA Verification File](#step-2-create-twa-verification-file)
- [Step 3: Install Google Bubblewrap CLI](#step-3-install-google-bubblewrap-cli)
- [Step 4: Initialize TWA Project](#step-4-initialize-twa-project)
- [Step 5: Build APK](#step-5-build-apk)
- [Step 6: Extract SHA256 Fingerprint](#step-6-extract-sha256-fingerprint)
- [Step 7: Update Asset Links](#step-7-update-asset-links)
- [Final Files Generated](#final-files-generated)
- [Testing and Deployment](#testing-and-deployment)
- [Troubleshooting](#troubleshooting)

## Overview

A Trusted Web Activity (TWA) allows you to package your Progressive Web App (PWA) as a native Android app. This provides:
- Native app experience without browser UI
- Access to device features through PWA APIs
- Distribution through Google Play Store
- Offline capabilities via service workers

## Prerequisites

- Node.js and npm installed
- Java JDK (for keytool)
- Your web app hosted and accessible via HTTPS
- Android development environment (for testing)

## Step 1: Update Web App Manifest

### Original manifest.json
```json
{
  "name": "TC RADIOS",
  "short_name": "TC",
  "description": "Uplifting Tamil & Malayalam Christian Radio Stations",
  "start_url": "/",
  "display": "standalone",
  "orientation": "portrait",
  "background_color": "#111827",
  "theme_color": "#f97316",
  "icons": [
    { "src": "/icons/icon-192x192.png", "sizes": "192x192", "type": "image/png", "purpose": "any maskable" },
    { "src": "/icons/icon-512x512.png", "sizes": "512x512", "type": "image/png", "purpose": "any maskable" }
  ],
  "categories": ["music", "entertainment", "lifestyle"]
}
```

### Updated manifest.json for TWA
```json
{
  "name": "TC RADIOS",
  "short_name": "TC",
  "description": "Uplifting Tamil & Malayalam Christian Radio Stations",
  "start_url": "/",
  "scope": "/",
  "display": "standalone",
  "orientation": "portrait",
  "background_color": "#111827",
  "theme_color": "#f97316",
  "icons": [
    { "src": "/icons/icon-192x192.png", "sizes": "192x192", "type": "image/png", "purpose": "any maskable" },
    { "src": "/icons/icon-512x512.png", "sizes": "512x512", "type": "image/png", "purpose": "any maskable" }
  ],
  "categories": ["music", "entertainment", "lifestyle"],
  "prefer_related_applications": false
}
```

### Key Changes Made:
- **Added `scope: "/"`** - Defines the navigation scope for the TWA
- **Added `prefer_related_applications: false`** - Prevents conflicts with native apps

## Step 2: Create TWA Verification File

### Create .well-known directory
```bash
mkdir -p .well-known
```

### Create assetlinks.json
```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "app.vercel.tcradios_new.twa",
    "sha256_cert_fingerprints": ["YOUR_SHA256_FINGERPRINT_HERE"]
  }
}]
```

**Note:** The SHA256 fingerprint will be updated later after generating the keystore.

## Step 3: Install Google Bubblewrap CLI

### Install Bubblewrap globally
```bash
npm install -g @bubblewrap/cli
```

### Verify installation
```bash
bubblewrap --version
```

## Step 4: Initialize TWA Project

### Run Bubblewrap initialization
```bash
bubblewrap init --manifest=https://tcradios-new.vercel.app/manifest.json --directory=twa
```

### Interactive Configuration
The command will prompt for the following information:

#### Web App Details (1/5)
- **Domain:** `tcradios-new.vercel.app`
- **URL path:** `/index.html`

#### Android App Details (2/5)
- **Application name:** `TC RADIOS`
- **Short name:** `tcradiod`
- **Application ID:** `app.vercel.tcradios_new.twa`
- **Starting version code:** `1`
- **Display mode:** `standalone`
- **Orientation:** `portrait`
- **Status bar color:** `#F97316`

#### Launcher Icons and Splash Screen (3/5)
- **Splash screen color:** `#111827`
- **Icon URL:** `https://tcradios-new.vercel.app/icons/icon-512x512.png`
- **Maskable icon URL:** `https://tcradios-new.vercel.app/icons/icon-512x512.png`

#### Optional Features (4/5)
- **Monochrome icon URL:** (leave empty)
- **Include support for Play Billing:** `No`
- **Request geolocation permission:** `No`

#### Signing Key Information (5/5)
- **Key store location:** `/path/to/twa/android.keystore`
- **Key name:** `android`

### Keystore Creation
When prompted to create a new keystore:
- **First and Last names:** `Simson peter`
- **Organizational Unit:** `1`
- **Organization:** `jayathasoft`
- **Country:** `be`
- **Password for the Key Store:** (choose a secure password)
- **Password for the Key:** (choose a secure password)

## Step 5: Build APK

### Navigate to TWA directory
```bash
cd twa
```

### Build the APK
```bash
bubblewrap build
```

### Enter keystore passwords
When prompted, enter the passwords you set during keystore creation.

### Generated Files
After successful build, you'll have:
- `app-release-signed.apk` - Ready-to-install Android APK
- `app-release-bundle.aab` - Google Play Store bundle
- `android.keystore` - Signing key for future updates

## Step 6: Extract SHA256 Fingerprint

### Install Java JDK (if not already installed)
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
```

### Extract fingerprint from keystore
```bash
keytool -list -v -keystore android.keystore -alias android
```

### Sample Output
```
Certificate fingerprints:
         SHA1: 34:D3:AC:FD:6E:55:F1:7B:E3:60:A3:BB:89:26:19:D3:49:C4:9C:36
         SHA256: 6F:33:BF:A6:2F:03:2D:F7:9C:FC:1C:6C:75:3F:A7:8E:F6:12:13:67:BA:C9:BA:B5:EE:F2:57:F6:6F:5F:7B:47
```

**Copy the SHA256 fingerprint** (without spaces or colons for the assetlinks.json file).

## Step 7: Update Asset Links

### Update .well-known/assetlinks.json
```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "app.vercel.tcradios_new.twa",
    "sha256_cert_fingerprints": ["6F:33:BF:A6:2F:03:2D:F7:9C:FC:1C:6C:75:3F:A7:8E:F6:12:13:67:BA:C9:BA:B5:EE:F2:57:F6:6F:5F:7B:47"]
  }
}]
```

### Deploy assetlinks.json
Ensure the file is accessible at:
```
https://tcradios-new.vercel.app/.well-known/assetlinks.json
```

## Final Files Generated

### TWA Directory Structure
```
twa/
├── android.keystore                    # Signing key
├── app-release-signed.apk             # Installable APK (1.66 MB)
├── app-release-bundle.aab             # Play Store bundle (1.78 MB)
├── app-release-unsigned-aligned.apk   # Unsigned APK
├── twa-manifest.json                  # TWA configuration
├── build.gradle                       # Build configuration
├── settings.gradle                    # Gradle settings
├── gradlew                           # Gradle wrapper
└── app/                              # Android project files
```

### Key Configuration Files

#### twa-manifest.json
```json
{
  "packageId": "app.vercel.tcradios_new.twa",
  "host": "tcradios-new.vercel.app",
  "name": "TC RADIOS",
  "launcherName": "tcradiod",
  "display": "standalone",
  "themeColor": "#F97316",
  "backgroundColor": "#111827",
  "enableNotifications": true,
  "startUrl": "/index.html",
  "iconUrl": "https://tcradios-new.vercel.app/icons/icon-512x512.png",
  "maskableIconUrl": "https://tcradios-new.vercel.app/icons/icon-512x512.png",
  "signingKey": {
    "path": "/path/to/twa/android.keystore",
    "alias": "android"
  },
  "appVersionName": "1",
  "appVersionCode": 1
}
```

## Testing and Deployment

### Install APK on Android Device
```bash
adb install app-release-signed.apk
```

### Test TWA Features
- Verify app opens in full-screen mode
- Test offline functionality
- Check notification permissions
- Verify app shortcuts work

### Publish to Google Play Store
1. Upload `app-release-bundle.aab` to Google Play Console
2. Complete store listing information
3. Submit for review

## Troubleshooting

### Common Issues

#### 1. "Invalid URL" Error
**Problem:** Bubblewrap fails with "Invalid URL" error
**Solution:** Ensure your web app is accessible via HTTPS and the manifest.json is properly formatted

#### 2. Keystore Password Issues
**Problem:** Cannot access keystore
**Solution:** Use the same password you set during keystore creation

#### 3. Asset Links Not Working
**Problem:** TWA verification fails
**Solution:** 
- Verify assetlinks.json is accessible at `https://yourdomain.com/.well-known/assetlinks.json`
- Check SHA256 fingerprint matches exactly
- Ensure package name is correct

#### 4. App Not Opening in Full Screen
**Problem:** App opens in browser instead of TWA mode
**Solution:** 
- Verify assetlinks.json is properly deployed
- Check manifest.json has correct scope
- Ensure TWA verification passes

### Verification Commands

#### Check TWA Verification
```bash
curl -s https://tcradios-new.vercel.app/.well-known/assetlinks.json | jq .
```

#### Validate APK
```bash
aapt dump badging app-release-signed.apk
```

## Security Considerations

1. **Keep keystore secure** - Store `android.keystore` in a safe location
2. **Use strong passwords** - Choose complex passwords for keystore and key
3. **Regular updates** - Update your web app regularly for security patches
4. **HTTPS only** - Ensure your web app only uses HTTPS

## Performance Optimization

1. **Service Worker** - Implement proper caching strategies
2. **Image optimization** - Use WebP format for icons and images
3. **Code splitting** - Implement lazy loading for better performance
4. **Offline support** - Ensure critical functionality works offline

## Future Updates

### Updating Your TWA
1. Update your web app
2. Increment version in `twa-manifest.json`
3. Rebuild APK:
   ```bash
   cd twa
   bubblewrap build
   ```

### Version Management
- Update `appVersionCode` for each release
- Update `appVersionName` for user-facing version
- Keep track of changes in release notes

## Conclusion

Your TC RADIOS web app has been successfully converted into a Trusted Web Activity and packaged as an Android APK. The TWA provides a native app experience while maintaining all the benefits of your Progressive Web App.

### Key Benefits Achieved:
- ✅ Native Android app experience
- ✅ Full-screen display (no browser UI)
- ✅ Access to device features
- ✅ Offline capabilities
- ✅ App store distribution ready
- ✅ Automatic updates via web app

### Next Steps:
1. Deploy the updated assetlinks.json file
2. Test the APK on Android devices
3. Consider publishing to Google Play Store
4. Monitor app performance and user feedback

---

**Created by:** AI Assistant  
**Date:** September 5, 2025  
**Project:** TC RADIOS TWA Conversion  
**Web App:** https://tcradios-new.vercel.app/
