# Android Auto Integration for TC RADIOS

## Overview
TC RADIOS now includes comprehensive Android Auto integration, allowing users to safely control Christian radio stations while driving.

## Features

### 🚗 **Car-Optimized Interface**
- Large, easy-to-read station list
- Touch-friendly controls
- Driving-safe UI elements
- Voice command support

### 🎵 **Media Controls**
- Play/Pause stations
- Next/Previous station navigation
- Favorites access
- Volume control integration

### 🗣️ **Voice Commands**
Supported voice commands:
- "Play TC RADIOS"
- "Play Tamil radio" / "Play English radio" / "Play Malayalam radio"
- "Pause radio" / "Stop radio"
- "Next station" / "Previous station"
- "Show favorites"
- "Switch to [language] stations"

### 📱 **Media Session Integration**
- Rich media metadata display
- Album artwork and station logos
- Now playing information
- Playback state synchronization

## Technical Implementation

### Native Android Auto Media Integration
TC RADIOS exposes a native `MediaBrowserServiceCompat` from the Android app so Android Auto can discover it as a media app. The web/TWA app still provides the full phone UI, while Android Auto receives a car-safe station browser and transport controls from the native media service.

### Core Technologies
1. **MediaBrowserServiceCompat** - Required Android Auto entry point for media apps
2. **MediaSessionCompat** - Rich media controls and playback state
3. **Native MediaPlayer stream playback** - Plays radio streams directly from Android Auto
4. **Progressive Web App (PWA)** - Full app UI on the phone
5. **Trusted Web Activity (TWA)** - Native Android app wrapper

### Key Features
- **Native Media Browser** - Lets Android Auto list TC RADIOS in media apps
- **Media Session Integration** - Full Android Auto media controls
- **Voice Command Support** - Web Speech API for hands-free control
- **Automotive UI Optimization** - Large touch targets and simplified interface
- **Background Playback** - Continues playing when Android Auto interface changes
- **Rich Metadata Display** - Station info, logos, and now playing details

### Key Files
- `twa/app/src/main/java/com/jayathasoft/tcradios/app/AndroidAutoMediaService.java` - Native Android Auto station browser and playback service
- `twa/app/src/main/AndroidManifest.xml` - Declares the media browser service and Android Auto metadata
- `index.html` - Enhanced with media session support for the phone/web player
- `automotive_app_desc.xml` - Android Auto app descriptor
- `automotive.xml` - Android Auto metadata and voice command examples

## Installation & Setup

### Prerequisites
1. Android Auto app installed on phone
2. Compatible car or Android Auto head unit
3. TC RADIOS Android app version 30 or newer installed from the latest APK/AAB build

### Usage
1. Install the latest TC RADIOS Android APK/AAB on the phone.
2. Open TC RADIOS once on the phone so Android registers the app.
3. Connect phone to car via USB or wireless.
4. In Android Auto, open the app launcher or media app list.
5. Select TC RADIOS and choose a station.
6. Use car touch controls, steering wheel buttons, or Google Assistant transport commands.

## Voice Command Examples

### Basic Playback
- "OK Google, play TC RADIOS"
- "OK Google, pause music"
- "OK Google, resume music"

### Station Navigation
- "OK Google, next station"
- "OK Google, previous station"
- "OK Google, show favorites"

### Language Selection
- "OK Google, play Tamil radio"
- "OK Google, switch to English stations"
- "OK Google, play Malayalam Christian radio"

## Safety Features

### Driving Mode Optimizations
- Simplified interface with large touch targets
- Voice-first interaction design
- Minimal visual distractions
- Automatic background operation

### Hands-Free Operation
- Complete voice control support
- Media button integration
- Steering wheel control compatibility
- Notification-based feedback

## Troubleshooting

### Common Issues

**Q: TC RADIOS doesn't appear in Android Auto**
A: Install a freshly built APK/AAB from version 30 or newer. Web deployment alone cannot update Android Auto because the native media service is inside the Android app.

**Q: Voice commands not working**
A: Check microphone permissions and ensure Android Auto voice recognition is enabled.

**Q: Stations not loading in car**
A: Verify internet connection and check if the app works normally on phone first.

**Q: Audio cutting out**
A: Check Bluetooth connection quality and ensure phone has stable internet connection.

### Debug Information
- Enable Android Auto developer mode for detailed logs
- Check `adb logcat` for TC RADIOS specific messages
- Verify network connectivity in car environment

## Compatibility

### Supported Vehicles
- All Android Auto compatible vehicles (2016+)
- Android Auto wireless compatible cars (2018+)
- Aftermarket Android Auto head units

### Android Requirements
- Android 6.0+ (API level 23)
- Android Auto app version 4.0+
- TC RADIOS app version 28+

## Future Enhancements

### Planned Features
- Offline station caching for poor connectivity areas
- Custom equalizer settings for car audio
- Integration with car's native voice assistant
- Multi-user profile support
- Advanced metadata display (song lyrics, artist info)

### Development Roadmap
- Phase 1: Core Android Auto functionality ✅
- Phase 2: Advanced voice commands
- Phase 3: Offline capabilities
- Phase 4: Car-specific optimizations
- Phase 5: OEM integrations

## Support
For Android Auto specific issues, please contact support with:
- Car make/model and year
- Android Auto version
- TC RADIOS version
- Detailed description of the issue
- Steps to reproduce

---

*TC RADIOS - Bringing Christian radio to your car safely and conveniently!* 🚗🎵✝️
