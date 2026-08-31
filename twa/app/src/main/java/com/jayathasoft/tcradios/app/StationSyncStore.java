package com.jayathasoft.tcradios.app;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shared prefs bridge between the PWA (favorites / recents / auth) and
 * Android Auto + home-screen widgets.
 */
public final class StationSyncStore {
    public static final String PREFS_NAME = "android_auto_media";
    public static final String PREF_FAVORITE_IDS = "favorite_station_ids";
    public static final String PREF_FAVORITE_NAMES = "favorite_station_names";
    public static final String PREF_RECENT_JSON = "recent_stations_json";
    public static final String PREF_AUTH_TOKEN = "supabase_access_token";
    public static final String PREF_USER_ID = "supabase_user_id";
    public static final String PREF_LAST_SYNC_MS = "library_last_sync_ms";

    public static final String PREF_LAST_ID = "last_station_id";
    public static final String PREF_LAST_LANGUAGE = "last_station_language";
    public static final String PREF_LAST_NAME = "last_station_name";
    public static final String PREF_LAST_GENRE = "last_station_genre";
    public static final String PREF_LAST_URL = "last_station_url";
    public static final String PREF_LAST_ARTWORK = "last_station_artwork";
    public static final String PREF_IS_PLAYING = "is_playing";

    private static final String SUPABASE_URL = "https://rerzpgrxztaarwecidza.supabase.co";
    private static final String SUPABASE_ANON_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJlcnpwZ3J4enRhYXJ3ZWNpZHphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODQzNjkyOTAsImV4cCI6MjA5OTk0NTI5MH0.HpIroCI67avAyizlrk9Wv05RC3QzYCPV_2JHuM6h5es";

    private StationSyncStore() {}

    public static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void applySyncPayload(Context context, String jsonPayload) throws JSONException {
        if (TextUtils.isEmpty(jsonPayload)) return;
        JSONObject payload = new JSONObject(jsonPayload);
        SharedPreferences.Editor editor = prefs(context).edit();

        if (payload.has("favorites")) {
            JSONArray favorites = payload.optJSONArray("favorites");
            if (favorites == null) favorites = new JSONArray();
            editor.putString(PREF_FAVORITE_NAMES, favorites.toString());
            editor.putString(PREF_FAVORITE_IDS, namesToLegacyIds(favorites));
        }

        if (payload.has("recents")) {
            JSONArray recents = payload.optJSONArray("recents");
            if (recents == null) recents = new JSONArray();
            editor.putString(PREF_RECENT_JSON, recents.toString());
            if (recents.length() > 0) {
                JSONObject first = recents.optJSONObject(0);
                if (first != null) {
                    putLastStationFields(editor, first);
                }
            }
        }

        if (payload.has("lastPlayed") && !payload.isNull("lastPlayed")) {
            JSONObject last = payload.optJSONObject("lastPlayed");
            if (last != null) {
                putLastStationFields(editor, last);
            }
        }

        String token = payload.optString("token", "").trim();
        String userId = payload.optString("userId", "").trim();
        if (!TextUtils.isEmpty(token)) editor.putString(PREF_AUTH_TOKEN, token);
        if (!TextUtils.isEmpty(userId)) editor.putString(PREF_USER_ID, userId);
        editor.putLong(PREF_LAST_SYNC_MS, System.currentTimeMillis());
        editor.apply();

        notifyWidgets(context);
    }

