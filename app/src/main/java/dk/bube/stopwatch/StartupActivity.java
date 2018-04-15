package dk.bube.stopwatch;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartupActivity extends AppCompatActivity {

    private final static String TAG = "stopwatch.starter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        startLauncher();

        Button startLauncherButton = findViewById(R.id.startLauncherButton);

        startLauncherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLauncher();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLauncher();
    }

    private void startLauncher() {
        enableTimerLauncherActivity();
        Intent startLauncher = new Intent(Intent.ACTION_MAIN);
        startLauncher.addCategory(Intent.CATEGORY_HOME);
        startLauncher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLauncher);
    }

    private void enableTimerLauncherActivity() {
        Log.d(TAG, "enabling home mode");
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(getApplicationContext(), StopwatchActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
