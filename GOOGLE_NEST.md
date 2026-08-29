# Google Nest Compatibility for TC RADIOS

## Overview

TC RADIOS supports Google Nest speakers, Nest Hub displays, Chromecast Audio, and Chromecast-enabled speakers through Google Cast sender integration in the web app.

## Features

- Cast live radio stations from Chrome/Android to Google Nest devices
- Transfer active playback to a Nest device without keeping duplicate local audio
- Play, pause, stop, and station navigation through the existing app controls
- Station name, genre, and artwork metadata on compatible Nest displays
- Media Session controls continue to work alongside Cast playback
- Deep links for Assistant-style routines: `?action=playlang&lang=tamil` and `?action=playfav`

## Usage

1. Open TC RADIOS in a Cast-capable browser such as Chrome.
2. Make sure the phone/computer and Google Nest device are on the same network.
3. Tap the Chromecast button in the header or menu.
4. Select a Google Nest speaker or display.
5. Choose any station, or continue the currently selected station on the Nest device.

## Nest routine: “Hey Google, play TC RADIOS Tamil”

Google Nest speakers do **not** automatically discover every Android media app. Use one of these working setups:

### Option A — Phone Assistant + Android Auto media service (recommended with the APK)

1. Install the latest TC RADIOS Android APK (v31+).
2. Open the app once while logged in so favorites/recents sync to native storage.
3. On a phone with Google Assistant:  
   **“Hey Google, play Tamil radio on TC RADIOS”**  
   (Also try: “Play TC RADIOS”, “Play my favorite on TC RADIOS”.)
4. The native `MediaBrowserService` resolves Tamil / favorites via `onPlayFromSearch`.

### Option B — Custom Nest routine that opens the deep link

1. Google Home app → **Automations** → **New routine**.
2. Starter: voice phrase **“Play TC RADIOS Tamil”**.
3. Action → **Communicate & announce** is not enough for radio; instead add:
   - **Try to add a custom action / open link** to  
     `https://tcradios-new.vercel.app/?action=playlang&lang=tamil`  
     on a phone/tablet that has the app, **or**
   - Cast manually after the phone starts Tamil, then tap Cast to Nest.

### Option C — Routine that plays a known Tamil stream URL

1. Create a routine with your phrase.
2. Action: **Play music / media** and paste a Tamil station stream URL from TC RADIOS  
   (open a station in the app → share / copy stream if available), **or** cast from the phone after launching the deep link above.

### Option D — Cast from the phone after a launcher shortcut

Long-press the TC RADIOS icon → **Play Tamil** → then tap **Cast to Google Nest**.

## Technical Implementation

The main web app (`index.html`) loads the Google Cast sender SDK and uses the Default Media Receiver (`CC1AD845`) for live audio streams. When a Cast session is connected, station playback is loaded as a live Cast media item and local audio is paused to prevent echo.

Android App Actions metadata lives in `twa/app/src/main/res/xml/actions.xml` and points Assistant toward the Tamil / favorite deep links.

## Compatibility Notes

- Requires a browser/device that supports Google Cast sender APIs.
- Some radio streams may fail on Nest if the upstream station blocks Cast receiver access or uses an unsupported audio format.
- Google Nest devices must be reachable on the same local network as the sender device.
- Full “play on Nest with no phone” partner-media registration is not included; use Cast or a stream URL routine for speaker-only playback.
