#!/bin/bash

# Separate smartwatch packages are intentionally disabled.
#
# TC RADIOS ships as one Bubblewrap radio APK/AAB. Wear OS support should come
# from the main Android package through responsive UI and Android Media Session
# controls, not from a separate Wear OS APK/package.

echo "Separate smartwatch package generation is disabled."
echo "Build the main TC RADIOS Bubblewrap APK/AAB from the twa/ project instead."
exit 0

