package com.jayathasoft.tcradios.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.List;

/**
 * 2x2 home-screen widget — up to four synced favorites as one-tap play buttons.
 */
public class FavoritesGridWidgetProvider extends AppWidgetProvider {
    private static final int[] CELL_IDS = {
            R.id.widget_fav_1,
            R.id.widget_fav_2,
            R.id.widget_fav_3,
            R.id.widget_fav_4
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        List<String> names = StationSyncStore.getFavoriteNames(context);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_favorites_grid);
            views.setTextViewText(R.id.widget_fav_header, "TC RADIOS Favorites");

            for (int i = 0; i < 4; i++) {
                String title = i < names.size() ? names.get(i) : "Add favorite";
                views.setTextViewText(CELL_IDS[i], title);

                Intent playIntent = new Intent(context, AndroidAutoMediaService.class);
                playIntent.setAction(AndroidAutoMediaService.ACTION_PLAY_FAVORITE);
                playIntent.putExtra(AndroidAutoMediaService.EXTRA_STATION_INDEX, i);
                PendingIntent pendingIntent = PendingIntent.getService(
                        context,
                        2000 + i,
                        playIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(CELL_IDS[i], pendingIntent);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
