package com.jayathasoft.tcradios.wear;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class ArtworkLoader {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private ArtworkLoader() {}

    static void load(ImageView imageView, Uri uri, int placeholderResId) {
        imageView.setTag(uri);
        imageView.setImageResource(placeholderResId);
        if (uri == null || TextUtils.isEmpty(uri.toString())) {
            return;
        }

        EXECUTOR.execute(() -> {
            Bitmap bitmap = fetchBitmap(uri.toString());
            if (bitmap == null) return;
            imageView.post(() -> {
                Object tag = imageView.getTag();
                if (tag != null && uri.equals(tag)) {
                    WearAnim.crossfadeImage(imageView, () -> imageView.setImageBitmap(bitmap));
                }
            });
        });
    }

    private static Bitmap fetchBitmap(String urlString) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (InputStream inputStream = connection.getInputStream()) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
