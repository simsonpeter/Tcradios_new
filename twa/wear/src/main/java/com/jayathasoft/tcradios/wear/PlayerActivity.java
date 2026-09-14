package com.jayathasoft.tcradios.wear;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PlayerActivity extends Activity {
    static final String EXTRA_LANGUAGE_ID = "language_id";
    static final String EXTRA_LANGUAGE_TITLE = "language_title";
    private static final long VOLUME_CONTROLS_HIDE_MS = 3000L;

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    private AudioManager audioManager;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView stationTitle;
    private ImageView artworkView;
    private View artworkRing;
    private TextView playPauseButton;
    private TextView volumeButton;
    private View volumeControls;
    private TextView volumeUpButton;
    private TextView volumeDownButton;
    private boolean volumeLongPressHandled;
    private ObjectAnimator artworkSpin;
    private ObjectAnimator ringSpin;
    private boolean artworkSpinning;

    private final Runnable hideVolumeControlsRunnable = new Runnable() {
        @Override
        public void run() {
            hideVolumeControls();
        }
    };

    private final MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    attachMediaController();
                }
            };

    private final MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
                    updatePlaybackUi(state);
                }

                @Override
                public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
                    updateMetadataUi(metadata);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        stationTitle = findViewById(R.id.station_title);
        artworkView = findViewById(R.id.artwork_view);
        artworkRing = findViewById(R.id.artwork_ring);
        playPauseButton = findViewById(R.id.play_pause_button);
        volumeButton = findViewById(R.id.volume_button);
        volumeControls = findViewById(R.id.volume_controls);
        volumeUpButton = findViewById(R.id.volume_up_button);
        volumeDownButton = findViewById(R.id.volume_down_button);
        TextView backButton = findViewById(R.id.player_back_button);
        TextView prevButton = findViewById(R.id.prev_button);
        TextView nextButton = findViewById(R.id.next_button);

        FaIconHelper.setIcon(backButton, FaIconHelper.BACK, 13f);
        FaIconHelper.setIcon(prevButton, FaIconHelper.PREVIOUS, 13f);
        FaIconHelper.setIcon(nextButton, FaIconHelper.NEXT, 13f);
        FaIconHelper.setIcon(playPauseButton, FaIconHelper.PLAY, 15f);
        FaIconHelper.setIcon(volumeUpButton, FaIconHelper.VOLUME_UP, 11f);
        FaIconHelper.setIcon(volumeDownButton, FaIconHelper.VOLUME_DOWN, 11f);
        CircleImageHelper.apply(artworkView);
        updateVolumeIcon();

        artworkSpin = WearAnim.spinning(artworkView);
        if (artworkRing != null) {
            ringSpin = WearAnim.spinning(artworkRing);
            ringSpin.setDuration(12000L);
        }

        backButton.setOnClickListener(v -> returnToStations());
        prevButton.setOnClickListener(v -> {
            tickHaptic();
            WearAnim.bounce(prevButton);
            sendTransportAction(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        });
        playPauseButton.setOnClickListener(v -> {
            tickHaptic();
            WearAnim.bounce(playPauseButton);
            togglePlayPause();
        });
        nextButton.setOnClickListener(v -> {
            tickHaptic();
            WearAnim.bounce(nextButton);
            sendTransportAction(PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        });

        volumeButton.setOnClickListener(v -> {
            if (volumeLongPressHandled) {
                volumeLongPressHandled = false;
                return;
            }
            tickHaptic();
            WearAnim.bounce(volumeButton);
            toggleMute();
        });
        volumeButton.setOnLongClickListener(v -> {
            volumeLongPressHandled = true;
            tickHaptic();
            showVolumeControls();
            return true;
        });
        volumeUpButton.setOnClickListener(v -> {
            tickHaptic();
            WearAnim.bounce(volumeUpButton);
            adjustVolume(AudioManager.ADJUST_RAISE);
            scheduleHideVolumeControls();
        });
        volumeDownButton.setOnClickListener(v -> {
            tickHaptic();
            WearAnim.bounce(volumeDownButton);
            adjustVolume(AudioManager.ADJUST_LOWER);
            scheduleHideVolumeControls();
        });

        mediaBrowser = new MediaBrowserCompat(
                this,
                new android.content.ComponentName(this, WearMediaService.class),
                connectionCallback,
                null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mediaBrowser.isConnected()) {
            mediaBrowser.connect();
        } else {
            attachMediaController();
        }
        updateVolumeIcon();
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(hideVolumeControlsRunnable);
        if (mediaController != null) {
            mediaController.unregisterCallback(controllerCallback);
        }
        setArtworkSpinning(false);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mediaBrowser != null) {
            mediaBrowser.disconnect();
        }
        super.onDestroy();
    }

    private void attachMediaController() {
        try {
            if (mediaController == null) {
                mediaController = new MediaControllerCompat(
                        PlayerActivity.this,
                        mediaBrowser.getSessionToken());
            }
            mediaController.registerCallback(controllerCallback);
            syncFromController();
        } catch (Exception ignored) {
            // Retry on the next lifecycle event.
        }
    }

    @Override
    public void onBackPressed() {
        if (volumeControls.getVisibility() == View.VISIBLE) {
            hideVolumeControls();
            return;
        }
        returnToStations();
    }

    private void tickHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager manager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                if (manager != null) {
                    manager.getDefaultVibrator().vibrate(
                            VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(18);
                    }
                }
            }
        } catch (Exception ignored) {
            // Haptics are best-effort on Wear.
        }
    }

    private void returnToStations() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_SHOW_STATIONS, true);
        intent.putExtra(MainActivity.EXTRA_LANGUAGE_ID, getIntent().getStringExtra(EXTRA_LANGUAGE_ID));
        intent.putExtra(MainActivity.EXTRA_LANGUAGE_TITLE, getIntent().getStringExtra(EXTRA_LANGUAGE_TITLE));
        startActivity(intent);
        overridePendingTransition(R.anim.browse_enter, R.anim.player_exit);
        finish();
    }

    private void syncFromController() {
        if (mediaController == null) return;
        updateMetadataUi(mediaController.getMetadata());
        updatePlaybackUi(mediaController.getPlaybackState());
    }

    private void togglePlayPause() {
        if (mediaController == null) return;
        PlaybackStateCompat state = mediaController.getPlaybackState();
        if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mediaController.getTransportControls().pause();
        } else {
            mediaController.getTransportControls().play();
        }
    }

    private void sendTransportAction(long action) {
        if (mediaController == null) return;
        if (action == PlaybackStateCompat.ACTION_SKIP_TO_NEXT) {
            mediaController.getTransportControls().skipToNext();
        } else if (action == PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) {
            mediaController.getTransportControls().skipToPrevious();
        }
    }

    private void toggleMute() {
        if (audioManager == null) return;
        boolean muted = audioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                muted ? AudioManager.ADJUST_UNMUTE : AudioManager.ADJUST_MUTE,
                0);
        updateVolumeIcon();
    }

    private void adjustVolume(int direction) {
        if (audioManager == null) return;
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0);
        updateVolumeIcon();
    }

    private void showVolumeControls() {
        WearAnim.showOverlay(volumeControls);
        scheduleHideVolumeControls();
    }

    private void hideVolumeControls() {
        handler.removeCallbacks(hideVolumeControlsRunnable);
        WearAnim.hideOverlay(volumeControls);
    }

    private void scheduleHideVolumeControls() {
        handler.removeCallbacks(hideVolumeControlsRunnable);
        handler.postDelayed(hideVolumeControlsRunnable, VOLUME_CONTROLS_HIDE_MS);
    }

    private void updateVolumeIcon() {
        if (audioManager == null) return;
        boolean muted = audioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        FaIconHelper.setIcon(
                volumeButton,
                muted ? FaIconHelper.VOLUME_MUTE : FaIconHelper.VOLUME_HIGH,
                13f);
        volumeButton.setContentDescription(getString(muted ? R.string.unmute : R.string.mute));
    }

    private void updatePlaybackUi(@Nullable PlaybackStateCompat state) {
        if (state == null) return;
        int playbackState = state.getState();
        boolean playing = playbackState == PlaybackStateCompat.STATE_PLAYING;

        if (playbackState == PlaybackStateCompat.STATE_ERROR) {
            WearAnim.fadeText(stationTitle, getString(R.string.connection_failed));
        } else if (playbackState == PlaybackStateCompat.STATE_BUFFERING) {
            WearAnim.fadeText(stationTitle, getString(R.string.buffering));
        } else if (mediaController != null && mediaController.getMetadata() != null) {
            CharSequence title = mediaController.getMetadata().getText(MediaMetadataCompat.METADATA_KEY_TITLE);
            WearAnim.fadeText(stationTitle, title != null ? title : getString(R.string.app_name));
        }

        FaIconHelper.setIcon(
                playPauseButton,
                playing ? FaIconHelper.PAUSE : FaIconHelper.PLAY,
                15f);
        setArtworkSpinning(playing);
        if (artworkRing != null) {
            WearAnim.fadeTo(artworkRing, playing ? 0.75f : 0.3f);
        }
    }

    private void setArtworkSpinning(boolean spinning) {
        if (spinning == artworkSpinning) return;
        artworkSpinning = spinning;
        if (spinning) {
            if (artworkSpin != null && !artworkSpin.isRunning()) {
                artworkSpin.start();
            }
            if (ringSpin != null && !ringSpin.isRunning()) {
                ringSpin.start();
            }
        } else {
            if (artworkSpin != null) {
                artworkSpin.cancel();
            }
            if (ringSpin != null) {
                ringSpin.cancel();
            }
        }
    }

    private void updateMetadataUi(@Nullable MediaMetadataCompat metadata) {
        if (metadata == null) return;

        CharSequence currentTitle = stationTitle.getText();
        if (currentTitle == null
                || (!getString(R.string.buffering).contentEquals(currentTitle)
                && !getString(R.string.connection_failed).contentEquals(currentTitle))) {
            CharSequence title = metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE);
            WearAnim.fadeText(stationTitle, title != null ? title : getString(R.string.app_name));
        }

        String artworkUri = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
        if (artworkUri == null) {
            artworkUri = metadata.getString(MediaMetadataCompat.METADATA_KEY_ART_URI);
        }
        ArtworkLoader.load(
                artworkView,
                artworkUri != null ? Uri.parse(artworkUri) : null,
                R.drawable.ic_station_placeholder);
    }
}
