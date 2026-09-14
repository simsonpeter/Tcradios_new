package com.jayathasoft.tcradios.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    static final String EXTRA_LANGUAGE_ID = "language_id";
    static final String EXTRA_LANGUAGE_TITLE = "language_title";
    static final String EXTRA_SHOW_STATIONS = "show_stations";

    private static final int SCREEN_LANGUAGES = 0;
    private static final int SCREEN_STATIONS = 1;

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;

    private View languagesPanel;
    private View stationsPanel;
    private RecyclerView languagesList;
    private RecyclerView stationsList;
    private BrowseAdapter languagesAdapter;
    private BrowseAdapter stationsAdapter;
    private ProgressBar loadingLanguages;
    private ProgressBar loadingStations;
    private TextView stationsTitle;
    private String selectedLanguageId;
    private String selectedLanguageTitle = "";
    private String subscribedCategoryId;
    private int currentScreen = SCREEN_LANGUAGES;
    private boolean animatePanelChanges;

    private final MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        mediaController = new MediaControllerCompat(
                                MainActivity.this,
                                mediaBrowser.getSessionToken());
                        if (currentScreen == SCREEN_STATIONS && selectedLanguageId != null) {
                            loadStations(selectedLanguageId);
                        } else {
                            loadLanguages();
                        }
                    } catch (Exception error) {
                        // Connection failed silently; lists stay empty.
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        languagesPanel = findViewById(R.id.languages_panel);
        stationsPanel = findViewById(R.id.stations_panel);
        languagesList = findViewById(R.id.languages_list);
        stationsList = findViewById(R.id.stations_list);
        loadingLanguages = findViewById(R.id.loading_languages);
        loadingStations = findViewById(R.id.loading_stations);
        stationsTitle = findViewById(R.id.stations_title);

        TextView backButton = findViewById(R.id.back_button);
        FaIconHelper.setIcon(backButton, FaIconHelper.BACK, 14f);

        languagesAdapter = new BrowseAdapter(this::onLanguageClicked, true);
        stationsAdapter = new BrowseAdapter(this::onStationClicked, false);

        setupRecycler(languagesList, languagesAdapter);
        setupRecycler(stationsList, stationsAdapter);

        backButton.setOnClickListener(v -> navigateBack());

        languagesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                float offset = Math.min(Math.abs(recyclerView.computeVerticalScrollOffset()), 80) / 80f;
                recyclerView.setAlpha(1f - offset * 0.08f);
            }
        });
        stationsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                float offset = Math.min(Math.abs(recyclerView.computeVerticalScrollOffset()), 80) / 80f;
                recyclerView.setAlpha(1f - offset * 0.08f);
            }
        });

        mediaBrowser = new MediaBrowserCompat(
                this,
                new android.content.ComponentName(this, WearMediaService.class),
                connectionCallback,
                null);

        if (getIntent().getBooleanExtra(EXTRA_SHOW_STATIONS, false)) {
            selectedLanguageId = getIntent().getStringExtra(EXTRA_LANGUAGE_ID);
            selectedLanguageTitle = getIntent().getStringExtra(EXTRA_LANGUAGE_TITLE);
            if (selectedLanguageTitle == null) {
                selectedLanguageTitle = getString(R.string.stations_title);
            }
            showStationsScreen(false);
        } else {
            showLanguagesScreen(false);
        }
        animatePanelChanges = true;
    }

    private void setupRecycler(RecyclerView recyclerView, BrowseAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mediaBrowser.isConnected()) {
            mediaBrowser.connect();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaBrowser != null) {
            mediaBrowser.disconnect();
        }
        super.onDestroy();
    }

    private void navigateBack() {
        if (currentScreen == SCREEN_STATIONS) {
            showLanguagesScreen(true);
        }
    }

    @Override
    public void onBackPressed() {
        navigateBack();
        if (currentScreen == SCREEN_LANGUAGES) {
            super.onBackPressed();
        }
    }

    private void showLanguagesScreen(boolean animate) {
        currentScreen = SCREEN_LANGUAGES;
        if (animate && animatePanelChanges) {
            WearAnim.slideReplace(stationsPanel, languagesPanel, false);
        } else {
            languagesPanel.setVisibility(View.VISIBLE);
            stationsPanel.setVisibility(View.GONE);
        }
        scheduleListEntrance(languagesList);
    }

    private void showStationsScreen(boolean animate) {
        currentScreen = SCREEN_STATIONS;
        stationsTitle.setText(selectedLanguageTitle);
        if (animate && animatePanelChanges) {
            WearAnim.slideReplace(languagesPanel, stationsPanel, true);
        } else {
            languagesPanel.setVisibility(View.GONE);
            stationsPanel.setVisibility(View.VISIBLE);
        }
        scheduleListEntrance(stationsList);
    }

    private void scheduleListEntrance(RecyclerView list) {
        list.post(() -> {
            if (list.getAdapter() != null && list.getAdapter().getItemCount() > 0) {
                list.scheduleLayoutAnimation();
            }
        });
    }

    private void onLanguageClicked(MediaBrowserCompat.MediaItem item) {
        if (!item.isBrowsable()) return;
        selectedLanguageId = item.getMediaId();
        selectedLanguageTitle = item.getDescription().getTitle() != null
                ? item.getDescription().getTitle().toString()
                : getString(R.string.stations_title);
        showStationsScreen(true);
        loadStations(selectedLanguageId);
    }

    private void onStationClicked(MediaBrowserCompat.MediaItem item) {
        if (!item.isPlayable() || mediaController == null) return;
        mediaController.getTransportControls().playFromMediaId(item.getMediaId(), null);
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_LANGUAGE_ID, selectedLanguageId);
        intent.putExtra(PlayerActivity.EXTRA_LANGUAGE_TITLE, selectedLanguageTitle);
        startActivity(intent);
        overridePendingTransition(R.anim.player_enter, R.anim.browse_exit);
        finish();
    }

    private void loadLanguages() {
        if (mediaBrowser == null || !mediaBrowser.isConnected()) {
            return;
        }
        WearAnim.setVisible(loadingLanguages, true);
        mediaBrowser.subscribe(WearMediaService.ROOT_ID, new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(
                    @NonNull String parentId,
                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
                runOnUiThread(() -> {
                    WearAnim.setVisible(loadingLanguages, false);
                    List<MediaBrowserCompat.MediaItem> languages = new ArrayList<>();
                    for (MediaBrowserCompat.MediaItem child : children) {
                        if (!child.isBrowsable()) continue;
                        String mediaId = child.getMediaId();
                        if ((WearMediaService.CATEGORY_PREFIX + "all").equals(mediaId)) continue;
                        languages.add(child);
                    }
                    languagesAdapter.setItems(languages);
                    scheduleListEntrance(languagesList);
                });
            }

            @Override
            public void onError(@NonNull String parentId) {
                runOnUiThread(() -> WearAnim.setVisible(loadingLanguages, false));
            }
        });
    }

    private void loadStations(@NonNull String languageId) {
        if (mediaBrowser == null || !mediaBrowser.isConnected()) {
            return;
        }
        if (subscribedCategoryId != null && !subscribedCategoryId.equals(languageId)) {
            mediaBrowser.unsubscribe(subscribedCategoryId);
        }
        subscribedCategoryId = languageId;
        WearAnim.setVisible(loadingStations, true);
        mediaBrowser.subscribe(languageId, new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(
                    @NonNull String parentId,
                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
                runOnUiThread(() -> {
                    WearAnim.setVisible(loadingStations, false);
                    stationsAdapter.setItems(children);
                    scheduleListEntrance(stationsList);
                });
            }

            @Override
            public void onError(@NonNull String parentId) {
                runOnUiThread(() -> WearAnim.setVisible(loadingStations, false));
            }
        });
    }
}
