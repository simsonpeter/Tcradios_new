package com.jayathasoft.tcradios.app;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AndroidAutoMediaService extends MediaBrowserServiceCompat {
    private static final String ROOT_ID = "tcradios_root";
    private static final String STATION_PREFIX = "station:";
    private static final String CATEGORY_PREFIX = "category:";
    private static final String CATEGORY_ALL = CATEGORY_PREFIX + "all";
    private static final String CATEGORY_KANNADA = CATEGORY_PREFIX + "kannada";
    private static final String CATEGORY_SINHALA = CATEGORY_PREFIX + "sinhala";
    private static final String CONTENT_STYLE_SUPPORTED =
            "android.media.browse.CONTENT_STYLE_SUPPORTED";
    private static final String CONTENT_STYLE_BROWSABLE_HINT =
            "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT";
    private static final String CONTENT_STYLE_PLAYABLE_HINT =
            "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT";
    private static final int CONTENT_STYLE_LIST = 1;

    private static final Station[] STATIONS = new Station[] {
            new Station(
                    "firstborn-kannada",
                    "Firstborn Ministries Kannada",
                    "Kannada Christian",
                    "https://centova71.instainternet.com/proxy/christianfm?mp=/stream&1745909125962/;stream/1",
                    "https://tvradiotuner.com/wp-content/uploads/Firstborn-Ministries.jpg"),
            new Station(
                    "hoj-kannada",
                    "HOJ Kannada",
                    "Kannada Christian",
                    "https://dc1.serverse.com/proxy/hojkannada/stream",
                    "https://tamilradios.net/webpimg/kerala/radio_39070_Hand-Of-Jesus-%E2%80%93-Telugu.webp"),
            new Station(
                    "kannada-bible-radio",
                    "Kannada Bible Radio",
                    "Kannada Christian",
                    "https://gains.reviveradio.net/proxy/kannadabible?mp=/stream",
                    "https://indiabible.radio/wp-content/uploads/2022/05/default-2.png"),
            new Station(
                    "dimuthu-handa",
                    "Dimuthu Handa",
                    "Sinhala Christian",
                    "https://cp11.serverse.com/proxy/geethika/stream",
                    "https://i.ibb.co/1tKDL1Dg/cropped-site-icon-1.png"),
            new Station(
                    "havilah-fm",
                    "Havilah FM",
                    "Sinhala Christian",
                    "https://stream.zeno.fm/434ubnhq638uv",
                    "https://listenonlineradio.com/wp-content/uploads/HaVilah-FM.jpg"),
            new Station(
                    "jesus-coming-fm",
                    "Jesus Coming FM",
                    "Sinhala Christian",
                    "https://live.jesuscomingfm.com/proxy/sinhala/stream.aac",
                    "https://static-media.streema.com/media/cache/e0/05/e0055f1d09d7cbe84c6187f1a6e50823.jpg"),
            new Station(
                    "judah-sinhala-bible",
                    "Judah Sinhala Bible",
                    "Sinhala Christian",
                    "https://stream-176.zeno.fm/3gw6wa7apy8uv",
                    "https://zeno.fm/favicon.ico")
    };

    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Station currentStation = STATIONS[0];
    private int currentStationIndex = 0;

    private final AudioManager.OnAudioFocusChangeListener audioFocusListener = focusChange -> {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            stopPlayback();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            pausePlayback();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN && mediaPlayer != null && currentStation != null) {
            resumePlayback();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this, "TC RADIOS Android Auto");
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(mediaSessionCallback);
        setSessionToken(mediaSession.getSessionToken());

        updateMetadata(currentStation);
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
            categories.add(createCategory(CATEGORY_KANNADA, "Kannada", "Kannada Christian stations"));
            categories.add(createCategory(CATEGORY_SINHALA, "Sinhala", "Sinhala Christian stations"));
            result.sendResult(categories);
            return;
        }

        if (!parentId.startsWith(CATEGORY_PREFIX)) {
            result.sendResult(new ArrayList<>());
            return;
        }

        List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
        for (Station station : STATIONS) {
            if (CATEGORY_ALL.equals(parentId)
                    || (CATEGORY_KANNADA.equals(parentId) && station.genre.toLowerCase().contains("kannada"))
                    || (CATEGORY_SINHALA.equals(parentId) && station.genre.toLowerCase().contains("sinhala"))) {
                items.add(createPlayableStation(station));
            }
        }
        result.sendResult(items);
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
                currentStation = STATIONS[0];
                currentStationIndex = 0;
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
            currentStationIndex = (currentStationIndex + 1) % STATIONS.length;
            playStation(STATIONS[currentStationIndex]);
        }

        @Override
        public void onSkipToPrevious() {
            currentStationIndex = (currentStationIndex - 1 + STATIONS.length) % STATIONS.length;
            playStation(STATIONS[currentStationIndex]);
        }
    };

    private void playStation(Station station) {
        if (station == null) return;

        currentStation = station;
        currentStationIndex = indexOfStation(station.id);
        updateMetadata(station);
        updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING);
        mediaSession.setActive(true);

        if (!requestAudioFocus()) {
            updatePlaybackState(PlaybackStateCompat.STATE_ERROR);
            return;
        }

        releasePlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(player -> {
            player.start();
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
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
            mediaPlayer.start();
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
        } else if (currentStation != null) {
            playStation(currentStation);
        }
    }

    private void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
    }

    private void stopPlayback() {
        releasePlayer();
        abandonAudioFocus();
        if (mediaSession != null) {
            mediaSession.setActive(false);
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

        for (Station station : STATIONS) {
            if (station.id.equals(stationId)) {
                return station;
            }
        }
        return null;
    }

    private int indexOfStation(String stationId) {
        for (int i = 0; i < STATIONS.length; i++) {
            if (STATIONS[i].id.equals(stationId)) {
                return i;
            }
        }
        return 0;
    }

    private static final class Station {
        final String id;
        final String name;
        final String genre;
        final String streamUrl;
        final String artworkUrl;

        Station(String id, String name, String genre, String streamUrl, String artworkUrl) {
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
