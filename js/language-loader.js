/**
 * Dynamic Language Loader Utility
 * Automatically discovers and loads languages from GitHub repository
 * No need to manually update code when adding new languages!
 */

const LANGUAGE_LOADER = {
  GITHUB_REPO: 'simsonpeter/Tcradios',
  GITHUB_BRANCH: 'main',
  
  get BASE_URL() {
    return `https://raw.githubusercontent.com/${this.GITHUB_REPO}/refs/heads/${this.GITHUB_BRANCH}`;
  },
  
  // Fallback list (used if GitHub API fails)
  FALLBACK_LANGUAGES: ['tamil', 'english', 'dutch', 'hindi', 'malayalam', 'sinhala', 'telugu'],
  
  /**
   * Discover available languages from GitHub repository
   * @returns {Promise<string[]>} Array of language names
   */
  async discoverLanguages() {
    try {
      const apiUrl = `https://api.github.com/repos/${this.GITHUB_REPO}/contents/languages?ref=${this.GITHUB_BRANCH}`;
      const response = await fetch(apiUrl);
      
      if (response.ok) {
        const files = await response.json();
        const languages = files
          .filter(file => file.type === 'file' && file.name.endsWith('.json'))
          .map(file => file.name.replace('.json', ''))
          .filter(lang => !lang.match(/^(readme|README|\.)/i)); // Exclude readme and hidden files
        
        if (languages.length > 0) {
          console.log('✅ Discovered languages from GitHub:', languages);
          return languages;
        }
      }
    } catch (error) {
      console.warn('⚠️ Failed to fetch languages from GitHub API, using fallback:', error);
    }
    
    // Fallback to predefined list
    console.log('📋 Using fallback language list:', this.FALLBACK_LANGUAGES);
    return this.FALLBACK_LANGUAGES;
  },
  
  /**
   * Build language URLs dynamically
   * @returns {Promise<Object>} Object with language names as keys and URLs as values
   */
  async buildLanguageUrls() {
    const languages = await this.discoverLanguages();
    const langUrls = {};
    
    languages.forEach(lang => {
      // Special case: Tamil uses stations.json instead of languages/tamil.json
      if (lang === 'tamil') {
        langUrls[lang] = `${this.BASE_URL}/stations.json`;
      } else {
        langUrls[lang] = `${this.BASE_URL}/languages/${lang}.json`;
      }
    });
    
    return langUrls;
  },
  
  /**
   * Load stations for all languages
   * @param {Object} langUrls - Object with language names and URLs
   * @returns {Promise<Object>} Object with language names as keys and station arrays as values
   */
  async loadStations(langUrls) {
    const stationsPerLang = {};
    
    const promises = Object.keys(langUrls).map(async lang => {
      try {
        console.log(`📻 Loading stations for ${lang}...`);
        const response = await fetch(langUrls[lang]);
        
        if (!response.ok) {
          throw new Error(`Failed to load ${lang}: ${response.status} ${response.statusText}`);
        }
        
        stationsPerLang[lang] = await response.json();
        console.log(`✅ Loaded ${stationsPerLang[lang]?.length || 0} stations for ${lang}`);
      } catch (error) {
        console.error(`❌ Error loading ${lang}:`, error);
        stationsPerLang[lang] = []; // Set empty array on error
      }
    });
    
    await Promise.allSettled(promises);
    
    const loadedLanguages = Object.keys(stationsPerLang).filter(
      lang => stationsPerLang[lang] && stationsPerLang[lang].length > 0
    );
    
    console.log(`🎉 All stations loaded. Total languages: ${loadedLanguages.length}`);
    
    return stationsPerLang;
  }
};

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
  module.exports = LANGUAGE_LOADER;
}
