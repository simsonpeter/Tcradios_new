# Smartwatch App for TC RADIOS

## Overview

TC RADIOS Smartwatch app brings uplifting Christian music and messages to your wrist. The app is designed for multiple smartwatch platforms including Wear OS, watchOS, Tizen, and Fitbit with optimized interfaces for small screens and gesture-based navigation.

## Features

### 🎵 **Audio Features**
- **Multi-Language Support**: Tamil, English, Malayalam, Hindi, Dutch
- **Favorites System**: Save and organize favorite stations
- **Voice Control**: "Hey Google, play Christian music"
- **Bluetooth Audio**: Connect to wireless headphones
- **Offline Mode**: Download stations for offline listening

### ⌚ **Smartwatch Integration**
- **Gesture Control**: Swipe, tap, and long-press navigation
- **Voice Commands**: Full voice control integration
- **Complications**: Watch face widgets
- **Always On Display**: Keep playing info visible
- **Ambient Mode**: Low-power display mode
- **Battery Optimization**: Efficient power management

### 📱 **User Interface**
- **Multi-Platform**: Works on Wear OS, watchOS, Tizen, Fitbit
- **Gesture Navigation**: Intuitive touch controls
- **Round/Square Support**: Adapts to different watch shapes
- **High Contrast**: Clear visibility in all lighting
- **Responsive Layout**: Optimized for small screens

## Supported Platforms

### Wear OS (Google)
- **Models**: Samsung Galaxy Watch, Fossil Gen 6, TicWatch Pro 3
- **Features**: Full voice control, complications, always-on display
- **Installation**: Google Play Store

### watchOS (Apple)
- **Models**: Apple Watch Series 3+, Apple Watch SE
- **Features**: Siri integration, complications, always-on display
- **Installation**: App Store

### Tizen (Samsung)
- **Models**: Samsung Galaxy Watch Active, Galaxy Watch 3
- **Features**: Bixby integration, complications, always-on display
- **Installation**: Galaxy Store

### Fitbit
- **Models**: Fitbit Versa, Fitbit Sense, Fitbit Ionic
- **Features**: Basic voice control, complications
- **Installation**: Fitbit App Gallery

## Installation

### Prerequisites
- Smartwatch with compatible platform
- Internet connection
- Bluetooth headphones (optional)

### Installation Methods

#### 1. App Store Installation
```bash
# Wear OS: Google Play Store
# watchOS: App Store
# Tizen: Galaxy Store
# Fitbit: Fitbit App Gallery
```

#### 2. Developer Installation
```bash
# Enable Developer Mode on smartwatch
# Install via USB or wireless debugging
```

#### 3. Manual Installation
```bash
# Copy app files to smartwatch
# Requires platform-specific tools
```

## Development

### File Structure
```
smartwatch-app/
├── manifest.json              # Main app configuration
├── index.html                 # Main app interface
├── css/
│   └── smartwatch-style.css   # Smartwatch-optimized styles
├── js/
│   ├── smartwatch-api.js      # Smartwatch API integration
│   └── smartwatch-app.js      # Main app logic
├── images/
│   ├── logo.png               # App logo
│   └── default-station.png    # Default station image
├── platforms/
│   ├── wearos/                # Wear OS specific files
│   ├── watchos/               # watchOS specific files
│   ├── tizen/                 # Tizen specific files
│   └── fitbit/                # Fitbit specific files
└── assets/                    # Additional assets
```

### Platform-Specific Development

#### Wear OS Development
```bash
# Install Android Studio
# Install Wear OS SDK
# Create Wear OS project
# Import TC RADIOS app files
```

#### watchOS Development
```bash
# Install Xcode
# Install watchOS SDK
# Create watchOS project
# Import TC RADIOS app files
```

#### Tizen Development
```bash
# Install Tizen Studio
# Install Tizen SDK
# Create Tizen project
# Import TC RADIOS app files
```

#### Fitbit Development
```bash
# Install Fitbit Studio
# Install Fitbit SDK
# Create Fitbit project
# Import TC RADIOS app files
```

## Configuration

### App Manifest
```json
{
  "name": "TC RADIOS",
  "version": "1.0.0",
  "smartwatch": {
    "platforms": ["wearos", "watchos", "tizen", "fitbit"],
    "voiceCommands": [...],
    "gestures": [...],
    "complications": true,
    "alwaysOnDisplay": true,
    "ambientMode": true
  }
}
```

### Language Configuration
```javascript
const LANG_URLS = {
  tamil: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/stations.json',
  english: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/english.json',
  // ... other languages
};
```

## Voice Commands

### Basic Commands
- **"Play Christian Music"** - Start playing English Christian stations
- **"Play Tamil Music"** - Switch to Tamil stations
- **"Play English Music"** - Switch to English stations
- **"Play Favorites"** - Open favorites screen
- **"Next Station"** - Skip to next station
- **"Previous Station"** - Go to previous station

