package dk.bube.stopwatch;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import dk.bube.stopwatch.ui.Slider;
import dk.bube.stopwatch.ui.StopwatchFragment;

public class StopwatchActivity extends AppCompatActivity {

    private static final String TAG = "stopwatch.main";

    private StopwatchFragment stopwatchFragment;

    private boolean hijackActivityManager = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_launcher);

        stopwatchFragment = (StopwatchFragment) getFragmentManager().findFragmentById(R.id.stopwatchFragment);

        final Slider stopSlider = findViewById(R.id.stopSwitch);

        stopSlider.setOnStateChangedListener(new Slider.OnStateChangedListener() {

            @Override
            public void onStateChanged(boolean atEnd) {
                if (atEnd) {
                    unlockAppSwitcher();
                    disableHomeActivityMode();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (stopwatchFragment.isRunning()) {
                    stopwatchFragment.lapButtonPressed();
                } else {
                    stopwatchFragment.startButtonPressed();
                }
                return true;
        }
        return false;
    }

    private void unlockAppSwitcher() {
        hijackActivityManager = false;
    }

    private void disableHomeActivityMode() {
        Log.d(TAG, "disabling home activity mode");
        setComponentEnabledSetting(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, StopwatchActivity.class);
    }

    private void setComponentEnabledSetting(int componentEnabledStateDisabled, Class klass) {
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(getApplicationContext(), klass);
        packageManager.setComponentEnabledSetting(componentName, componentEnabledStateDisabled, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hijackActivityManager) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            assert activityManager != null;
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.d(TAG, "suppressing key " + KeyEvent.keyCodeToString(keyCode));
                return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
