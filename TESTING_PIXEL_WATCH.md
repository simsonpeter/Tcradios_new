# Testing TC RADIOS on Pixel Watch & Mobile Phones

## Prerequisites

### For Pixel Watch:
- Pixel Watch connected to your phone
- Chrome browser on your computer
- USB debugging enabled (if using ADB)

### For Mobile Phones:
- Android or iOS device
- Modern browser (Chrome, Safari, Firefox, etc.)
- Internet connection

## Method 1: Direct Browser Access (Easiest)

### On Pixel Watch:
1. **On your Pixel Watch:**
   - Open the browser app (or install Chrome for Wear OS)
   - Navigate to: `https://tcradios-new.vercel.app`
   - The app will automatically detect the watch screen size and optimize

2. **Test Features:**
   - Double-tap: Play/Pause
   - Swipe left/right: Navigate stations
   - Long-press: Next station
   - Check if UI is properly sized
   - Test audio playback (requires Bluetooth headphones)

### On Mobile Phones:

#### Android Phones:
1. **Open Chrome or any modern browser**
2. **Navigate to:** `https://tcradios-new.vercel.app`
3. **Test Features:**
   - Tap to play/pause stations
   - Swipe left/right on player: Navigate stations
   - Test hamburger menu (☰) button for sleep timer, alarm, etc.
   - Test fullscreen player (tap on player bar)
   - Test volume control in fullscreen player
   - Test floating widget (if enabled)
   - Check responsive design in portrait and landscape
   - Test PWA installation prompt

#### iOS (iPhone/iPad):
1. **Open Safari browser**
2. **Navigate to:** `https://tcradios-new.vercel.app`
3. **Test Features:**
   - Tap to play/pause stations
   - Swipe gestures for navigation
   - Test hamburger menu button
   - Test fullscreen player
   - Test volume control
   - Test "Add to Home Screen" for PWA
   - Check safe area insets (notched devices)
   - Test in both portrait and landscape modes

## Method 2: Install as PWA

### On Pixel Watch:
1. **On Pixel Watch:**
   - Open the app in browser
   - Look for "Install" or "Add to Home Screen" option
   - Install as standalone app
   - App will appear in watch app drawer

### On Mobile Phones:

#### Android:
1. **Open the app in Chrome**
2. **Look for install banner** at the bottom or:
   - Tap the menu (☰) button
   - Select "Install App" option
3. **Install the PWA:**
   - Tap "Install" in the prompt
   - App will be added to home screen
   - Can be launched like a native app
4. **Benefits:**
   - Works offline (cached)
   - No browser UI
   - App-like experience
   - Appears in app drawer

#### iOS (iPhone/iPad):
1. **Open the app in Safari**
2. **Tap the Share button** (square with arrow)
3. **Select "Add to Home Screen"**
4. **Customize the name** (optional)
5. **Tap "Add"**
6. **App icon appears on home screen**
7. **Launch like a native app**

## Method 3: Remote Debugging (Advanced)

### Setup ADB Connection:

1. **Enable Developer Options on Watch:**
   - Go to Settings → System → About
   - Tap "Build number" 7 times
   - Go back to Settings → Developer options
   - Enable "ADB debugging"
   - Enable "Debug over WiFi" (optional)

2. **Connect via ADB:**
   ```bash
   # Connect watch via WiFi (get IP from watch settings)
   adb connect <WATCH_IP_ADDRESS>:5555
   
   # Or connect via USB (if using USB adapter)
   adb devices
   ```

3. **Open Chrome DevTools:**
   - Open Chrome on your computer
   - Go to: `chrome://inspect`
   - Find your watch in the list
   - Click "inspect"
   - You can now see/watch the app running on your watch!

## Method 4: Local Development Server

If testing locally:

1. **Start local server:**
   ```bash
   # Using Python
   python3 -m http.server 8000
   
   # Or using Node.js
   npx serve .
   ```

2. **Find your computer's IP:**
   ```bash
   # Linux/Mac
   ip addr show | grep inet
   
   # Or
   hostname -I
   ```

