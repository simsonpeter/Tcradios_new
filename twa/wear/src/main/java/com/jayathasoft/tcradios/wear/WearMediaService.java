package com.jayathasoft.tcradios.wear;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WearMediaService extends MediaBrowserServiceCompat {
    public static final String ROOT_ID = "tcradios_root";
    public static final String STATION_PREFIX = "station:";
    public static final String CATEGORY_PREFIX = "category:";

    private static final String CATEGORY_ALL = CATEGORY_PREFIX + "all";
    private static final String DATA_BASE_URL =
            "https://raw.githubusercontent.com/simsonpeter/Tcradios/refs/heads/main";
    private static final String DEFAULT_ARTWORK_URL =
            "https://tcradios-new.vercel.app/icons/icon-512x512.png";
    private static final String CONTENT_STYLE_SUPPORTED =
            "android.media.browse.CONTENT_STYLE_SUPPORTED";
    private static final String CONTENT_STYLE_BROWSABLE_HINT =
            "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT";
    private static final String CONTENT_STYLE_PLAYABLE_HINT =
            "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT";
    private static final int CONTENT_STYLE_LIST = 1;
    private static final int PLAYBACK_NOTIFICATION_ID = 1001;
    private static final String PLAYBACK_CHANNEL_ID = "tcradios_wear_playback";

    private static final LanguageCategory[] LANGUAGE_CATEGORIES = new LanguageCategory[] {
            new LanguageCategory("tamil", "Tamil", "Tamil Christian stations",
                    DATA_BASE_URL + "/stations.json"),
            new LanguageCategory("english", "English", "English Christian stations",
                    DATA_BASE_URL + "/languages/english.json"),
            new LanguageCategory("malayalam", "Malayalam", "Malayalam Christian stations",
                    DATA_BASE_URL + "/languages/malayalam.json"),
            new LanguageCategory("hindi", "Hindi", "Hindi Christian stations",
                    DATA_BASE_URL + "/languages/hindi.json"),
            new LanguageCategory("dutch", "Dutch", "Dutch Christian stations",
                    DATA_BASE_URL + "/languages/dutch.json"),
            new LanguageCategory("sinhala", "Sinhala", "Sinhala Christian stations",
                    DATA_BASE_URL + "/languages/sinhala.json"),
            new LanguageCategory("telugu", "Telugu", "Telugu Christian stations",
                    DATA_BASE_URL + "/languages/telugu.json"),
            new LanguageCategory("kannada", "Kannada", "Kannada Christian stations",
                    DATA_BASE_URL + "/languages/Kannada.json")
    };
    private static final Station[] FALLBACK_STATIONS = new Station[] {
            new Station("kannada", "firstborn-kannada", "Firstborn Ministries Kannada",
                    "Kannada Christian", "https://centova71.instainternet.com/proxy/christianfm?mp=/stream&1745909125962/;stream/1",
                    DEFAULT_ARTWORK_URL),
            new Station("sinhala", "dimuthu-handa", "Dimuthu Handa",
                    "Sinhala Christian", "https://cp11.serverse.com/proxy/geethika/stream",
                    DEFAULT_ARTWORK_URL)
    };

    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private final ExecutorService stationLoader = Executors.newSingleThreadExecutor();
    private final Map<String, List<Station>> stationsByLanguage = new LinkedHashMap<>();
    private Station currentStation = null;
    private List<Station> activeQueue = Collections.emptyList();
    private boolean resumeAfterFocusLoss = false;
    private boolean pausedByTransientFocusLoss = false;

    private final AudioManager.OnAudioFocusChangeListener audioFocusListener = focusChange -> {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            pausePlayback();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            pausedByTransientFocusLoss = true;
            pausePlayback();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // Keep radio playing when the watch screen turns off.
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (currentStation != null && (resumeAfterFocusLoss || pausedByTransientFocusLoss)) {
                resumePlayback();
            }
            pausedByTransientFocusLoss = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createPlaybackChannel();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this, "TC RADIOS Wear");
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setActive(true);
        setSessionToken(mediaSession.getSessionToken());
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(
            @NonNull String clientPackageName,
            int clientUid,
            @Nullable Bundle rootHints) {
        Bundle extras = new Bundle();
        extras.putBoolean(CONTENT_STYLE_SUPPORTED, true);
        extras.putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_LIST);
        extras.putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST);
        return new BrowserRoot(ROOT_ID, extras);
    }

    @Override
    public void onLoadChildren(
            @NonNull String parentId,
            @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        if (ROOT_ID.equals(parentId)) {
            List<MediaBrowserCompat.MediaItem> categories = new ArrayList<>();
            categories.add(createCategory(CATEGORY_ALL, "All Stations", "Browse every TC RADIOS station"));
            for (LanguageCategory category : LANGUAGE_CATEGORIES) {
                categories.add(createCategory(CATEGORY_PREFIX + category.key, category.title, category.subtitle));
            }
            result.sendResult(categories);
            return;
        }

        if (!parentId.startsWith(CATEGORY_PREFIX)) {
            result.sendResult(new ArrayList<>());
            return;
        }

        result.detach();
        stationLoader.execute(() -> {
            List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
            try {
                for (Station station : getStationsForCategory(parentId)) {
                    items.add(createPlayableStation(station));
                }
            } catch (RuntimeException error) {
                // Return an empty list instead of hanging browse.
            }
            result.sendResult(items);
        });
    }

    private MediaBrowserCompat.MediaItem createCategory(String mediaId, String title, String subtitle) {
        Bundle extras = new Bundle();
        extras.putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_LIST);
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(mediaId)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setExtras(extras)
                .build();
        return new MediaBrowserCompat.MediaItem(
                description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createPlayableStation(Station station) {
        Bundle extras = new Bundle();
        extras.putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST);
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(STATION_PREFIX + station.id)
                .setTitle(station.name)
                .setSubtitle(station.genre)
                .setIconUri(Uri.parse(station.artworkUrl))
                .setMediaUri(Uri.parse(station.streamUrl))
                .setExtras(extras)
                .build();
        return new MediaBrowserCompat.MediaItem(
                description,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiverCompat.handleIntent(mediaSession, intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopPlayback();
        stationLoader.shutdownNow();
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }

    private final MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            if (currentStation == null) {
                updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
                return;
            }
            playStation(currentStation);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Station station = findStation(mediaId);
            if (station != null) {
                playStation(station);
            }
        }

        @Override
        public void onPause() {
            pausePlayback();
        }

        @Override
        public void onStop() {
            stopPlayback();
        }

        @Override
        public void onSkipToNext() {
            playAdjacentStation(1);
        }

        @Override
        public void onSkipToPrevious() {
            playAdjacentStation(-1);
        }
    };

    private void playAdjacentStation(int step) {
        if (activeQueue.isEmpty() || currentStation == null) return;
        int index = indexOfStationInQueue(currentStation.id);
        if (index < 0) index = 0;
        int nextIndex = (index + step + activeQueue.size()) % activeQueue.size();
        playStation(activeQueue.get(nextIndex));
    }

    private int indexOfStationInQueue(String stationId) {
        for (int i = 0; i < activeQueue.size(); i++) {
            if (activeQueue.get(i).id.equals(stationId)) {
                return i;
            }
        }
        return -1;
    }

    private void playStation(Station station) {
        if (station == null) return;

        currentStation = station;
        activeQueue = loadStationsForLanguage(station.languageKey);
        if (activeQueue.isEmpty()) {
            activeQueue = Collections.singletonList(station);
        }
        updateMetadata(station);
        updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING);
        mediaSession.setActive(true);
        startForeground(PLAYBACK_NOTIFICATION_ID, buildPlaybackNotification(station.name, true));

        if (!requestAudioFocus()) {
            updatePlaybackState(PlaybackStateCompat.STATE_ERROR);
            return;
        }

        releasePlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), android.os.PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(player -> {
            resumeAfterFocusLoss = true;
            player.start();
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
            startForeground(PLAYBACK_NOTIFICATION_ID, buildPlaybackNotification(station.name, true));
        });
        mediaPlayer.setOnCompletionListener(player -> updatePlaybackState(PlaybackStateCompat.STATE_STOPPED));
        mediaPlayer.setOnErrorListener((player, what, extra) -> {
            updatePlaybackState(PlaybackStateCompat.STATE_ERROR);
            return true;
        });

        try {
            mediaPlayer.setDataSource(station.streamUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException | IllegalArgumentException | SecurityException error) {
            updatePlaybackState(PlaybackStateCompat.STATE_ERROR);
            releasePlayer();
        }
    }

    private void createPlaybackChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(
                PLAYBACK_CHANNEL_ID,
                getString(R.string.playback_channel_name),
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(getString(R.string.playback_channel_description));
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildPlaybackNotification(String title, boolean playing) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PLAYBACK_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView());
        return builder.build();
    }

    private boolean requestAudioFocus() {
        if (audioManager == null) return true;
        int result = audioManager.requestAudioFocus(
                audioFocusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void resumePlayback() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (!requestAudioFocus()) {
                updatePlaybackState(PlaybackStateCompat.STATE_ERROR);
                return;
            }
            mediaPlayer.start();
            resumeAfterFocusLoss = true;
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
            if (currentStation != null) {
                startForeground(PLAYBACK_NOTIFICATION_ID, buildPlaybackNotification(currentStation.name, true));
            }
        } else if (currentStation != null) {
            playStation(currentStation);
        }
    }

    private void pausePlayback() {
        resumeAfterFocusLoss = mediaPlayer != null && mediaPlayer.isPlaying();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
        if (currentStation != null) {
            startForeground(PLAYBACK_NOTIFICATION_ID, buildPlaybackNotification(currentStation.name, false));
            mediaSession.setActive(true);
        }
    }

    private void stopPlayback() {
        resumeAfterFocusLoss = false;
        pausedByTransientFocusLoss = false;
        releasePlayer();
        abandonAudioFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH);
        } else {
            stopForeground(false);
        }
        currentStation = null;
        activeQueue = Collections.emptyList();
        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.setMetadata(new MediaMetadataCompat.Builder().build());
        }
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void abandonAudioFocus() {
        if (audioManager != null) {
            audioManager.abandonAudioFocus(audioFocusListener);
        }
    }

    private void updateMetadata(Station station) {
        if (mediaSession == null || station == null) return;

        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, STATION_PREFIX + station.id)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, station.name)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "TC RADIOS")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, station.genre)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, station.genre)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, station.streamUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, station.artworkUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, station.artworkUrl)
                .build();
        mediaSession.setMetadata(metadata);
    }

    private void updatePlaybackState(int state) {
        if (mediaSession == null) return;

        long actions = PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PLAY_PAUSE
                | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                | PlaybackStateCompat.ACTION_PAUSE
                | PlaybackStateCompat.ACTION_STOP
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder()
                .setActions(actions)
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f);

        if (state == PlaybackStateCompat.STATE_ERROR) {
            builder.setErrorMessage("Unable to play this station");
        }

        mediaSession.setPlaybackState(builder.build());
    }

    private Station findStation(String mediaId) {
        if (TextUtils.isEmpty(mediaId)) return null;
        String stationId = mediaId.startsWith(STATION_PREFIX)
                ? mediaId.substring(STATION_PREFIX.length())
                : mediaId;

        for (Station station : getLoadedStations()) {
            if (station.id.equals(stationId)) {
                return station;
            }
        }
        if (currentStation != null && currentStation.id.equals(stationId)) {
            return currentStation;
        }
        for (Station station : activeQueue) {
            if (station.id.equals(stationId)) {
                return station;
            }
        }
        return null;
    }

    private List<Station> getLoadedStations() {
        List<Station> allStations = new ArrayList<>();
        synchronized (stationsByLanguage) {
            for (List<Station> stations : stationsByLanguage.values()) {
                allStations.addAll(stations);
            }
        }
        return allStations;
    }

    private List<Station> getStationsForCategory(String categoryId) {
        if (CATEGORY_ALL.equals(categoryId)) {
            List<Station> allStations = new ArrayList<>();
            for (LanguageCategory category : LANGUAGE_CATEGORIES) {
                allStations.addAll(loadStationsForLanguage(category.key));
            }
            return allStations;
        }

        String languageKey = categoryId.substring(CATEGORY_PREFIX.length());
        return loadStationsForLanguage(languageKey);
    }

    private List<Station> loadStationsForLanguage(String languageKey) {
        synchronized (stationsByLanguage) {
            if (stationsByLanguage.containsKey(languageKey)) {
                return stationsByLanguage.get(languageKey);
            }
        }

        LanguageCategory category = findLanguageCategory(languageKey);
        List<Station> stations = new ArrayList<>();
        if (category != null) {
            try {
                stations.addAll(fetchStations(category));
            } catch (IOException | JSONException error) {
                // Keep browse usable even if remote station JSON is unavailable.
            }
        }

        if (stations.isEmpty()) {
            for (Station station : FALLBACK_STATIONS) {
                if (station.languageKey.equals(languageKey)) {
                    stations.add(station);
                }
            }
        }

        synchronized (stationsByLanguage) {
            stationsByLanguage.put(languageKey, stations);
        }
        return stations;
    }

    private LanguageCategory findLanguageCategory(String languageKey) {
        for (LanguageCategory category : LANGUAGE_CATEGORIES) {
            if (category.key.equals(languageKey)) {
                return category;
            }
        }
        return null;
    }

    private List<Station> fetchStations(LanguageCategory category) throws IOException, JSONException {
        String json = readUrl(category.url);
        Object parsed = new org.json.JSONTokener(json).nextValue();
        JSONArray stationArray;
        if (parsed instanceof JSONArray) {
            stationArray = (JSONArray) parsed;
        } else if (parsed instanceof JSONObject) {
            stationArray = ((JSONObject) parsed).optJSONArray("stations");
            if (stationArray == null) stationArray = new JSONArray();
        } else {
            stationArray = new JSONArray();
        }

        List<Station> stations = new ArrayList<>();
        for (int i = 0; i < stationArray.length(); i++) {
            JSONObject stationJson = stationArray.optJSONObject(i);
            if (stationJson == null) continue;

            String baseName = stationJson.optString("name", "Station " + (i + 1)).trim();
            String genre = stationJson.optString("genre", category.title + " Christian").trim();
            String artwork = firstNonEmpty(
                    stationJson.optString("logo", ""),
                    stationJson.optString("image", ""),
                    stationJson.optString("artwork", ""),
                    DEFAULT_ARTWORK_URL);
            List<String> streamUrls = collectStreamUrls(stationJson);
            for (int streamIndex = 0; streamIndex < streamUrls.size(); streamIndex++) {
                String name = streamUrls.size() > 1 ? baseName + " (" + (streamIndex + 1) + ")" : baseName;
                String id = category.key + "-" + slugify(name) + "-" + streamIndex;
                stations.add(new Station(category.key, id, name, genre, streamUrls.get(streamIndex), artwork));
            }
        }
        return stations;
    }

    private String readUrl(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setConnectTimeout(7000);
        connection.setReadTimeout(7000);
        connection.setRequestProperty("Accept", "application/json");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } finally {
            connection.disconnect();
        }
    }

    private List<String> collectStreamUrls(JSONObject stationJson) {
        List<String> urls = new ArrayList<>();
        addUrl(urls, stationJson.optString("url", ""));
        for (int i = 1; i <= 10; i++) {
            addUrl(urls, stationJson.optString("url" + i, ""));
        }
        addJsonUrlArray(urls, stationJson.optJSONArray("urls"));
        addJsonUrlArray(urls, stationJson.optJSONArray("streams"));
        return urls;
    }

    private void addJsonUrlArray(List<String> urls, JSONArray values) {
        if (values == null) return;
        for (int i = 0; i < values.length(); i++) {
            Object value = values.opt(i);
            if (value instanceof String) {
                addUrl(urls, (String) value);
            } else if (value instanceof JSONObject) {
                addUrl(urls, ((JSONObject) value).optString("url", ""));
            }
        }
    }

    private void addUrl(List<String> urls, String url) {
        if (TextUtils.isEmpty(url)) return;
        String trimmedUrl = url.trim();
        if (!urls.contains(trimmedUrl)) {
            urls.add(trimmedUrl);
        }
    }

    private String firstNonEmpty(String... values) {
        for (String value : values) {
            if (!TextUtils.isEmpty(value)) return value.trim();
        }
        return DEFAULT_ARTWORK_URL;
    }

    private String slugify(String value) {
        String slug = value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-+", "").replaceAll("-+$", "");
        return TextUtils.isEmpty(slug) ? "station" : slug;
    }

    private static final class LanguageCategory {
        final String key;
        final String title;
        final String subtitle;
        final String url;

        LanguageCategory(String key, String title, String subtitle, String url) {
            this.key = key;
            this.title = title;
            this.subtitle = subtitle;
            this.url = url;
        }
    }

    private static final class Station {
        final String languageKey;
        final String id;
        final String name;
        final String genre;
        final String streamUrl;
        final String artworkUrl;

        Station(String languageKey, String id, String name, String genre, String streamUrl, String artworkUrl) {
            this.languageKey = languageKey;
            this.id = id;
            this.name = name;
            this.genre = genre;
            this.streamUrl = streamUrl;
            this.artworkUrl = artworkUrl;
        }
    }

    private static final class MediaButtonReceiverCompat {
        private MediaButtonReceiverCompat() {}

        static void handleIntent(MediaSessionCompat mediaSession, Intent intent) {
            if (mediaSession == null || intent == null) return;
            androidx.media.session.MediaButtonReceiver.handleIntent(mediaSession, intent);
        }
    }
}
