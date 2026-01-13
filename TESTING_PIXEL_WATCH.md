# Testing TC RADIOS on Pixel Watch

## Prerequisites
- Pixel Watch connected to your phone
- Chrome browser on your computer
- USB debugging enabled (if using ADB)

## Method 1: Direct Browser Access (Easiest)

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

## Method 2: Install as PWA

1. **On Pixel Watch:**
   - Open the app in browser
   - Look for "Install" or "Add to Home Screen" option
   - Install as standalone app
   - App will appear in watch app drawer

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

## Troubleshooting

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

## Pixel Watch Specifications

- Screen: 320x320 (round)
- OS: Wear OS 3+
- Browser: Chrome for Wear OS
- Audio: Bluetooth required

## Quick Test URL

If your app is deployed:
- Production: `https://tcradios-new.vercel.app`
- Or your custom domain

If testing locally:
- `http://<YOUR_IP>:8000`
