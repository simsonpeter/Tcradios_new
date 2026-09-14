package com.jayathasoft.tcradios.wear;

import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

final class FaIconHelper {
  static final char PLAY = '\uf04b';
  static final char PAUSE = '\uf04c';
  static final char PREVIOUS = '\uf048';
  static final char NEXT = '\uf051';
  static final char BACK = '\uf060';
  static final char BROWSE = '\uf03a';
  static final char VOLUME_HIGH = '\uf028';
  static final char VOLUME_MUTE = '\uf6a9';
  static final char VOLUME_UP = '\uf077';
  static final char VOLUME_DOWN = '\uf078';

  private static android.graphics.Typeface typeface;

  private FaIconHelper() {}

  static void setIcon(TextView view, char icon, float sizeSp) {
    try {
      if (typeface == null) {
        typeface = ResourcesCompat.getFont(view.getContext(), R.font.fa_solid);
      }
      if (typeface != null) {
        view.setTypeface(typeface);
      }
    } catch (Exception ignored) {
      // Keep default typeface if Font Awesome fails to load.
    }
    view.setText(String.valueOf(icon));
    view.setTextSize(sizeSp);
    view.setIncludeFontPadding(false);
  }
}
