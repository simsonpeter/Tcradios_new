#!/bin/bash

# Smartwatch App Package Script for TC RADIOS

echo "⌚ TC RADIOS - Smartwatch App Package Script"
echo "============================================="

# Create package directory
PACKAGE_DIR="tcradios-smartwatch-app"
if [ -d "$PACKAGE_DIR" ]; then
    rm -rf "$PACKAGE_DIR"
fi
mkdir "$PACKAGE_DIR"

echo "📁 Creating package directory: $PACKAGE_DIR"

# Copy app files
cp -r css "$PACKAGE_DIR/"
cp -r js "$PACKAGE_DIR/"
cp -r images "$PACKAGE_DIR/"
cp -r assets "$PACKAGE_DIR/"
cp -r platforms "$PACKAGE_DIR/"
cp manifest.json "$PACKAGE_DIR/"
cp index.html "$PACKAGE_DIR/"
cp icon.png "$PACKAGE_DIR/"
cp README.md "$PACKAGE_DIR/"

echo "📋 App files copied"

# Create package info
cat > "$PACKAGE_DIR/package-info.txt" << EOF
TC RADIOS - Smartwatch App
==========================

Version: 1.0.0
Platforms: Wear OS, watchOS, Tizen, Fitbit
Screen Sizes: 240x240, 320x320, 360x360, 400x400
Form Factors: Round, Square, Rectangular

Features:
- Multi-language Christian radio stations
- Voice control integration
- Gesture control system
- Multi-platform support
- Favorites system
- Complications support
- Always on display
- Ambient mode
- Battery optimization
- Bluetooth audio support

Supported Platforms:
- Wear OS (Google)
- watchOS (Apple)
- Tizen (Samsung)
- Fitbit

Installation:
1. Install platform-specific SDK
2. Import project files
3. Build and test
4. Submit to app store

Voice Commands:
- "Play Christian Music"
- "Play Tamil Music"
- "Play English Music"
- "Next Station"
- "Previous Station"
- "Volume Up/Down"
- "Stop Music"

Gesture Controls:
- Tap to select
- Double tap for favorites
- Long press for menu
- Swipe to navigate

Contact: simsonpeter@gmail.com
Website: https://tcradios-new.vercel.app
EOF

echo "📄 Package info created"

# Create zip package
ZIP_NAME="tcradios-smartwatch-app-$(date +%Y%m%d).zip"
zip -r "$ZIP_NAME" "$PACKAGE_DIR"

echo "📦 Package created: $ZIP_NAME"

# Display package contents
echo ""
echo "📋 Package Contents:"
echo "==================="
ls -la "$PACKAGE_DIR"

echo ""
echo "✅ Smartwatch App package ready!"
echo "📁 Package: $ZIP_NAME"
echo "📁 Directory: $PACKAGE_DIR"
echo ""
echo "Smartwatch App Features:"
echo "⌚ Multi-platform support (Wear OS, watchOS, Tizen, Fitbit)"
echo "🎵 Multi-language Christian radio stations"
echo "🎤 Voice control integration"
echo "👆 Gesture control system"
echo "❤️ Favorites system"
echo "📱 Complications support"
echo "🔋 Always on display"
echo "🌙 Ambient mode"
echo "⚡ Battery optimization"
echo "🔊 Bluetooth audio support"
echo ""
echo "Next Steps:"
echo "1. Install platform-specific SDK"
echo "2. Import the project files"
echo "3. Build and test on emulator"
echo "4. Test on actual smartwatch"
echo "5. Submit to app store"
echo ""
echo "For detailed instructions, see README.md"

