#!/bin/bash

# Tesla App Package Script for TC RADIOS

echo "🚗 TC RADIOS - Tesla App Package Script"
echo "========================================"

# Create package directory
PACKAGE_DIR="tcradios-tesla-app"
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
cp manifest.json "$PACKAGE_DIR/"
cp index.html "$PACKAGE_DIR/"
cp icon.png "$PACKAGE_DIR/"
cp README.md "$PACKAGE_DIR/"

echo "📋 App files copied"

# Create package info
cat > "$PACKAGE_DIR/package-info.txt" << EOF
TC RADIOS - Tesla App
====================

Version: 1.0.0
Platform: Tesla Infotainment System
Compatibility: Tesla Model 3, Y, S, X (2023.44+)

Features:
- Multi-language Christian radio stations
- Voice control integration
- Tesla audio system integration
- Driving mode optimization
- Favorites system
- Safety features

Installation:
1. Enable Developer Mode on Tesla
2. Install via USB or network
3. Launch from Tesla app menu

Voice Commands:
- "Play Christian Music"
- "Play Tamil Music"
- "Play English Music"
- "Next Station"
- "Previous Station"
- "Volume Up/Down"
- "Stop Music"

Contact: simsonpeter@gmail.com
Website: https://tcradios-new.vercel.app
EOF

echo "📄 Package info created"

# Create zip package
ZIP_NAME="tcradios-tesla-app-$(date +%Y%m%d).zip"
zip -r "$ZIP_NAME" "$PACKAGE_DIR"

echo "📦 Package created: $ZIP_NAME"

# Display package contents
echo ""
echo "📋 Package Contents:"
echo "==================="
ls -la "$PACKAGE_DIR"

echo ""
echo "✅ Tesla App package ready!"
echo "📁 Package: $ZIP_NAME"
echo "📁 Directory: $PACKAGE_DIR"
echo ""
echo "Tesla App Features:"
echo "🎵 Multi-language Christian radio stations"
echo "🎤 Voice control integration"
echo "🚗 Driving mode optimization"
echo "🔊 Tesla audio system integration"
echo "❤️ Favorites system"
echo "🛡️ Safety features"
echo ""
echo "Next Steps:"
echo "1. Enable Developer Mode on Tesla"
echo "2. Install the app package"
echo "3. Test voice commands"
echo "4. Submit to Tesla App Store"
echo ""
echo "For detailed instructions, see README.md"