### Volume Control
- **"Volume Up"** - Increase volume by 10%
- **"Volume Down"** - Decrease volume by 10%
- **"Mute"** - Mute audio
- **"Unmute"** - Unmute audio

### Playback Control
- **"Stop Music"** - Stop current station
- **"Pause Music"** - Pause playback
- **"Resume Music"** - Resume playback

## Gesture Controls

### Touch Gestures
- **Tap** - Select item
- **Double Tap** - Toggle favorite
- **Long Press** - Show menu
- **Swipe Up** - Next screen
- **Swipe Down** - Previous screen
- **Swipe Left** - Next station
- **Swipe Right** - Previous station

### Gesture Implementation
```javascript
// Swipe gestures
SmartwatchAPI.gestures.onSwipeUp(() => {
    this.nextScreen();
});

// Tap gestures
SmartwatchAPI.gestures.onDoubleTap(() => {
    this.toggleFavorite();
});
```

## Complications (Watch Face Widgets)

### Supported Types
- **Short Text** - Station name
- **Long Text** - Station name + genre
- **Icon** - App icon
- **Small Image** - Station logo

### Complication Updates
```javascript
// Update watch face complication
SmartwatchAPI.complications.updateNowPlaying(
    stationName,
    isPlaying
);
```

## Battery Optimization

### Power Management
- **Low Power Mode** - Reduces animations when battery < 20%
- **Screen Timeout** - Auto-lock after 30 seconds of inactivity
- **Ambient Mode** - Low-power display mode
- **Always On Display** - Keep essential info visible

### Battery Features
```javascript
// Monitor battery level
setInterval(() => {
    const batteryLevel = SmartwatchAPI.device.getBatteryLevel();
    if (batteryLevel < 20) {
        this.enableLowPowerMode();
    }
}, 60000);
```

## Testing

### Emulator Testing
```bash
# Wear OS: Android Studio Emulator
# watchOS: Xcode Simulator
# Tizen: Tizen Studio Emulator
# Fitbit: Fitbit Studio Emulator
```

### Device Testing
```bash
# Test on actual smartwatch
# Verify gesture controls
# Test voice commands
# Check battery optimization
```

### Voice Command Testing
```bash
# Test all voice commands
# Verify proper execution
# Check platform-specific integration
```

## Deployment

### Production Build
1. **Optimize Assets**: Compress images and minify code
2. **Test Thoroughly**: Test on multiple smartwatch models
3. **Voice Testing**: Verify all voice commands work
4. **Gesture Testing**: Ensure all gestures work correctly
5. **Battery Testing**: Verify power optimization
6. **Submit to Store**: Submit to respective app stores

### Distribution
- **Wear OS**: Google Play Store
- **watchOS**: App Store
- **Tizen**: Galaxy Store
- **Fitbit**: Fitbit App Gallery

## Troubleshooting

### Common Issues

1. **Voice Commands Not Working**
   - Check platform-specific voice integration
   - Verify microphone permissions
   - Test voice commands in platform voice system

2. **Audio Not Playing**
   - Check internet connection
   - Verify station URLs are accessible
   - Check smartwatch audio settings

3. **Gestures Not Responding**
   - Check touch sensitivity settings
   - Verify gesture recognition
   - Test on different smartwatch models

4. **Battery Drain**
   - Enable battery optimization
   - Use ambient mode
   - Reduce screen brightness

### Debug Tips

1. **Enable Console Logging**
   ```javascript
   console.log('Debug info:', data);
   ```

2. **Check Platform Status**
   ```javascript
   SmartwatchAPI.platform.getPlatform();
   ```

3. **Monitor Battery Level**
   ```javascript
   SmartwatchAPI.device.getBatteryLevel();
   ```

## Support

### Contact Information
- **Email**: simsonpeter@gmail.com
- **Website**: https://tcradios-new.vercel.app
- **GitHub**: https://github.com/simsonpeter/Tcradios_new

### Platform Resources
- **Wear OS**: https://developer.android.com/wear
- **watchOS**: https://developer.apple.com/watchos/
- **Tizen**: https://developer.tizen.org/
- **Fitbit**: https://dev.fitbit.com/

## License

MIT License - See LICENSE file for details.

## Changelog

### Version 1.0.0
- ✅ Initial smartwatch app release
- ✅ Multi-platform support (Wear OS, watchOS, Tizen, Fitbit)
- ✅ Voice command integration
- ✅ Gesture control system
- ✅ Multi-language support
- ✅ Favorites system
- ✅ Complications support
- ✅ Always on display
- ✅ Ambient mode
- ✅ Battery optimization
- ✅ Bluetooth audio support
