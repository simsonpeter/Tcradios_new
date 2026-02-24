// Smartwatch App for TC RADIOS
class TCRadiosSmartwatch {
    constructor() {
        this.currentLanguage = 'tamil';
        this.stations = {};
        this.favorites = JSON.parse(localStorage.getItem('tcr-smartwatch-favorites') || '[]');
        this.currentStation = null;
        this.audio = null;
        this.isPlaying = false;
        this.volume = 50;
        this.currentTime = 0;
        this.currentScreen = 'nowPlaying';
        this.isVoiceActive = false;
        this.isAlwaysOn = false;
        this.isAmbient = false;
        
        this.init();
    }

    async init() {
        try {
            this.showLoadingScreen();
            await this.loadStations();
            this.setupEventListeners();
            this.setupSmartwatchIntegration();
            this.setupGestures();
            this.renderStations();
            this.renderFavorites();
            this.updateTime();
            this.hideLoadingScreen();
        } catch (error) {
            console.error('Initialization error:', error);
            this.showErrorScreen();
        }
    }

    async loadStations() {
        const langUrls = {
            tamil: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/stations.json',
            english: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/english.json',
            dutch: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/dutch.json',
            hindi: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/hindi.json',
            malayalam: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/malayalam.json',
            sinhala: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/sinhala.json',
            telugu: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/telugu.json'
        };

        const promises = Object.keys(langUrls).map(async (lang) => {
            try {
                const response = await fetch(langUrls[lang]);
                this.stations[lang] = await response.json();
            } catch (error) {
                console.error(`Failed to load ${lang} stations:`, error);
                this.stations[lang] = [];
            }
        });

        await Promise.allSettled(promises);
    }

