# Samsung TV App Build Script

## Prerequisites

1. **Tizen Studio** - Download from [Samsung Developer](https://developer.samsung.com/tizen)
2. **Samsung TV SDK** - Install through Tizen Studio
3. **Certificate** - Create developer certificate for app signing

## Installation Steps

### 1. Install Tizen Studio
```bash
# Download Tizen Studio from Samsung Developer website
# Install with TV SDK components
```

### 2. Create Certificate
```bash
# Open Tizen Studio
# Go to Tools > Certificate Manager
# Create new certificate for TV development
```

### 3. Build the App
```bash
# Open Tizen Studio
# Import project: samsung-tv-app/
# Build and package the app
```

## Manual Build Process

### 1. Package the App
```bash
# In Tizen Studio:
# 1. Right-click project
# 2. Select "Build Project"
# 3. Select "Package Project"
```

### 2. Install on TV
```bash
# Connect Samsung TV via USB or network
# Install the .wgt package
# Launch the app
```

## Development Commands

### Test in Emulator
```bash
# Launch Tizen TV Emulator
# Install and test the app
```

### Debug Mode
```bash
# Enable remote debugging
# Connect browser dev tools
```

## File Structure
```
samsung-tv-app/
├── config.xml          # Tizen app configuration
├── index.html          # Main app interface
├── css/
│   └── tv-style.css    # TV-optimized styles
├── js/
│   ├── tv-app.js       # Main app logic
│   └── tizen.js        # Tizen API integration
└── images/
    ├── logo.png        # App logo
    └── default-station.png  # Default station image
```

## Features

### TV Remote Navigation
- **Arrow Keys**: Navigate through interface
- **OK Button**: Select items
- **Back Button**: Go back or exit
- **Color Buttons**:
  - **Red**: Play/Pause
  - **Green**: Stop
  - **Blue**: Toggle Favorite

### TV-Optimized Interface
- **Large Text**: Easy to read from distance
- **High Contrast**: Clear visibility on TV screen
- **Focus Indicators**: Clear focus states for navigation
- **Grid Layout**: Optimized for TV screen proportions

### Audio Features
- **Multi-language Support**: Tamil, English, Malayalam, Hindi, Dutch
- **Favorites System**: Save and organize favorite stations
- **Volume Control**: Integrated with TV volume
- **Now Playing**: Display current song information

## Troubleshooting

### Common Issues

1. **App won't install**
   - Check certificate validity
   - Ensure TV is in developer mode
   - Verify package format (.wgt)

2. **Audio not playing**
   - Check internet connection
   - Verify station URLs are accessible
   - Check TV audio settings

3. **Navigation issues**
   - Ensure focus is properly set
   - Check key event handling
   - Verify TV remote compatibility

### Debug Tips

1. **Enable Console Logging**
   ```javascript
   console.log('Debug info:', data);
   ```

2. **Check Network Status**
   ```javascript
   TizenAPI.network.isConnected();
   ```

3. **Monitor Audio Events**
   ```javascript
   audio.addEventListener('error', console.error);
   ```

## Deployment

### Production Build
1. **Optimize Assets**: Compress images and minify code
2. **Test Thoroughly**: Test on multiple TV models
3. **Create Certificate**: Use production certificate
4. **Package App**: Build final .wgt package
5. **Submit to Store**: Submit to Samsung TV App Store

### Distribution
- **Samsung TV App Store**: Official distribution
- **Developer Mode**: Direct installation for testing
- **USB Installation**: Install via USB drive

## Support

For issues and questions:
- **Email**: simsonpeter@gmail.com
- **Website**: https://tcradios-new.vercel.app
- **GitHub**: https://github.com/simsonpeter/Tcradios_new

## License

MIT License - See LICENSE file for details.

