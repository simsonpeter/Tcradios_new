# 🎵 TC RADIOS

**Uplifting Christian Music & Messages Worldwide**

[![Version](https://img.shields.io/badge/version-24.0-blue.svg)](https://github.com/simsonpeter/Tcradios_new)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android%20%7C%20Web%20%7C%20Google%20Nest-blue.svg)](https://tcradios-new.vercel.app)
[![PWA](https://img.shields.io/badge/PWA-enabled-orange.svg)](https://tcradios-new.vercel.app)

A modern Progressive Web App (PWA) and Android application that brings uplifting Christian music and inspirational messages to believers worldwide. Built with cutting-edge web technologies and optimized for mobile devices.

## 🌟 Features

### 🎵 **Multi-Language Support**
- **Tamil** - Traditional Tamil Christian music
- **English** - Contemporary Christian music
- **Malayalam** - Malayalam Christian songs
- **Hindi** - Hindi Christian music
- **Dutch** - Dutch Christian radio stations

### 📱 **Cross-Platform**
- **Progressive Web App (PWA)** - Install on any device
- **Android App** - Native Android application
- **Google Nest / Chromecast** - Cast live stations to Nest speakers and smart displays
- **Responsive Design** - Works on all screen sizes
- **Offline Support** - Cached content for offline listening

### ❤️ **Smart Features**
- **Favorites System** - Save and organize favorite stations
- **Drag & Drop Reordering** - Customize favorites order (mobile & desktop)
- **Search Functionality** - Find stations quickly
- **Sleep Timer** - Auto-stop after set time
- **Dark/Light Themes** - Choose your preferred theme
- **Haptic Feedback** - Touch feedback on mobile devices

### 🔧 **Technical Features**
- **Cache Management** - Automatic cache clearing on startup
- **Metadata Display** - Shows current song and artist
- **Media Session** - Android Auto and Wear OS support
- **Google Cast** - Remote playback controls and station metadata for Google Nest devices
- **Notifications** - Rich media notifications
- **Swipe Gestures** - Navigate with touch gestures

## 🚀 Quick Start

### Web App
1. Visit [https://tcradios-new.vercel.app](https://tcradios-new.vercel.app)
2. Click "Install App" when prompted
3. Enjoy Christian music on any device!

### Android App
1. Download the APK from releases
2. Install on your Android device
3. Launch and start listening!

## 📱 Installation

### Progressive Web App (PWA)
```bash
# Visit the website
https://tcradios-new.vercel.app

# Click "Install App" in your browser
# Or use browser menu: "Add to Home Screen"
```

### Android APK
```bash
# Download the latest APK
wget https://github.com/simsonpeter/Tcradios_new/releases/latest/download/app-release-signed.apk

# Install on Android device
adb install app-release-signed.apk
```

## 🛠️ Development

### Prerequisites
- Node.js 16+ (for development)
- Android Studio (for Android development)
- Bubblewrap CLI (for TWA builds)

### Local Development
```bash
# Clone the repository
git clone https://github.com/simsonpeter/Tcradios_new.git
cd Tcradios_new

# Serve locally (any HTTP server)
python -m http.server 8000
# or
npx serve .

# Visit http://localhost:8000
```

### Building Android APK
```bash
# Install Bubblewrap CLI
npm install -g @bubblewrap/cli

# Navigate to TWA directory
cd twa

# Build APK
bubblewrap build
```

## 📁 Project Structure

```
Tcradios_new/
├── index.html              # Main PWA application
├── manifest.json           # PWA manifest
├── sw.js                   # Service worker
├── icons/                  # App icons
│   ├── icon-192x192.png
│   ├── icon-512x512.png
│   └── favicon.ico
├── twa/                    # Trusted Web Activity
│   ├── twa-manifest.json   # TWA configuration
│   ├── app-release-signed.apk
│   └── app-release-bundle.aab
├── .well-known/
│   └── assetlinks.json     # Digital Asset Links
└── README.md              # This file
```

## 🎨 Design Features

### Modern UI/UX
- **Clean Interface** - Minimalist design focused on content
- **Smooth Animations** - Fluid transitions and interactions
- **Touch Optimized** - Designed for mobile-first experience
- **Accessibility** - Screen reader and keyboard navigation support

### Professional About Section
- **Hero Section** - Prominent branding and tagline
- **Feature Cards** - Comprehensive feature showcase
- **Statistics** - Impact metrics and achievements
- **Contact Information** - Professional contact details
- **Technology Stack** - Technical capabilities overview

## 🔧 Configuration

### Language URLs
The app supports multiple languages through configurable JSON endpoints:

```javascript
const LANG_URLS = {
  tamil: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/stations.json',
  english: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/english.json',
  dutch: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/dutch.json',
  hindi: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/hindi.json',
  malayalam: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/malayalam.json',
  sinhala: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/sinhala.json',
  telugu: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/telugu.json'
};
```

### TWA Configuration
Android app settings in `twa/twa-manifest.json`:

```json
{
  "packageId": "com.jayathasoft.tcradios.app",
  "host": "tcradios-new.vercel.app",
  "backgroundColor": "#FFFFFF",
  "themeColor": "#F97316",
  "appVersionName": "24",
  "appVersionCode": 24
}
```

## 📊 Statistics

- **50+** Radio Stations
- **7** Languages Supported
- **24/7** Availability
- **100%** Free to Use
- **Multi-Platform** Support

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Reporting Issues
- Use GitHub Issues to report bugs
- Include device/browser information
- Provide steps to reproduce

### Feature Requests
- Submit feature requests via GitHub Issues
- Describe the use case and benefits
- Consider implementation complexity

### Code Contributions
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📞 Contact

**JayathaSoft**
- **Email**: [simsonpeter@gmail.com](mailto:simsonpeter@gmail.com)
- **Location**: Antwerp, Belgium
- **Website**: [tcradios-new.vercel.app](https://tcradios-new.vercel.app)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Christian Radio Stations** - For providing quality content
- **Open Source Community** - For amazing tools and libraries
- **Beta Testers** - For valuable feedback and testing
- **Contributors** - For helping improve the app

## 🔄 Changelog

### Version 24.0
- ✅ White splash screen background
- ✅ Professional About Us section redesign
- ✅ Mobile drag-and-drop reordering fixes
- ✅ Cache clearing on app startup
- ✅ Enhanced touch event handling
- ✅ Improved visual feedback

### Version 23.0
- ✅ Multi-language support (Tamil, English, Malayalam, Hindi, Dutch)
- ✅ Drag-and-drop favorites reordering
- ✅ Cache management system
- ✅ Enhanced mobile experience

### Version 22.0
- ✅ Progressive Web App (PWA) support
- ✅ Android app with TWA
- ✅ Dark/Light theme support
- ✅ Sleep timer functionality

## 🌐 Links

- **Live App**: [https://tcradios-new.vercel.app](https://tcradios-new.vercel.app)
- **GitHub Repository**: [https://github.com/simsonpeter/Tcradios_new](https://github.com/simsonpeter/Tcradios_new)
- **Issues**: [https://github.com/simsonpeter/Tcradios_new/issues](https://github.com/simsonpeter/Tcradios_new/issues)

---

**Made with ❤️ for the Christian community worldwide.**

*"Let everything that has breath praise the Lord. Praise the Lord!" - Psalm 150:6*
