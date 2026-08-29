package com.jayathasoft.tcradios.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * 1x1 home-screen widget — tap to play the last / most recent station.
 */
public class PlayLastWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = StationSyncStore.prefs(context);
        String name = prefs.getString(StationSyncStore.PREF_LAST_NAME, "Play last");
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_play_last);
            views.setTextViewText(R.id.widget_play_last_title, "TC RADIOS");
            views.setTextViewText(R.id.widget_play_last_subtitle,
                    name == null || name.isEmpty() ? "Tap to play" : name);

            Intent playIntent = new Intent(context, AndroidAutoMediaService.class);
            playIntent.setAction(AndroidAutoMediaService.ACTION_PLAY_LAST);
            PendingIntent pendingIntent = PendingIntent.getService(
                    context,
                    1001,
                    playIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_play_last_root, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
