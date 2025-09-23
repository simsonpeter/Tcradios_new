// Samsung TV App for TC RADIOS
class TCRadiosTV {
    constructor() {
        this.currentLanguage = 'tamil';
        this.stations = {};
        this.favorites = JSON.parse(localStorage.getItem('tcr-tv-favorites') || '[]');
        this.currentStation = null;
        this.audio = null;
        this.isPlaying = false;
        this.volume = 50;
        this.focusedElement = null;
        
        this.init();
    }

    async init() {
        try {
            this.showLoadingScreen();
            await this.loadStations();
            this.setupEventListeners();
            this.setupTVNavigation();
            this.renderStations();
            this.renderFavorites();
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
        document.querySelectorAll('.language-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                this.selectLanguage(e.target.closest('.language-btn').dataset.lang);
            });
        });

        // Player controls
        document.getElementById('playPauseBtn').addEventListener('click', () => {
            this.togglePlayPause();
        });

        document.getElementById('stopBtn').addEventListener('click', () => {
            this.stop();
        });

        document.getElementById('favoriteBtn').addEventListener('click', () => {
            this.toggleFavorite();
        });

        // Volume control
        const volumeBar = document.querySelector('.volume-bar');
        volumeBar.addEventListener('click', (e) => {
            const rect = volumeBar.getBoundingClientRect();
            const clickX = e.clientX - rect.left;
            const percentage = (clickX / rect.width) * 100;
            this.setVolume(Math.max(0, Math.min(100, percentage)));
        });

        // Retry button
        document.getElementById('retryBtn').addEventListener('click', () => {
            this.init();
        });
    }

    setupTVNavigation() {
        // TV remote navigation
        document.addEventListener('keydown', (e) => {
            switch(e.key) {
                case 'ArrowUp':
                    this.navigateUp();
                    break;
                case 'ArrowDown':
                    this.navigateDown();
                    break;
                case 'ArrowLeft':
                    this.navigateLeft();
                    break;
                case 'ArrowRight':
                    this.navigateRight();
                    break;
                case 'Enter':
                    this.selectFocusedElement();
                    break;
                case 'Backspace':
                    this.goBack();
                    break;
                case 'F1': // Red button
                    this.togglePlayPause();
                    break;
                case 'F2': // Green button
                    this.stop();
                    break;
                case 'F3': // Blue button
                    this.toggleFavorite();
                    break;
            }
        });

        // Set initial focus
        this.setFocus(document.querySelector('.language-btn.active'));
    }

    navigateUp() {
        const current = this.focusedElement;
        if (!current) return;

        const parent = current.closest('.language-grid, .stations-grid, .favorites-grid');
        if (!parent) return;

        const elements = Array.from(parent.querySelectorAll('button, .station-card'));
        const currentIndex = elements.indexOf(current);
        
        if (currentIndex > 0) {
            this.setFocus(elements[currentIndex - 1]);
        }
    }

    navigateDown() {
        const current = this.focusedElement;
        if (!current) return;

        const parent = current.closest('.language-grid, .stations-grid, .favorites-grid');
        if (!parent) return;

        const elements = Array.from(parent.querySelectorAll('button, .station-card'));
        const currentIndex = elements.indexOf(current);
        
        if (currentIndex < elements.length - 1) {
            this.setFocus(elements[currentIndex + 1]);
        }
    }

    navigateLeft() {
        const current = this.focusedElement;
        if (!current) return;

        // Navigate between sections
        if (current.closest('.language-section')) {
            this.setFocus(document.querySelector('.station-card'));
        } else if (current.closest('.stations-section')) {
            this.setFocus(document.querySelector('.language-btn.active'));
        }
    }

    navigateRight() {
        const current = this.focusedElement;
        if (!current) return;

        // Navigate between sections
        if (current.closest('.language-section')) {
            this.setFocus(document.querySelector('.station-card'));
        } else if (current.closest('.stations-section')) {
            this.setFocus(document.querySelector('.favorite-btn'));
        }
    }

    setFocus(element) {
        if (this.focusedElement) {
            this.focusedElement.classList.remove('focused');
        }
        
        this.focusedElement = element;
        if (element) {
            element.classList.add('focused');
            element.focus();
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }

    selectFocusedElement() {
        if (!this.focusedElement) return;

        if (this.focusedElement.classList.contains('language-btn')) {
            this.selectLanguage(this.focusedElement.dataset.lang);
        } else if (this.focusedElement.classList.contains('station-card')) {
            const stationName = this.focusedElement.querySelector('.station-name').textContent;
            this.playStation(stationName);
        } else if (this.focusedElement.classList.contains('favorite-btn')) {
            this.toggleFavorite();
        }
    }

    selectLanguage(lang) {
        this.currentLanguage = lang;
        
        // Update active language button
        document.querySelectorAll('.language-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`[data-lang="${lang}"]`).classList.add('active');
        
        this.renderStations();
        this.setFocus(document.querySelector('.station-card'));
    }

    renderStations() {
        const grid = document.getElementById('stationsGrid');
        const stations = this.stations[this.currentLanguage] || [];
        
        grid.innerHTML = stations.map(station => `
            <div class="station-card" data-station='${JSON.stringify(station)}'>
                <img src="${station.logo}" alt="${station.name}" class="station-logo" 
                     onerror="this.src='images/default-station.png'">
                <div class="station-info">
                    <div class="station-name">${station.name}</div>
                    <div class="station-genre">${station.genre}</div>
                </div>
                <button class="favorite-btn ${this.favorites.includes(station.name) ? 'active' : ''}" 
                        data-station="${station.name}">
                    ${this.favorites.includes(station.name) ? '♥' : '♡'}
                </button>
            </div>
        `).join('');

        // Add event listeners to station cards
        grid.querySelectorAll('.station-card').forEach(card => {
            card.addEventListener('click', () => {
                const station = JSON.parse(card.dataset.station);
                this.playStation(station.name);
            });
        });

        // Add event listeners to favorite buttons
        grid.querySelectorAll('.favorite-btn').forEach(btn => {
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
            <div class="station-card" data-station='${JSON.stringify(station)}'>
                <img src="${station.logo}" alt="${station.name}" class="station-logo" 
                     onerror="this.src='images/default-station.png'">
                <div class="station-info">
                    <div class="station-name">${station.name}</div>
                    <div class="station-genre">${station.genre}</div>
                </div>
                <button class="favorite-btn active" data-station="${station.name}">♥</button>
            </div>
        `).join('');

        // Add event listeners
        grid.querySelectorAll('.station-card').forEach(card => {
            card.addEventListener('click', () => {
                const station = JSON.parse(card.dataset.station);
                this.playStation(station.name);
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
        this.showPlayer();
        this.updatePlayerInfo();
        
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
            this.updateStationCards();
        });

        this.audio.addEventListener('pause', () => {
            this.isPlaying = false;
            this.updatePlayButton('play');
        });

        this.audio.addEventListener('error', (e) => {
            console.error('Audio error:', e);
            this.updatePlayButton('error');
        });

        this.audio.play().catch(error => {
            console.error('Playback error:', error);
            this.updatePlayButton('error');
        });
    }

    togglePlayPause() {
        if (!this.audio) return;

        if (this.isPlaying) {
            this.audio.pause();
        } else {
            this.audio.play().catch(error => {
                console.error('Playback error:', error);
            });
        }
    }

    stop() {
        if (this.audio) {
            this.audio.pause();
            this.audio.currentTime = 0;
            this.isPlaying = false;
            this.updatePlayButton('play');
            this.updateStationCards();
        }
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

        localStorage.setItem('tcr-tv-favorites', JSON.stringify(this.favorites));
        this.renderStations();
        this.renderFavorites();
        this.updateFavoriteButton();
    }

    setVolume(volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        
        if (this.audio) {
            this.audio.volume = this.volume / 100;
        }

        document.getElementById('volumeFill').style.width = `${this.volume}%`;
        document.getElementById('volumeValue').textContent = Math.round(this.volume);
    }

    showPlayer() {
        document.getElementById('tvPlayer').classList.add('active');
    }

    hidePlayer() {
        document.getElementById('tvPlayer').classList.remove('active');
    }

    updatePlayerInfo() {
        if (!this.currentStation) return;

        document.getElementById('currentStationLogo').src = this.currentStation.logo;
        document.getElementById('currentStationName').textContent = this.currentStation.name;
        document.getElementById('currentStationGenre').textContent = this.currentStation.genre;
        document.getElementById('songTitle').textContent = 'Loading...';
        document.getElementById('artistName').textContent = 'Please wait';
    }

    updatePlayButton(state) {
        const btn = document.getElementById('playPauseBtn');
        const icon = btn.querySelector('.play-icon');
        
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

    updateStationCards() {
        document.querySelectorAll('.station-card').forEach(card => {
            const station = JSON.parse(card.dataset.station);
            if (this.currentStation && station.name === this.currentStation.name && this.isPlaying) {
                card.classList.add('playing');
            } else {
                card.classList.remove('playing');
            }
        });
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

    goBack() {
        if (document.getElementById('tvPlayer').classList.contains('active')) {
            this.hidePlayer();
        } else {
            // Exit app (if supported by Tizen)
            if (typeof tizen !== 'undefined') {
                tizen.application.getCurrentApplication().exit();
            }
        }
    }
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new TCRadiosTV();
});

// Tizen-specific initialization
if (typeof tizen !== 'undefined') {
    tizen.application.getCurrentApplication().addEventListener('appcontrol', (event) => {
        console.log('App control event:', event);
    });
}