    setupEventListeners() {
        // Navigation buttons
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                this.switchScreen(e.target.closest('.nav-btn').dataset.screen);
            });
        });

        // Language buttons
        document.querySelectorAll('.lang-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                this.selectLanguage(e.target.closest('.lang-btn').dataset.lang);
            });
        });

        // Control buttons
        document.getElementById('playPauseBtn').addEventListener('click', () => {
            this.togglePlayPause();
        });

        document.getElementById('nextBtn').addEventListener('click', () => {
            this.nextStation();
        });

        document.getElementById('prevBtn').addEventListener('click', () => {
            this.previousStation();
        });

        // Volume controls
        document.getElementById('volumeUp').addEventListener('click', () => {
            this.setVolume(Math.min(100, this.volume + 10));
        });

        document.getElementById('volumeDown').addEventListener('click', () => {
            this.setVolume(Math.max(0, this.volume - 10));
        });

        // Volume slider
        const volumeSlider = document.querySelector('.volume-slider');
        volumeSlider.addEventListener('click', (e) => {
            const rect = volumeSlider.getBoundingClientRect();
            const clickX = e.clientX - rect.left;
            const percentage = (clickX / rect.width) * 100;
            this.setVolume(Math.max(0, Math.min(100, percentage)));
        });

        // Back buttons
        document.getElementById('backToNowPlaying').addEventListener('click', () => {
            this.switchScreen('nowPlaying');
        });

        document.getElementById('backToNowPlayingFromFav').addEventListener('click', () => {
            this.switchScreen('nowPlaying');
        });

        document.getElementById('backToNowPlayingFromSettings').addEventListener('click', () => {
            this.switchScreen('nowPlaying');
        });

        // Settings toggles
        document.getElementById('bluetoothToggle').addEventListener('change', (e) => {
            this.toggleBluetooth(e.target.checked);
        });

        document.getElementById('voiceToggle').addEventListener('change', (e) => {
            this.toggleVoiceControl(e.target.checked);
        });

        document.getElementById('alwaysOnToggle').addEventListener('change', (e) => {
            this.toggleAlwaysOnDisplay(e.target.checked);
        });

        document.getElementById('ambientToggle').addEventListener('change', (e) => {
            this.toggleAmbientMode(e.target.checked);
        });

        // Retry button
        document.getElementById('retryBtn').addEventListener('click', () => {
            this.init();
        });
    }

    setupSmartwatchIntegration() {
        // Integrate with smartwatch platform
        if (window.SmartwatchAPI) {
            this.setupVoiceCommands();
            this.setupComplications();
        }

        // Set up smartwatch-specific features
        this.setupSmartwatchFeatures();
    }

    setupVoiceCommands() {
        // Voice command processing
        const voiceCommands = {
            'play christian music': () => this.selectLanguage('english'),
            'play tamil music': () => this.selectLanguage('tamil'),
            'play english music': () => this.selectLanguage('english'),
            'play malayalam music': () => this.selectLanguage('malayalam'),
            'play hindi music': () => this.selectLanguage('hindi'),
            'play dutch music': () => this.selectLanguage('dutch'),
            'play favorites': () => this.switchScreen('favorites'),
            'next station': () => this.nextStation(),
            'previous station': () => this.previousStation(),
            'volume up': () => this.setVolume(Math.min(100, this.volume + 10)),
            'volume down': () => this.setVolume(Math.max(0, this.volume - 10)),
            'stop music': () => this.stop(),
            'pause music': () => this.pause(),
            'resume music': () => this.resume()
        };

        // Process voice commands
        if (window.SmartwatchAPI) {
            SmartwatchAPI.voice.processCommand = (command) => {
                const cmd = command.toLowerCase();
                for (const [key, action] of Object.entries(voiceCommands)) {
                    if (cmd.includes(key)) {
                        action();
                        SmartwatchAPI.notifications.showSuccess(`Executed: ${key}`);
                        return true;
                    }
                }
                SmartwatchAPI.notifications.showWarning(`Unknown command: ${command}`);
                return false;
            };
        }
    }

    setupComplications() {
        // Update watch face complications
        if (window.SmartwatchAPI) {
            setInterval(() => {
                if (this.currentStation) {
                    SmartwatchAPI.complications.updateNowPlaying(
                        this.currentStation.name,
                        this.isPlaying
                    );
                }
                
                const batteryLevel = SmartwatchAPI.device.getBatteryLevel();
                SmartwatchAPI.complications.updateBattery(batteryLevel);
            }, 30000);
        }
    }

    setupSmartwatchFeatures() {
        // Adjust UI for smartwatch
        this.adjustForSmartwatchUI();
        
        // Set up battery optimization
        this.setupBatteryOptimization();
        
        // Initialize smartwatch audio
        this.setupSmartwatchAudio();
    }

    adjustForSmartwatchUI() {
        // Adjust UI for smartwatch screen
        document.body.classList.add('smartwatch-ui');
        
        // Set smartwatch-appropriate font sizes
        const style = document.createElement('style');
        style.textContent = `
            .smartwatch-ui {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            }
        `;
        document.head.appendChild(style);
    }

    setupBatteryOptimization() {
        // Optimize for battery life
        this.setupLowPowerMode();
        this.setupScreenTimeout();
    }

    setupLowPowerMode() {
        // Monitor battery level and adjust performance
        setInterval(() => {
            if (window.SmartwatchAPI) {
                const batteryLevel = SmartwatchAPI.device.getBatteryLevel();
                if (batteryLevel < 20) {
                    this.enableLowPowerMode();
                } else if (batteryLevel > 50) {
                    this.disableLowPowerMode();
                }
            }
        }, 60000);
    }

    enableLowPowerMode() {
        document.body.classList.add('low-power-mode');
        // Reduce animations and effects
    }

    disableLowPowerMode() {
        document.body.classList.remove('low-power-mode');
        // Restore full animations and effects
    }

    setupScreenTimeout() {
        // Auto-lock screen after inactivity
        let inactivityTimer;
        const resetTimer = () => {
            clearTimeout(inactivityTimer);
            inactivityTimer = setTimeout(() => {
                if (!this.isAlwaysOn) {
                    this.lockScreen();
                }
            }, 30000); // 30 seconds
        };

        document.addEventListener('touchstart', resetTimer);
        document.addEventListener('touchmove', resetTimer);
        resetTimer();
    }

    lockScreen() {
        // Lock the screen
        document.body.classList.add('locked');
    }

    unlockScreen() {
        // Unlock the screen
        document.body.classList.remove('locked');
    }

    setupSmartwatchAudio() {
        // Integrate with smartwatch audio system
        if (window.SmartwatchAPI) {
            // Set initial volume
            SmartwatchAPI.audio.setVolume(this.volume);
        }
    }

    setupGestures() {
        // Set up gesture recognition
        if (window.SmartwatchAPI && SmartwatchAPI.gestures.isSupported()) {
            this.setupGestureHandlers();
        }
    }

    setupGestureHandlers() {
        // Swipe gestures
        SmartwatchAPI.gestures.onSwipeUp(() => {
            this.nextScreen();
        });

        SmartwatchAPI.gestures.onSwipeDown(() => {
            this.previousScreen();
        });

        SmartwatchAPI.gestures.onSwipeLeft(() => {
            this.nextStation();
        });

        SmartwatchAPI.gestures.onSwipeRight(() => {
            this.previousStation();
        });

        // Tap gestures
        SmartwatchAPI.gestures.onDoubleTap(() => {
            this.toggleFavorite();
        });

        SmartwatchAPI.gestures.onLongPress(() => {
            this.showGestureHints();
        });
    }

    switchScreen(screenName) {
        // Hide all screens
        document.querySelectorAll('.watch-main > section').forEach(screen => {
            screen.classList.remove('active');
        });

        // Show selected screen
        const targetScreen = document.getElementById(screenName + 'Screen');
        if (targetScreen) {
            targetScreen.classList.add('active');
            this.currentScreen = screenName;
        }

        // Update navigation
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`[data-screen="${screenName}"]`).classList.add('active');

        // Vibrate on screen change
        if (window.SmartwatchAPI) {
            SmartwatchAPI.utils.vibrate([50]);
        }
    }

    nextScreen() {
        const screens = ['nowPlaying', 'language', 'stations', 'favorites', 'settings'];
        const currentIndex = screens.indexOf(this.currentScreen);
        const nextIndex = (currentIndex + 1) % screens.length;
        this.switchScreen(screens[nextIndex]);
    }

    previousScreen() {
        const screens = ['nowPlaying', 'language', 'stations', 'favorites', 'settings'];
        const currentIndex = screens.indexOf(this.currentScreen);
        const prevIndex = currentIndex <= 0 ? screens.length - 1 : currentIndex - 1;
        this.switchScreen(screens[prevIndex]);
    }

    selectLanguage(lang) {
        this.currentLanguage = lang;
        
        // Update active language button
        document.querySelectorAll('.lang-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`[data-lang="${lang}"]`).classList.add('active');
        
        this.renderStations();
        
        // Smartwatch notification
        if (window.SmartwatchAPI) {
            SmartwatchAPI.notifications.showSuccess(`Switched to ${lang} stations`);
        }
    }

    renderStations() {
        const list = document.getElementById('stationsList');
        const stations = this.stations[this.currentLanguage] || [];
        
        list.innerHTML = stations.map(station => `
            <div class="station-item" data-station='${JSON.stringify(station)}'>
                <img src="${station.logo}" alt="${station.name}" 
                     onerror="this.src='images/default-station.png'">
                <div class="info">
                    <div class="name">${station.name}</div>
                    <div class="genre">${station.genre}</div>
                </div>
                <button class="favorite-btn ${this.favorites.includes(station.name) ? 'active' : ''}" 
                        data-station="${station.name}">
                    ${this.favorites.includes(station.name) ? '♥' : '♡'}
                </button>
            </div>
        `).join('');

        // Add event listeners
        list.querySelectorAll('.station-item').forEach(item => {
            item.addEventListener('click', () => {
                const station = JSON.parse(item.dataset.station);
                this.playStation(station.name);
                this.switchScreen('nowPlaying');
            });
        });

        list.querySelectorAll('.favorite-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.toggleFavorite(btn.dataset.station);
            });
        });
    }

    renderFavorites() {
        const list = document.getElementById('favoritesList');
        const favoriteStations = this.getFavoriteStations();
        
        if (favoriteStations.length === 0) {
            list.innerHTML = '<p style="text-align: center; opacity: 0.7;">No favorites yet. Add some stations to your favorites!</p>';
            return;
        }

        list.innerHTML = favoriteStations.map(station => `
            <div class="station-item" data-station='${JSON.stringify(station)}'>
                <img src="${station.logo}" alt="${station.name}" 
                     onerror="this.src='images/default-station.png'">
                <div class="info">
                    <div class="name">${station.name}</div>
                    <div class="genre">${station.genre}</div>
                </div>
                <button class="favorite-btn active" data-station="${station.name}">♥</button>
            </div>
        `).join('');

        // Add event listeners
        list.querySelectorAll('.station-item').forEach(item => {
            item.addEventListener('click', () => {
                const station = JSON.parse(item.dataset.station);
                this.playStation(station.name);
                this.switchScreen('nowPlaying');
            });
        });

        list.querySelectorAll('.favorite-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.toggleFavorite(btn.dataset.station);
            });
        });
    }

    getFavoriteStations() {
        const allStations = Object.values(this.stations).flat();
        return allStations.filter(station => this.favorites.includes(station.name));
    }

    playStation(stationName) {
        const allStations = Object.values(this.stations).flat();
        const station = allStations.find(s => s.name === stationName);
        
        if (!station) return;

        this.currentStation = station;
        this.updateNowPlaying();
        
        if (this.audio) {
            this.audio.pause();
            this.audio = null;
        }

        this.audio = new Audio(station.url);
        this.audio.volume = this.volume / 100;
        
        this.audio.addEventListener('loadstart', () => {
            this.updatePlayButton('loading');
        });

        this.audio.addEventListener('canplay', () => {
            this.updatePlayButton('play');
        });

        this.audio.addEventListener('play', () => {
            this.isPlaying = true;
            this.updatePlayButton('pause');
            this.updateStationItems();
            this.startProgressUpdate();
        });

        this.audio.addEventListener('pause', () => {
            this.isPlaying = false;
            this.updatePlayButton('play');
            this.stopProgressUpdate();
        });

        this.audio.addEventListener('error', (e) => {
            console.error('Audio error:', e);
            this.updatePlayButton('error');
            if (window.SmartwatchAPI) {
                SmartwatchAPI.notifications.showError('Failed to play station');
            }
        });

        this.audio.play().catch(error => {
            console.error('Playback error:', error);
            this.updatePlayButton('error');
        });

        // Smartwatch notification
        if (window.SmartwatchAPI) {
            SmartwatchAPI.notifications.showSuccess(`Now playing: ${station.name}`);
        }
    }

    togglePlayPause() {
        if (!this.audio) return;

        if (this.isPlaying) {
            this.pause();
        } else {
            this.resume();
        }
    }

    pause() {
        if (this.audio) {
            this.audio.pause();
        }
    }

    resume() {
        if (this.audio) {
            this.audio.play().catch(error => {
                console.error('Resume error:', error);
            });
        }
    }

    stop() {
        if (this.audio) {
            this.audio.pause();
            this.audio.currentTime = 0;
            this.isPlaying = false;
            this.updatePlayButton('play');
            this.updateStationItems();
            this.stopProgressUpdate();
        }
    }

    nextStation() {
        const stations = this.stations[this.currentLanguage] || [];
        if (stations.length === 0) return;

        const currentIndex = stations.findIndex(s => s.name === this.currentStation?.name);
        const nextIndex = (currentIndex + 1) % stations.length;
        this.playStation(stations[nextIndex].name);
    }

    previousStation() {
        const stations = this.stations[this.currentLanguage] || [];
        if (stations.length === 0) return;

        const currentIndex = stations.findIndex(s => s.name === this.currentStation?.name);
        const prevIndex = currentIndex <= 0 ? stations.length - 1 : currentIndex - 1;
        this.playStation(stations[prevIndex].name);
    }

    toggleFavorite(stationName = null) {
        const station = stationName || (this.currentStation ? this.currentStation.name : null);
        if (!station) return;

        const index = this.favorites.indexOf(station);
        if (index > -1) {
            this.favorites.splice(index, 1);
        } else {
            this.favorites.push(station);
        }

        localStorage.setItem('tcr-smartwatch-favorites', JSON.stringify(this.favorites));
        this.renderStations();
        this.renderFavorites();
        this.updateFavoriteButton();
    }

    setVolume(volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        
        if (this.audio) {
            this.audio.volume = this.volume / 100;
        }

        // Update smartwatch audio system
        if (window.SmartwatchAPI) {
            SmartwatchAPI.audio.setVolume(this.volume);
        }

        document.getElementById('volumeFill').style.width = `${this.volume}%`;
    }

    updateNowPlaying() {
        if (!this.currentStation) return;

        document.getElementById('currentStationLogo').src = this.currentStation.logo;
        document.getElementById('currentStationName').textContent = this.currentStation.name;
        document.getElementById('currentStationGenre').textContent = this.currentStation.genre;
        document.getElementById('songTitle').textContent = 'Loading...';
        document.getElementById('artistName').textContent = 'Please wait';
    }

    updatePlayButton(state) {
        const btn = document.getElementById('playPauseBtn');
        const icon = btn.querySelector('.icon');
        
        switch(state) {
            case 'play':
                icon.textContent = '▶';
                btn.classList.remove('active');
                break;
            case 'pause':
                icon.textContent = '⏸';
                btn.classList.add('active');
                break;
            case 'loading':
                icon.textContent = '⟳';
                btn.classList.add('active');
                break;
            case 'error':
                icon.textContent = '⚠';
                btn.classList.remove('active');
                break;
        }
    }

    updateFavoriteButton() {
        // Update favorite button in now playing screen
        const btn = document.querySelector('.now-playing-screen .favorite-btn');
        if (btn) {
            const icon = btn.querySelector('.heart-icon');
            if (this.currentStation && this.favorites.includes(this.currentStation.name)) {
                icon.textContent = '♥';
                btn.classList.add('active');
            } else {
                icon.textContent = '♡';
                btn.classList.remove('active');
            }
        }
    }

    updateStationItems() {
        document.querySelectorAll('.station-item').forEach(item => {
            const station = JSON.parse(item.dataset.station);
            if (this.currentStation && station.name === this.currentStation.name && this.isPlaying) {
                item.classList.add('playing');
            } else {
                item.classList.remove('playing');
            }
        });
    }

    startProgressUpdate() {
        this.progressInterval = setInterval(() => {
            if (this.audio && this.isPlaying) {
                this.currentTime = this.audio.currentTime;
                this.updateProgress();
            }
        }, 1000);
    }

    stopProgressUpdate() {
        if (this.progressInterval) {
            clearInterval(this.progressInterval);
            this.progressInterval = null;
        }
    }

    updateProgress() {
        const progressFill = document.getElementById('progressFill');
        
        if (progressFill) {
            // For radio streams, we'll show elapsed time
            progressFill.style.width = '100%';
        }
    }

    toggleBluetooth(enabled) {
        if (window.SmartwatchAPI) {
            SmartwatchAPI.audio.setOutputDevice(enabled ? 'bluetooth' : 'speaker');
        }
    }

    toggleVoiceControl(enabled) {
        this.isVoiceActive = enabled;
        if (window.SmartwatchAPI) {
            if (enabled) {
                SmartwatchAPI.voice.startListening();
            } else {
                SmartwatchAPI.voice.stopListening();
            }
        }
    }

    toggleAlwaysOnDisplay(enabled) {
        this.isAlwaysOn = enabled;
        if (window.SmartwatchAPI) {
            if (enabled) {
                SmartwatchAPI.alwaysOn.enable();
            } else {
                SmartwatchAPI.alwaysOn.disable();
            }
        }
    }

    toggleAmbientMode(enabled) {
        this.isAmbient = enabled;
        if (window.SmartwatchAPI) {
            if (enabled) {
                SmartwatchAPI.ambient.enable();
            } else {
                SmartwatchAPI.ambient.disable();
            }
        }
    }

    showGestureHints() {
        const hints = document.getElementById('gestureHints');
        hints.classList.add('active');
        
        setTimeout(() => {
            hints.classList.remove('active');
        }, 3000);
    }

    updateTime() {
        setInterval(() => {
            const timeElement = document.getElementById('timeDisplay');
            if (timeElement) {
                timeElement.textContent = new Date().toLocaleTimeString();
            }
        }, 1000);
    }

    showLoadingScreen() {
        document.getElementById('loadingScreen').style.display = 'flex';
    }

    hideLoadingScreen() {
        document.getElementById('loadingScreen').style.display = 'none';
    }

    showErrorScreen() {
        document.getElementById('errorScreen').style.display = 'flex';
        document.getElementById('loadingScreen').style.display = 'none';
    }
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new TCRadiosSmartwatch();
});

// Smartwatch-specific initialization
if (typeof window.SmartwatchAPI !== 'undefined') {
    window.SmartwatchAPI.init();
}