3. **On Pixel Watch:**
   - Open browser
   - Navigate to: `http://<YOUR_COMPUTER_IP>:8000`

## Testing Checklist

### Pixel Watch:
- [ ] App loads correctly on watch screen
- [ ] UI elements are properly sized (not too small/large)
- [ ] Touch targets are large enough (32px minimum)
- [ ] Gestures work (double-tap, swipe, long-press)
- [ ] Audio playback works (with Bluetooth headphones)
- [ ] Media controls appear in watch media player
- [ ] Battery usage is reasonable
- [ ] Round screen layout works (if applicable)
- [ ] Fullscreen player works
- [ ] Volume control works
- [ ] Station switching works smoothly

### Mobile Phones:
- [ ] App loads correctly on mobile browser
- [ ] Responsive design works in portrait mode
- [ ] Responsive design works in landscape mode
- [ ] Hamburger menu (☰) button is visible and works
- [ ] All menu items accessible (Sleep Timer, Alarm, Install)
- [ ] Fullscreen player opens when tapping player bar
- [ ] Fullscreen player close button is accessible (bottom-left)
- [ ] Volume control works in fullscreen player
- [ ] Floating widget works (if enabled)
- [ ] Touch targets are large enough (44px minimum)
- [ ] Swipe gestures work (swipe on player for next/prev)
- [ ] PWA installation works
- [ ] App works offline (after installation)
- [ ] Safe area insets respected (notched devices)
- [ ] Audio playback works
- [ ] Media notifications work (if enabled)
- [ ] Alarm clock feature works
- [ ] Sleep timer feature works
- [ ] Theme switching works
- [ ] Language switching works
- [ ] Search functionality works
- [ ] Favorites system works
- [ ] Drag & drop reordering works (favorites tab)

## Troubleshooting

### Pixel Watch Issues:

**App doesn't load:**
- Check internet connection on watch
- Verify URL is accessible
- Check browser console for errors (via remote debugging)

**Audio doesn't play:**
- Connect Bluetooth headphones to watch
- Check watch volume settings
- Verify audio permissions

**UI is too small/large:**
- Check if smartwatch CSS is being applied
- Verify viewport meta tag
- Test with different watch screen sizes

**Gestures don't work:**
- Check if `initWearOS()` is being called
- Verify touch event listeners
- Test with different gesture timings

### Mobile Phone Issues:

**Hamburger menu not visible:**
- Check screen width (should show on screens ≤600px)
- Verify menu button CSS is applied
- Check browser zoom level

**Buttons cut off on small screens:**
- Menu button should appear automatically
- Check responsive breakpoints
- Verify viewport meta tag

**Fullscreen player close button not accessible:**
- Button is now at bottom-left (not top-right)
- Check safe area insets
- Verify button is not hidden by system UI

**PWA installation not working:**
- Use HTTPS (required for PWA)
- Check if browser supports PWA
- Clear browser cache and try again
- On iOS, must use Safari (not Chrome)

**Audio issues:**
- Check device volume
- Verify browser audio permissions
- Try different browser
- Check if audio element is playing

**Touch targets too small:**
- All buttons should be minimum 44px
- Check if responsive CSS is applied
- Test on different screen sizes

**App not responsive:**
- Clear browser cache
- Check if CSS media queries are working
- Verify viewport meta tag: `<meta name="viewport" content="width=device-width, initial-scale=1">`

## Device Specifications

### Pixel Watch:
- Screen: 320x320 (round)
- OS: Wear OS 3+
- Browser: Chrome for Wear OS
- Audio: Bluetooth required

### Mobile Phones:
- **Android:** Any device with Chrome/Firefox/Edge
- **iOS:** iPhone/iPad with Safari (iOS 11.3+)
- **Screen sizes:** Optimized for all sizes (320px to 1920px+)
- **Orientations:** Portrait and Landscape supported
- **Audio:** Built-in speakers or headphones

## Quick Test URL

If your app is deployed:
- Production: `https://tcradios-new.vercel.app`
- Or your custom domain

If testing locally:
- `http://<YOUR_IP>:8000`
