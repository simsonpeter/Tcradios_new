// Tizen API Integration for Samsung TV
(function() {
    'use strict';

    // Tizen Web API wrapper
    window.TizenAPI = {
        // Application lifecycle
        app: {
            exit: function() {
                if (typeof tizen !== 'undefined' && tizen.application) {
                    tizen.application.getCurrentApplication().exit();
                }
            },
            
            hide: function() {
                if (typeof tizen !== 'undefined' && tizen.application) {
                    tizen.application.getCurrentApplication().hide();
                }
            },
            
            getAppInfo: function() {
                if (typeof tizen !== 'undefined' && tizen.application) {
                    return tizen.application.getCurrentApplication().getRequestedAppControl();
                }
                return null;
            }
        },

        // System information
        system: {
            getPropertyValue: function(property) {
                if (typeof tizen !== 'undefined' && tizen.systeminfo) {
                    return tizen.systeminfo.getPropertyValue(property);
                }
                return null;
            },
            
            getCapabilities: function() {
                if (typeof tizen !== 'undefined' && tizen.systeminfo) {
                    return tizen.systeminfo.getCapabilities();
                }
                return null;
            }
        },

        // Network status
        network: {
            getNetworkInfo: function() {
                if (typeof tizen !== 'undefined' && tizen.network) {
                    return tizen.network.getNetworkInfo();
                }
                return null;
            },
            
            isConnected: function() {
                if (typeof tizen !== 'undefined' && tizen.network) {
                    const networkInfo = tizen.network.getNetworkInfo();
                    return networkInfo.networkType !== 'NONE';
                }
                return navigator.onLine;
            }
        },

        // TV-specific features
        tv: {
            // Get TV information
            getTVInfo: function() {
                if (typeof tizen !== 'undefined' && tizen.tv) {
                    return {
                        model: tizen.tv.getModel(),
                        version: tizen.tv.getVersion(),
                        resolution: tizen.tv.getResolution()
                    };
                }
                return null;
            },
            
            // Control TV volume
            setVolume: function(volume) {
                if (typeof tizen !== 'undefined' && tizen.tv) {
                    tizen.tv.setVolume(volume);
                }
            },
            
            getVolume: function() {
                if (typeof tizen !== 'undefined' && tizen.tv) {
                    return tizen.tv.getVolume();
                }
                return 50;
            },
            
            // Control TV power
            turnOff: function() {
                if (typeof tizen !== 'undefined' && tizen.tv) {
                    tizen.tv.turnOff();
                }
            }
        },

        // Input device management
        input: {
            // Register key event listener
            registerKey: function(keyName, callback) {
                if (typeof tizen !== 'undefined' && tizen.inputdevice) {
                    tizen.inputdevice.registerKey(keyName);
                    document.addEventListener('keydown', function(e) {
                        if (e.keyName === keyName) {
                            callback(e);
                        }
                    });
                }
            },
            
            // Unregister key event listener
            unregisterKey: function(keyName) {
                if (typeof tizen !== 'undefined' && tizen.inputdevice) {
                    tizen.inputdevice.unregisterKey(keyName);
                }
            }
        },

        // Power management
        power: {
            // Request CPU to stay awake
            request: function(resource) {
                if (typeof tizen !== 'undefined' && tizen.power) {
                    tizen.power.request(resource);
                }
            },
            
            // Release CPU wake lock
            release: function(resource) {
                if (typeof tizen !== 'undefined' && tizen.power) {
                    tizen.power.release(resource);
                }
            }
        },

        // Notification
        notification: {
            show: function(message, title) {
                if (typeof tizen !== 'undefined' && tizen.notification) {
                    const notification = new tizen.Notification({
                        message: message,
                        title: title || 'TC RADIOS'
                    });
                    tizen.notification.show(notification);
                } else {
                    // Fallback for web browsers
                    if ('Notification' in window && Notification.permission === 'granted') {
                        new Notification(title || 'TC RADIOS', {
                            body: message,
                            icon: 'images/logo.png'
                        });
                    }
                }
            }
        },

        // File system access
        filesystem: {
            resolve: function(path) {
                if (typeof tizen !== 'undefined' && tizen.filesystem) {
                    return tizen.filesystem.resolve(path);
                }
                return null;
            }
        },

        // Initialize Tizen-specific features
        init: function() {
            if (typeof tizen === 'undefined') {
                console.log('Tizen API not available - running in web mode');
                return false;
            }

            console.log('Tizen API initialized');
            
            // Request power management
            this.power.request('CPU');
            
            // Register TV remote keys
            this.input.registerKey('VolumeUp', function() {
                const currentVolume = TizenAPI.tv.getVolume();
                TizenAPI.tv.setVolume(Math.min(100, currentVolume + 5));
            });
            
            this.input.registerKey('VolumeDown', function() {
                const currentVolume = TizenAPI.tv.getVolume();
                TizenAPI.tv.setVolume(Math.max(0, currentVolume - 5));
            });
            
            this.input.registerKey('Mute', function() {
                TizenAPI.tv.setVolume(0);
            });
            
            // Handle app control events
            if (tizen.application) {
                tizen.application.getCurrentApplication().addEventListener('appcontrol', function(event) {
                    console.log('App control event:', event);
                });
            }
            
            return true;
        }
    };

    // Auto-initialize when Tizen is available
    if (typeof tizen !== 'undefined') {
        document.addEventListener('DOMContentLoaded', function() {
            TizenAPI.init();
        });
    }
})();