    private static void putLastStationFields(SharedPreferences.Editor editor, JSONObject station) {
        String name = station.optString("name", "").trim();
        String url = firstNonEmpty(station.optString("url", ""), station.optString("streamUrl", ""));
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url)) return;
        String lang = station.optString("lang", station.optString("language", "tamil"));
        String genre = station.optString("genre", "Christian Radio");
        String artwork = firstNonEmpty(
                station.optString("logo", ""),
                station.optString("artwork", ""),
                station.optString("artworkUrl", ""),
                "https://tcradios-new.vercel.app/icons/icon-512x512.png");
        String id = station.optString("id", slugify(lang + "-" + name));
        editor.putString(PREF_LAST_ID, id);
        editor.putString(PREF_LAST_LANGUAGE, lang);
        editor.putString(PREF_LAST_NAME, name);
        editor.putString(PREF_LAST_GENRE, genre);
        editor.putString(PREF_LAST_URL, url);
        editor.putString(PREF_LAST_ARTWORK, artwork);
    }

    public static List<String> getFavoriteNames(Context context) {
        List<String> names = new ArrayList<>();
        String raw = prefs(context).getString(PREF_FAVORITE_NAMES, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                String name = array.optString(i, "").trim();
                if (!TextUtils.isEmpty(name)) names.add(name);
            }
        } catch (JSONException ignored) {
            // Fall through to legacy comma-separated IDs / names
        }
        if (names.isEmpty()) {
            String legacy = prefs(context).getString(PREF_FAVORITE_IDS, "");
            if (!TextUtils.isEmpty(legacy)) {
                for (String part : legacy.split(",")) {
                    String value = part.trim();
                    if (!TextUtils.isEmpty(value)) names.add(value);
                }
            }
        }
        return names;
    }

    public static boolean isPlaying(Context context) {
        return prefs(context).getBoolean(PREF_IS_PLAYING, false);
    }

    public static void setPlaying(Context context, boolean playing) {
        prefs(context).edit().putBoolean(PREF_IS_PLAYING, playing).apply();
        notifyWidgets(context);
    }

    public static JSONArray getRecentStationsJson(Context context) {
        String raw = prefs(context).getString(PREF_RECENT_JSON, "[]");
        try {
            return new JSONArray(raw);
        } catch (JSONException error) {
            return new JSONArray();
        }
    }

    /**
     * Pull cloud favorites from Supabase when a session token is available.
     * Safe to call from a background thread.
     */
    public static boolean refreshFavoritesFromCloud(Context context) {
        SharedPreferences preferences = prefs(context);
        String token = preferences.getString(PREF_AUTH_TOKEN, "");
        String userId = preferences.getString(PREF_USER_ID, "");
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(userId)) {
            return false;
        }

        HttpURLConnection connection = null;
        try {
            String endpoint = SUPABASE_URL + "/rest/v1/user_favorites?user_id=eq."
                    + userId + "&select=favorites,favorites_order";
            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(7000);
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");

            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }

            JSONArray rows = new JSONArray(builder.toString());
            if (rows.length() == 0) return false;
            JSONObject row = rows.getJSONObject(0);
            JSONArray favorites = row.optJSONArray("favorites");
            JSONArray order = row.optJSONArray("favorites_order");
            JSONArray ordered = order != null && order.length() > 0 ? order : favorites;
            if (ordered == null) ordered = new JSONArray();

            preferences.edit()
                    .putString(PREF_FAVORITE_NAMES, ordered.toString())
                    .putString(PREF_FAVORITE_IDS, namesToLegacyIds(ordered))
                    .putLong(PREF_LAST_SYNC_MS, System.currentTimeMillis())
                    .apply();
            notifyWidgets(context);
            return true;
        } catch (Exception ignored) {
            return false;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static void notifyWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        Intent playLast = new Intent(context, PlayLastWidgetProvider.class);
        playLast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] playLastIds = manager.getAppWidgetIds(new ComponentName(context, PlayLastWidgetProvider.class));
        playLast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, playLastIds);
        context.sendBroadcast(playLast);

        Intent favorites = new Intent(context, FavoritesGridWidgetProvider.class);
        favorites.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] favIds = manager.getAppWidgetIds(new ComponentName(context, FavoritesGridWidgetProvider.class));
        favorites.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, favIds);
        context.sendBroadcast(favorites);
    }

    private static String namesToLegacyIds(JSONArray names) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < names.length(); i++) {
            String name = names.optString(i, "").trim();
            if (TextUtils.isEmpty(name)) continue;
            if (builder.length() > 0) builder.append(',');
            builder.append(slugify(name));
        }
        return builder.toString();
    }

    public static String slugify(String value) {
        String slug = value == null ? "" : value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("(^-|-$)", "");
        return TextUtils.isEmpty(slug) ? "station" : slug;
    }

    private static String firstNonEmpty(String... values) {
        for (String value : values) {
            if (!TextUtils.isEmpty(value)) return value;
        }
        return "";
    }
}
