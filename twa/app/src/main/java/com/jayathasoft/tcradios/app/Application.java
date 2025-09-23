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
package com.jayathasoft.tcradios.app;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import java.io.File;

public class Application extends android.app.Application {

  @Override
  public void onCreate() {
      super.onCreate();
      
      // Clear cache on every app startup while preserving favorites
      clearTWACachePreservingFavorites();
  }

  /**
   * Clear TWA cache while preserving favorites and user preferences
   */
  private void clearTWACachePreservingFavorites() {
      try {
          // Clear WebView cache
          clearWebViewCache();
          
          // Clear WebView database (form data, HTTP auth) but preserve localStorage
          clearWebViewDatabase();
          
          // Clear application cache directories
          clearApplicationCache();
          
          android.util.Log.d("TC_RADIOS", "Cache cleared successfully on startup");
      } catch (Exception e) {
          android.util.Log.e("TC_RADIOS", "Error clearing cache: " + e.getMessage());
      }
  }

  /**
   * Clear WebView cache and history
   */
  private void clearWebViewCache() {
      try {
          WebView webView = new WebView(this);
          webView.clearCache(true);
          webView.clearHistory();
          webView.clearFormData();
          webView.destroy();
      } catch (Exception e) {
          android.util.Log.e("TC_RADIOS", "Error clearing WebView cache: " + e.getMessage());
      }
  }

  /**
   * Clear WebView database (form data, HTTP auth) but preserve localStorage
   */
  private void clearWebViewDatabase() {
      try {
          WebViewDatabase.getInstance(this).clearFormData();
          WebViewDatabase.getInstance(this).clearHttpAuthUsernamePassword();
          // Note: We don't clear localStorage here to preserve favorites
      } catch (Exception e) {
          android.util.Log.e("TC_RADIOS", "Error clearing WebView database: " + e.getMessage());
      }
  }

  /**
   * Clear application cache directories
   */
  private void clearApplicationCache() {
      try {
          // Clear internal cache
          File internalCacheDir = getCacheDir();
          if (internalCacheDir != null && internalCacheDir.exists()) {
              deleteRecursive(internalCacheDir);
          }

          // Clear external cache
          File externalCacheDir = getExternalCacheDir();
          if (externalCacheDir != null && externalCacheDir.exists()) {
              deleteRecursive(externalCacheDir);
          }

          // Clear WebView cache directory
          File webViewCacheDir = new File(getCacheDir(), "webview");
          if (webViewCacheDir.exists()) {
              deleteRecursive(webViewCacheDir);
          }
      } catch (Exception e) {
          android.util.Log.e("TC_RADIOS", "Error clearing application cache: " + e.getMessage());
      }
  }

  /**
   * Recursively delete directory contents
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
      // Don't delete the directory itself, just its contents
      if (!fileOrDirectory.getName().equals("cache")) {
          fileOrDirectory.delete();
      }
  }
}
