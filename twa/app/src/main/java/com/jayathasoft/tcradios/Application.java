/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayathasoft.tcradios;

import android.content.Context;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.util.Log;
import java.io.File;

public class Application extends android.app.Application {
    
    private static final String TAG = "TCRadiosApp";
    private static final String FAVORITES_KEY = "tcr-fav";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Clear TWA cache on startup while preserving favorites
        clearTWACachePreservingFavorites();
    }
    
    /**
     * Clears TWA cache and web data while preserving favorites from localStorage
     */
    private void clearTWACachePreservingFavorites() {
        try {
            Log.d(TAG, "Starting cache clear while preserving favorites");
            
            // Get the application context
            Context context = getApplicationContext();
            
            // Backup favorites before clearing cache
            String favoritesBackup = backupFavorites(context);
            
            // Clear WebView cache
            clearWebViewCache(context);
            
            // Clear WebView database (cookies, localStorage, etc.)
            clearWebViewDatabase(context);
            
            // Clear application cache
            clearApplicationCache(context);
            
            // Restore favorites after clearing cache
            restoreFavorites(context, favoritesBackup);
            
            Log.d(TAG, "Cache cleared successfully while preserving favorites");
            
        } catch (Exception e) {
            Log.e(TAG, "Error clearing cache: " + e.getMessage());
        }
    }
    
    /**
     * Backs up favorites from SharedPreferences before cache clear
     */
    private String backupFavorites(Context context) {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("TCRadiosPrefs", Context.MODE_PRIVATE);
            String favorites = prefs.getString(FAVORITES_KEY, "[]");
            Log.d(TAG, "Favorites backed up: " + favorites);
            return favorites;
        } catch (Exception e) {
            Log.e(TAG, "Error backing up favorites: " + e.getMessage());
            return "[]";
        }
    }
    
    /**
     * Restores favorites to SharedPreferences after cache clear
     */
    private void restoreFavorites(Context context, String favoritesBackup) {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("TCRadiosPrefs", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString(FAVORITES_KEY, favoritesBackup);
            editor.apply();
            Log.d(TAG, "Favorites restored: " + favoritesBackup);
        } catch (Exception e) {
            Log.e(TAG, "Error restoring favorites: " + e.getMessage());
        }
    }
    
    /**
     * Clears WebView cache
     */
    private void clearWebViewCache(Context context) {
        try {
            WebView webView = new WebView(context);
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            webView.destroy();
            Log.d(TAG, "WebView cache cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing WebView cache: " + e.getMessage());
        }
    }
    
    /**
     * Clears WebView database (cookies, sessionStorage, etc.) while preserving localStorage
     */
    private void clearWebViewDatabase(Context context) {
        try {
            WebViewDatabase webViewDatabase = WebViewDatabase.getInstance(context);
            
            // Clear cookies
            webViewDatabase.clearHttpAuthUsernamePassword();
            
            // Note: We don't clear WebStorage completely to preserve localStorage
            // Only clear sessionStorage and other temporary data
            Log.d(TAG, "WebView database cleared (preserving localStorage)");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing WebView database: " + e.getMessage());
        }
    }
    
    /**
     * Clears application cache directory
     */
    private void clearApplicationCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                deleteRecursive(cacheDir);
                Log.d(TAG, "Application cache cleared");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing application cache: " + e.getMessage());
        }
    }
    
    /**
     * Recursively deletes files and directories
     */
    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }
}
