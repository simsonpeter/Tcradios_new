package com.jayathasoft.tcradios.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Receives tcradios://sync?... payloads from the PWA and stores them for
 * Android Auto + home screen widgets.
 */
public class NativeSyncActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = getIntent();
            Uri data = intent != null ? intent.getData() : null;
            String payload = null;
            if (data != null) {
                payload = data.getQueryParameter("data");
                if (TextUtils.isEmpty(payload)) {
                    payload = data.getQueryParameter("payload");
                }
            }
            if (TextUtils.isEmpty(payload) && intent != null) {
                payload = intent.getStringExtra("data");
            }
            if (!TextUtils.isEmpty(payload)) {
                StationSyncStore.applySyncPayload(this, payload);
            }
        } catch (Exception error) {
            Toast.makeText(this, "Could not sync library to Android Auto", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
