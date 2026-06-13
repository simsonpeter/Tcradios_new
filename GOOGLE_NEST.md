# Google Nest Compatibility for TC RADIOS

## Overview

TC RADIOS supports Google Nest speakers, Nest Hub displays, Chromecast Audio, and Chromecast-enabled speakers through Google Cast sender integration in the web app.

## Features

- Cast live radio stations from Chrome/Android to Google Nest devices
- Transfer active playback to a Nest device without keeping duplicate local audio
- Play, pause, stop, and station navigation through the existing app controls
- Station name, genre, and artwork metadata on compatible Nest displays
- Media Session controls continue to work alongside Cast playback

## Usage

1. Open TC RADIOS in a Cast-capable browser such as Chrome.
2. Make sure the phone/computer and Google Nest device are on the same network.
3. Tap the Chromecast button in the header or menu.
4. Select a Google Nest speaker or display.
5. Choose any station, or continue the currently selected station on the Nest device.

## Technical Implementation

The main web app (`index.html`) loads the Google Cast sender SDK and uses the Default Media Receiver (`CC1AD845`) for live audio streams. When a Cast session is connected, station playback is loaded as a live Cast media item and local audio is paused to prevent echo.

## Compatibility Notes

- Requires a browser/device that supports Google Cast sender APIs.
- Some radio streams may fail on Nest if the upstream station blocks Cast receiver access or uses an unsupported audio format.
- Google Nest devices must be reachable on the same local network as the sender device.
