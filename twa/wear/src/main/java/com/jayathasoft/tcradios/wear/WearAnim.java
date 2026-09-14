package com.jayathasoft.tcradios.wear;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

final class WearAnim {
    static final long FAST = 140L;
    static final long NORMAL = 220L;

    private WearAnim() {}

    static void slideReplace(View outgoing, View incoming, boolean forward) {
        float distance = 28f * outgoing.getResources().getDisplayMetrics().density;
        float outTo = forward ? -distance : distance;
        float inFrom = forward ? distance : -distance;

        outgoing.animate().cancel();
        incoming.animate().cancel();

        incoming.setVisibility(View.VISIBLE);
        incoming.setAlpha(0f);
        incoming.setTranslationX(inFrom);

        outgoing.animate()
                .alpha(0f)
                .translationX(outTo)
                .setDuration(NORMAL)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    outgoing.setVisibility(View.GONE);
                    outgoing.setAlpha(1f);
                    outgoing.setTranslationX(0f);
                })
                .start();

        incoming.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(NORMAL)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    static void setVisible(View view, boolean visible) {
        view.animate().cancel();
        if (visible) {
            if (view.getVisibility() == View.VISIBLE && view.getAlpha() >= 0.99f) {
                return;
            }
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0f);
            view.animate().alpha(1f).setDuration(FAST).start();
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        view.animate()
                .alpha(0f)
                .setDuration(FAST)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1f);
                })
                .start();
    }

    static void bounce(View view) {
        view.animate().cancel();
        view.setScaleX(0.82f);
        view.setScaleY(0.82f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(220)
                .setInterpolator(new OvershootInterpolator(2.4f))
                .start();
    }

    static void fadeText(android.widget.TextView view, CharSequence text) {
        if (text == null) {
            text = "";
        }
        CharSequence current = view.getText();
        if (current != null && text.toString().contentEquals(current)) {
            return;
        }
        CharSequence nextText = text;
        view.animate().cancel();
        view.animate()
                .alpha(0f)
                .setDuration(90)
                .withEndAction(() -> {
                    view.setText(nextText);
                    view.animate().alpha(1f).setDuration(160).start();
                })
                .start();
    }

    static void showOverlay(View view) {
        view.animate().cancel();
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.setTranslationY(18f);
        view.setScaleX(0.94f);
        view.setScaleY(0.94f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(NORMAL)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    static void hideOverlay(View view) {
        view.animate().cancel();
        view.animate()
                .alpha(0f)
                .translationY(12f)
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(FAST)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1f);
                    view.setTranslationY(0f);
                    view.setScaleX(1f);
                    view.setScaleY(1f);
                })
                .start();
    }

    static ObjectAnimator spinning(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f);
        animator.setDuration(8000L);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    static void fadeTo(View view, float alpha) {
        view.animate().cancel();
        view.animate().alpha(alpha).setDuration(NORMAL).start();
    }

    static void crossfadeImage(View view, Runnable applyImage) {
        view.animate().cancel();
        view.animate()
                .alpha(0.2f)
                .setDuration(90)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        applyImage.run();
                        view.animate().setListener(null).alpha(1f).setDuration(200).start();
                    }
                })
                .start();
    }
}
