#!/bin/bash

# Samsung TV App Package Script for TC RADIOS

echo "🎵 TC RADIOS - Samsung TV App Package Script"
echo "=============================================="

# Check if Tizen CLI is available
if ! command -v tizen &> /dev/null; then
    echo "❌ Tizen CLI not found. Please install Tizen Studio first."
    echo "   Download from: https://developer.samsung.com/tizen"
    exit 1
fi

echo "✅ Tizen CLI found"

# Create package directory
PACKAGE_DIR="tcradios-tv-app"
if [ -d "$PACKAGE_DIR" ]; then
    rm -rf "$PACKAGE_DIR"
fi
mkdir "$PACKAGE_DIR"

echo "📁 Creating package directory: $PACKAGE_DIR"

# Copy app files
cp -r css "$PACKAGE_DIR/"
cp -r js "$PACKAGE_DIR/"
cp -r images "$PACKAGE_DIR/"
cp config.xml "$PACKAGE_DIR/"
cp index.html "$PACKAGE_DIR/"
cp icon.png "$PACKAGE_DIR/"

echo "📋 App files copied"

# Create package info
cat > "$PACKAGE_DIR/package-info.txt" << EOF
TC RADIOS - Samsung TV App
==========================

Version: 1.0.0
Package: com.jayathasoft.tcradios.tv
Platform: Samsung Tizen TV

Features:
- Multi-language Christian radio stations
- TV remote navigation
- Favorites system
- Volume control
- Now playing information

Installation:
1. Install Tizen Studio
2. Create developer certificate
3. Import this project
4. Build and package
5. Install on Samsung TV

Contact: simsonpeter@gmail.com
Website: https://tcradios-new.vercel.app
EOF

echo "📄 Package info created"

# Create zip package
ZIP_NAME="tcradios-tv-app-$(date +%Y%m%d).zip"
zip -r "$ZIP_NAME" "$PACKAGE_DIR"

echo "📦 Package created: $ZIP_NAME"

# Display package contents
echo ""
echo "📋 Package Contents:"
echo "==================="
ls -la "$PACKAGE_DIR"

echo ""
echo "✅ Samsung TV App package ready!"
echo "📁 Package: $ZIP_NAME"
echo "📁 Directory: $PACKAGE_DIR"
echo ""
echo "Next Steps:"
echo "1. Install Tizen Studio"
echo "2. Import the project"
echo "3. Create certificate"
echo "4. Build and test"
echo ""
echo "For detailed instructions, see BUILD.md"

