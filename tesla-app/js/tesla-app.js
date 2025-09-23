// Tesla App for TC RADIOS
class TCRadiosTesla {
    constructor() {
        this.currentLanguage = 'tamil';
        this.stations = {};
        this.favorites = JSON.parse(localStorage.getItem('tcr-tesla-favorites') || '[]');
        this.currentStation = null;
        this.audio = null;
        this.isPlaying = false;
        this.volume = 50;
        this.currentTime = 0;
        this.isVoiceActive = false;
        
        this.init();
    }

    async init() {
        try {
            this.showLoadingScreen();
            await this.loadStations();
            this.setupEventListeners();
            this.setupTeslaIntegration();
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
            malayalam: 'https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main/languages/malayalam.json'
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

        document.getElementById('stopBtn').addEventListener('click', () => {
            this.stop();
        });

        document.getElementById('nextBtn').addEventListener('click', () => {
            this.nextStation();
        });

        document.getElementById('prevBtn').addEventListener('click', () => {
            this.previousStation();
        });

        // Volume controls
        document.getElementById('volumeUp').addEventListener('click', () => {
            this.setVolume(Math.min(100, this.volume + 5));
        });

        document.getElementById('volumeDown').addEventListener('click', () => {
            this.setVolume(Math.max(0, this.volume - 5));
        });

        // Volume slider
        const volumeTrack = document.querySelector('.volume-track');
        volumeTrack.addEventListener('click', (e) => {
            const rect = volumeTrack.getBoundingClientRect();
            const clickX = e.clientX - rect.left;
            const percentage = (clickX / rect.width) * 100;
            this.setVolume(Math.max(0, Math.min(100, percentage)));
        });

        // Search functionality
        document.getElementById('stationSearch').addEventListener('input', (e) => {
            this.filterStations(e.target.value);
        });

        // Favorites panel
        document.getElementById('closeFavoritesBtn').addEventListener('click', () => {
            this.hideFavoritesPanel();
        });

        // Retry button
        document.getElementById('retryBtn').addEventListener('click', () => {
            this.init();
        });

        // Voice control activation
        document.addEventListener('keydown', (e) => {
            if (e.key === 'v' || e.key === 'V') {
                this.toggleVoiceControl();
            }
        });
    }

    setupTeslaIntegration() {
        // Integrate with Tesla's voice system
        if (window.TeslaAPI) {
            TeslaAPI.voice.startListening();
            this.setupVoiceCommands();
        }

        // Set up Tesla-specific features
        this.setupTeslaFeatures();
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
            'play favorites': () => this.showFavoritesPanel(),
            'next station': () => this.nextStation(),
            'previous station': () => this.previousStation(),
            'volume up': () => this.setVolume(Math.min(100, this.volume + 10)),
            'volume down': () => this.setVolume(Math.max(0, this.volume - 10)),
            'stop music': () => this.stop(),
            'pause music': () => this.pause(),
            'resume music': () => this.resume()
        };

        // Process voice commands
        if (window.TeslaAPI) {
            TeslaAPI.voice.processCommand = (command) => {
                const cmd = command.toLowerCase();
                for (const [key, action] of Object.entries(voiceCommands)) {
                    if (cmd.includes(key)) {
                        action();
                        TeslaAPI.notifications.showSuccess(`Executed: ${key}`);
                        return true;
                    }
                }
                TeslaAPI.notifications.showWarning(`Unknown command: ${command}`);
                return false;
            };
        }
    }

    setupTeslaFeatures() {
        // Tesla-specific UI adjustments
        this.adjustForTeslaUI();
        
        // Set up driving mode detection
        this.setupDrivingMode();
        
        // Initialize Tesla audio integration
        this.setupTeslaAudio();
    }

    adjustForTeslaUI() {
        // Adjust UI for Tesla's screen
        document.body.classList.add('tesla-ui');
        
        // Set Tesla-appropriate font sizes
        const style = document.createElement('style');
        style.textContent = `
            .tesla-ui {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            }
        `;
        document.head.appendChild(style);
    }

    setupDrivingMode() {
        // Monitor driving state
        setInterval(() => {
            if (window.TeslaAPI && TeslaAPI.safety.isDriving()) {
                document.body.classList.add('driving-mode');
                // Simplify UI when driving
                this.simplifyUIForDriving();
            } else {
                document.body.classList.remove('driving-mode');
                this.restoreFullUI();
            }
        }, 1000);
    }

