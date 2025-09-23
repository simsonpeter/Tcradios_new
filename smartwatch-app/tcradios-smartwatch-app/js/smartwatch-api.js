// Smartwatch API Integration for TC RADIOS
(function() {
    'use strict';

    // Smartwatch Web API wrapper
    window.SmartwatchAPI = {
        // Platform detection
        platform: {
            getPlatform: function() {
                const userAgent = navigator.userAgent.toLowerCase();
                if (userAgent.includes('wearos')) return 'wearos';
                if (userAgent.includes('watchos')) return 'watchos';
                if (userAgent.includes('tizen')) return 'tizen';
                if (userAgent.includes('fitbit')) return 'fitbit';
                return 'unknown';
            },
            
            getScreenSize: function() {
                return {
                    width: window.innerWidth,
                    height: window.innerHeight,
                    ratio: window.devicePixelRatio || 1
                };
            },
            
            getFormFactor: function() {
                const size = this.getScreenSize();
                if (size.width === size.height) return 'round';
                if (size.width > size.height) return 'rectangular';
                return 'square';
            }
        },

        // Device information
        device: {
            getInfo: function() {
                return {
                    platform: this.platform.getPlatform(),
                    screenSize: this.platform.getScreenSize(),
                    formFactor: this.platform.getFormFactor(),
                    battery: this.getBatteryLevel(),
                    connection: this.getConnectionStatus()
                };
            },
            
            getBatteryLevel: function() {
                // Simulate battery level
                return Math.floor(Math.random() * 40) + 60; // 60-100%
            },
            
            getConnectionStatus: function() {
                return {
                    wifi: navigator.onLine,
                    bluetooth: this.isBluetoothConnected(),
                    cellular: this.isCellularConnected()
                };
            },
            
            isBluetoothConnected: function() {
                // Simulate Bluetooth connection
                return true;
            },
            
            isCellularConnected: function() {
                // Simulate cellular connection
                return false;
            }
        },

        // Audio system
        audio: {
            getVolume: function() {
                return 50;
            },
            
            setVolume: function(volume) {
                console.log('Setting volume to:', volume);
                // Integrate with smartwatch audio system
            },
            
            getOutputDevice: function() {
                return 'speaker'; // speaker, bluetooth, wired
            },
            
            setOutputDevice: function(device) {
                console.log('Setting output device to:', device);
            },
            
            isMuted: function() {
                return false;
            },
            
            mute: function() {
                console.log('Muting audio');
            },
            
            unmute: function() {
                console.log('Unmuting audio');
            }
        },

        // Voice commands
        voice: {
            isListening: false,
            isSupported: function() {
                return 'webkitSpeechRecognition' in window || 'SpeechRecognition' in window;
            },
            
            startListening: function() {
                this.isListening = true;
                console.log('Voice listening started');
                // Integrate with smartwatch voice system
            },
            
            stopListening: function() {
                this.isListening = false;
                console.log('Voice listening stopped');
            },
            
            processCommand: function(command) {
                console.log('Processing voice command:', command);
                return this.executeCommand(command);
            },
            
            executeCommand: function(command) {
                const cmd = command.toLowerCase();
                
                if (cmd.includes('play') && cmd.includes('christian')) {
                    return 'playChristianMusic';
                } else if (cmd.includes('play') && cmd.includes('tamil')) {
                    return 'playTamilMusic';
                } else if (cmd.includes('play') && cmd.includes('english')) {
                    return 'playEnglishMusic';
                } else if (cmd.includes('play') && cmd.includes('favorites')) {
                    return 'playFavorites';
                } else if (cmd.includes('next')) {
                    return 'nextStation';
                } else if (cmd.includes('previous')) {
                    return 'previousStation';
                } else if (cmd.includes('volume up')) {
                    return 'volumeUp';
                } else if (cmd.includes('volume down')) {
                    return 'volumeDown';
                } else if (cmd.includes('stop')) {
                    return 'stopMusic';
                } else if (cmd.includes('pause')) {
                    return 'pauseMusic';
                } else if (cmd.includes('resume')) {
                    return 'resumeMusic';
                }
                
                return 'unknown';
            }
        },

        // Gesture recognition
        gestures: {
            isSupported: function() {
                return 'ontouchstart' in window;
            },
            
            onSwipeUp: function(callback) {
                this.addSwipeListener('up', callback);
            },
            
            onSwipeDown: function(callback) {
                this.addSwipeListener('down', callback);
            },
            
            onSwipeLeft: function(callback) {
                this.addSwipeListener('left', callback);
            },
            
            onSwipeRight: function(callback) {
                this.addSwipeListener('right', callback);
            },
            
            onTap: function(callback) {
                this.addTapListener(callback);
            },
            
            onDoubleTap: function(callback) {
                this.addDoubleTapListener(callback);
            },
            
            onLongPress: function(callback) {
                this.addLongPressListener(callback);
            },
            
            addSwipeListener: function(direction, callback) {
                let startX, startY, endX, endY;
                const minSwipeDistance = 50;
                
                document.addEventListener('touchstart', function(e) {
                    startX = e.touches[0].clientX;
                    startY = e.touches[0].clientY;
                });
                
                document.addEventListener('touchend', function(e) {
                    endX = e.changedTouches[0].clientX;
                    endY = e.changedTouches[0].clientY;
                    
                    const deltaX = endX - startX;
                    const deltaY = endY - startY;
                    
                    if (Math.abs(deltaX) > minSwipeDistance || Math.abs(deltaY) > minSwipeDistance) {
                        if (direction === 'up' && deltaY < -minSwipeDistance) callback();
                        if (direction === 'down' && deltaY > minSwipeDistance) callback();
                        if (direction === 'left' && deltaX < -minSwipeDistance) callback();
                        if (direction === 'right' && deltaX > minSwipeDistance) callback();
                    }
                });
            },
            
            addTapListener: function(callback) {
                let tapCount = 0;
                let tapTimer;
                
                document.addEventListener('touchstart', function(e) {
                    tapCount++;
                    if (tapCount === 1) {
                        tapTimer = setTimeout(function() {
                            callback(e);
                            tapCount = 0;
                        }, 300);
                    } else if (tapCount === 2) {
                        clearTimeout(tapTimer);
                        tapCount = 0;
                    }
                });
            },
            
            addDoubleTapListener: function(callback) {
                let tapCount = 0;
                let tapTimer;
                
                document.addEventListener('touchstart', function(e) {
                    tapCount++;
                    if (tapCount === 1) {
                        tapTimer = setTimeout(function() {
                            tapCount = 0;
                        }, 300);
                    } else if (tapCount === 2) {
                        clearTimeout(tapTimer);
                        callback(e);
                        tapCount = 0;
                    }
                });
            },
            
            addLongPressListener: function(callback) {
                let pressTimer;
                
                document.addEventListener('touchstart', function(e) {
                    pressTimer = setTimeout(function() {
                        callback(e);
                    }, 500);
                });
                
                document.addEventListener('touchend', function() {
                    clearTimeout(pressTimer);
                });
            }
        },

        // Notifications
        notifications: {
            show: function(message, type = 'info') {
                console.log(`Notification (${type}):`, message);
                // Integrate with smartwatch notification system
            },
            
            showError: function(message) {
                this.show(message, 'error');
            },
            
            showSuccess: function(message) {
                this.show(message, 'success');
            },
            
            showWarning: function(message) {
                this.show(message, 'warning');
            }
        },

        // System information
        system: {
            getTime: function() {
                return new Date().toLocaleTimeString();
            },
            
            getDate: function() {
                return new Date().toLocaleDateString();
            },
            
            getNetworkStatus: function() {
                return navigator.onLine ? 'connected' : 'disconnected';
            },
            
            getSignalStrength: function() {
                return 'strong'; // Simulate strong signal
            }
        },

        // Always On Display
        alwaysOn: {
            isSupported: function() {
                return true;
            },
            
            isEnabled: function() {
                return localStorage.getItem('alwaysOnDisplay') === 'true';
            },
            
            enable: function() {
                localStorage.setItem('alwaysOnDisplay', 'true');
                document.body.classList.add('always-on');
            },
            
            disable: function() {
                localStorage.setItem('alwaysOnDisplay', 'false');
                document.body.classList.remove('always-on');
            }
        },

        // Ambient Mode
        ambient: {
            isSupported: function() {
                return true;
            },
            
            isEnabled: function() {
                return localStorage.getItem('ambientMode') === 'true';
            },
            
            enable: function() {
                localStorage.setItem('ambientMode', 'true');
                document.body.classList.add('ambient-mode');
            },
            
            disable: function() {
                localStorage.setItem('ambientMode', 'false');
                document.body.classList.remove('ambient-mode');
            }
        },

        // Complications (Watch Face Widgets)
        complications: {
            isSupported: function() {
                return true;
            },
            
            updateNowPlaying: function(stationName, isPlaying) {
                console.log('Updating complication:', stationName, isPlaying);
                // Update watch face complication
            },
            
            updateBattery: function(level) {
                console.log('Updating battery complication:', level);
                // Update battery level on watch face
            }
        },

        // Initialize smartwatch-specific features
        init: function() {
            console.log('Smartwatch API initialized');
            
            // Set up periodic updates
            this.startPeriodicUpdates();
            
            // Initialize gesture recognition
            this.initGestures();
            
            // Initialize voice commands
            this.initVoiceCommands();
            
            // Set up always on display
            this.initAlwaysOnDisplay();
            
            // Set up ambient mode
            this.initAmbientMode();
            
            return true;
        },

        startPeriodicUpdates: function() {
            // Update time every second
            setInterval(() => {
                const timeElement = document.getElementById('timeDisplay');
                if (timeElement) {
                    timeElement.textContent = this.system.getTime();
                }
            }, 1000);

            // Update battery level every 30 seconds
            setInterval(() => {
                const batteryElement = document.getElementById('batteryIndicator');
                if (batteryElement) {
                    const level = this.device.getBatteryLevel();
                    batteryElement.querySelector('.battery-level').textContent = `${level}%`;
                }
            }, 30000);
        },

        initGestures: function() {
            if (this.gestures.isSupported()) {
                // Set up gesture listeners
                this.gestures.onSwipeUp(() => {
                    console.log('Swipe up detected');
                    // Navigate to next screen
                });
                
                this.gestures.onSwipeDown(() => {
                    console.log('Swipe down detected');
                    // Navigate to previous screen
                });
                
                this.gestures.onSwipeLeft(() => {
                    console.log('Swipe left detected');
                    // Next station
                });
                
                this.gestures.onSwipeRight(() => {
                    console.log('Swipe right detected');
                    // Previous station
                });
                
                this.gestures.onDoubleTap(() => {
                    console.log('Double tap detected');
                    // Toggle favorite
                });
                
                this.gestures.onLongPress(() => {
                    console.log('Long press detected');
                    // Show menu
                });
            }
        },

        initVoiceCommands: function() {
            if (this.voice.isSupported()) {
                console.log('Voice commands supported');
            } else {
                console.log('Voice commands not supported');
            }
        },

        initAlwaysOnDisplay: function() {
            if (this.alwaysOn.isSupported() && this.alwaysOn.isEnabled()) {
                this.alwaysOn.enable();
            }
        },

        initAmbientMode: function() {
            if (this.ambient.isSupported() && this.ambient.isEnabled()) {
                this.ambient.enable();
            }
        },

        // Utility functions
        utils: {
            formatTime: function(seconds) {
                const mins = Math.floor(seconds / 60);
                const secs = Math.floor(seconds % 60);
                return `${mins}:${secs.toString().padStart(2, '0')}`;
            },
            
            formatBattery: function(percentage) {
                return `${percentage}%`;
            },
            
            vibrate: function(pattern) {
                if ('vibrate' in navigator) {
                    navigator.vibrate(pattern);
                }
            }
        }
    };

    // Auto-initialize when DOM is loaded
    document.addEventListener('DOMContentLoaded', function() {
        SmartwatchAPI.init();
    });

    // Export for global use
    window.SmartwatchAPI = SmartwatchAPI;
})();
