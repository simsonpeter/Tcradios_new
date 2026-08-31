package com.jayathasoft.tcradios.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Compact home-screen player: station name plus play/pause, previous, and next.
 */
public class PlayLastWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = StationSyncStore.prefs(context);
        String name = prefs.getString(StationSyncStore.PREF_LAST_NAME, "");
        boolean playing = StationSyncStore.isPlaying(context);
        String status;
        if (name == null || name.isEmpty()) {
            name = "TC RADIOS";
            status = "Tap play";
        } else {
            status = playing ? "Live" : "Paused";
        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_play_last);
            views.setTextViewText(R.id.widget_play_last_title, name);
            views.setTextViewText(R.id.widget_play_last_subtitle, status);
            views.setImageViewResource(
                    R.id.widget_btn_play,
                    playing ? R.drawable.ic_widget_pause : R.drawable.ic_widget_play);

            Intent openApp = new Intent(context, LauncherActivity.class);
            openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            views.setOnClickPendingIntent(
                    R.id.widget_now_playing,
                    PendingIntent.getActivity(
                            context,
                            1000,
                            openApp,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

            views.setOnClickPendingIntent(
                    R.id.widget_btn_prev,
                    serviceIntent(context, 1001, AndroidAutoMediaService.ACTION_SKIP_PREVIOUS));
            views.setOnClickPendingIntent(
                    R.id.widget_btn_play,
                    serviceIntent(context, 1002, AndroidAutoMediaService.ACTION_TOGGLE_PLAYBACK));
            views.setOnClickPendingIntent(
                    R.id.widget_btn_next,
                    serviceIntent(context, 1003, AndroidAutoMediaService.ACTION_SKIP_NEXT));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private static PendingIntent serviceIntent(Context context, int requestCode, String action) {
        Intent intent = new Intent(context, AndroidAutoMediaService.class);
        intent.setAction(action);
        return PendingIntent.getService(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