    simplifyUIForDriving() {
        // Hide complex UI elements when driving
        document.querySelectorAll('.station-item').forEach(item => {
            item.style.opacity = '0.7';
        });
    }

    restoreFullUI() {
        // Restore full UI when parked
        document.querySelectorAll('.station-item').forEach(item => {
            item.style.opacity = '1';
        });
    }

    setupTeslaAudio() {
        // Integrate with Tesla's audio system
        if (window.TeslaAPI) {
            // Set initial volume
            TeslaAPI.audio.setVolume(this.volume);
        }
    }

    selectLanguage(lang) {
        this.currentLanguage = lang;
        
        // Update active language button
        document.querySelectorAll('.lang-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`[data-lang="${lang}"]`).classList.add('active');
        
        this.renderStations();
        
        // Tesla notification
        if (window.TeslaAPI) {
            TeslaAPI.notifications.showSuccess(`Switched to ${lang} stations`);
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
        const grid = document.getElementById('favoritesGrid');
        const favoriteStations = this.getFavoriteStations();
        
        if (favoriteStations.length === 0) {
            grid.innerHTML = '<p style="text-align: center; opacity: 0.7;">No favorites yet. Add some stations to your favorites!</p>';
            return;
        }

        grid.innerHTML = favoriteStations.map(station => `
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
        grid.querySelectorAll('.station-item').forEach(item => {
            item.addEventListener('click', () => {
                const station = JSON.parse(item.dataset.station);
                this.playStation(station.name);
                this.hideFavoritesPanel();
            });
        });

        grid.querySelectorAll('.favorite-btn').forEach(btn => {
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
            if (window.TeslaAPI) {
                TeslaAPI.notifications.showError('Failed to play station');
            }
        });

        this.audio.play().catch(error => {
            console.error('Playback error:', error);
            this.updatePlayButton('error');
        });

        // Tesla notification
        if (window.TeslaAPI) {
            TeslaAPI.notifications.showSuccess(`Now playing: ${station.name}`);
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

        localStorage.setItem('tcr-tesla-favorites', JSON.stringify(this.favorites));
        this.renderStations();
        this.renderFavorites();
        this.updateFavoriteButton();
    }

    setVolume(volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        
        if (this.audio) {
            this.audio.volume = this.volume / 100;
        }

        // Update Tesla audio system
        if (window.TeslaAPI) {
            TeslaAPI.audio.setVolume(this.volume);
        }

        document.getElementById('volumeFill').style.width = `${this.volume}%`;
        document.getElementById('volumeValue').textContent = Math.round(this.volume);
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
        const btn = document.getElementById('favoriteBtn');
        const icon = btn.querySelector('.heart-icon');
        
        if (this.currentStation && this.favorites.includes(this.currentStation.name)) {
            icon.textContent = '♥';
            btn.classList.add('active');
        } else {
            icon.textContent = '♡';
            btn.classList.remove('active');
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
        const currentTimeElement = document.getElementById('currentTime');
        
        if (progressFill && currentTimeElement) {
            // For radio streams, we'll show elapsed time
            progressFill.style.width = '100%';
            currentTimeElement.textContent = this.formatTime(this.currentTime);
        }
    }

    formatTime(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    filterStations(searchTerm) {
        const items = document.querySelectorAll('.station-item');
        const term = searchTerm.toLowerCase();
        
        items.forEach(item => {
            const name = item.querySelector('.name').textContent.toLowerCase();
            const genre = item.querySelector('.genre').textContent.toLowerCase();
            
            if (name.includes(term) || genre.includes(term)) {
                item.style.display = 'flex';
            } else {
                item.style.display = 'none';
            }
        });
    }

    showFavoritesPanel() {
        document.getElementById('favoritesPanel').classList.add('active');
    }

    hideFavoritesPanel() {
        document.getElementById('favoritesPanel').classList.remove('active');
    }

    toggleVoiceControl() {
        this.isVoiceActive = !this.isVoiceActive;
        const voiceControl = document.getElementById('voiceControl');
        
        if (this.isVoiceActive) {
            voiceControl.classList.add('active');
            if (window.TeslaAPI) {
                TeslaAPI.voice.startListening();
            }
        } else {
            voiceControl.classList.remove('active');
            if (window.TeslaAPI) {
                TeslaAPI.voice.stopListening();
            }
        }
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
    new TCRadiosTesla();
});

// Tesla-specific initialization
if (typeof window.TeslaAPI !== 'undefined') {
    window.TeslaAPI.init();
}

