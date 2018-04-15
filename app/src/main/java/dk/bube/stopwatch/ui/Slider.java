package dk.bube.stopwatch.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

public class Slider extends AppCompatSeekBar {

    private static final String TAG = "stopwatch.slider";

    private boolean seekerAtEnd = false;

    public Slider(Context context) {
        super(context);
    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Slider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnStateChangedListener(final OnStateChangedListener onStateChangedListener) {
        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i >= 80) {
                    if (!seekerAtEnd) {
                        Log.d(TAG, "at end " + i);
                        seekerAtEnd = true;
                        onStateChangedListener.onStateChanged(seekerAtEnd);
                    }
                } else if (i <= 20) {
                    if (seekerAtEnd){
                        Log.d(TAG, "not at end " + i);
                        seekerAtEnd = false;
                        onStateChangedListener.onStateChanged(seekerAtEnd);
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                animateToPosition(seekBar, seekerAtEnd);
            }
        });
    }

    private void animateToPosition(SeekBar seekBar, boolean toEnd) {
        int position = toEnd ? 100 : 0;
        ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, "progress", position);
        animation.setDuration(500); // 0.5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public abstract static class OnStateChangedListener {
        public abstract void onStateChanged(boolean atEnd);
    }
}
