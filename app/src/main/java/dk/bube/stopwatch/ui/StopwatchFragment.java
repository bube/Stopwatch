package dk.bube.stopwatch.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import dk.bube.stopwatch.R;
import dk.bube.stopwatch.model.Lap;
import dk.bube.stopwatch.model.Stopwatch;

public class StopwatchFragment extends Fragment {

    private static final String TIME_DISPLAY_FORMAT_TENTHS = "%02d:%02d.%d";

    private static final String TIME_DISPLAY_FORMAT_HUNDREDS = "%02d:%02d.%02d";

    private Stopwatch stopwatch = new Stopwatch();

    private TimerDisplayUpdater timerDisplayUpdater;

    private final Handler handler = new Handler();
    private TextView runningLapTimeTextView;
    private TextView elapsedTimeTextView;
    private LinearLayout lapsLayout;
    private Button startStopButton;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState = stopwatch.addStateToBundle(outState);
        handler.removeCallbacks(timerDisplayUpdater);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stopwatch_view, container, false);
        runningLapTimeTextView = view.findViewById(R.id.lapTextView);
        elapsedTimeTextView = view.findViewById(R.id.elapsedTextView);
        lapsLayout = view.findViewById(R.id.lapsLayout);
        startStopButton = view.findViewById(R.id.startStopButton);

        timerDisplayUpdater = new TimerDisplayUpdater(stopwatch, handler, new TimerDisplayUpdater.UpdaterCallback() {
            @Override
            void onUpdateTimerDisplay(long runningLapTime, long elapsedTime) {
                updateTimerDisplays(runningLapTime, elapsedTime, TimerPrecision.TENTHS);
            }
        });

        Button startStopButton = view.findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean running = stopwatch.isRunning();
                if (!running) {
                    startButtonPressed();
                }
            }
        });

        startStopButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                boolean running = stopwatch.isRunning();
                if (running) {
                    stopButtonPressed();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            stopwatch = restoreStopWatchState(savedInstanceState);
            restoreViewState(stopwatch.getLaps(), stopwatch.isRunning());
        }
        startUpdatingTimerDisplay();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        startUpdatingTimerDisplay();
        super.onResume();
    }

    public boolean isRunning() {
        return stopwatch.isRunning();
    }

    public void startButtonPressed() {
        stopwatch.start();
        startUpdatingTimerDisplay();
        setStartStopButtonText(stopwatch.isRunning());
    }

    public void stopButtonPressed() {
        if (stopwatch.isRunning()) {
            stopwatch.stop();
            clearPendingTimerDisplayUpdates();
            Lap lap = stopwatch.recordLap();
            addLapToLapsView(lap);
            insertDividerInLapsView();
            updateTimerDisplays(lap);
            setStartStopButtonText(stopwatch.isRunning());
        }
    }

    public void lapButtonPressed() {
        Lap lap = stopwatch.recordLap();
        addLapToLapsView(lap);
    }

    private Stopwatch restoreStopWatchState(Bundle savedInstanceState) {
        return stopwatch.restoreStateFromBundle(savedInstanceState);

    }

    private void restoreViewState(ArrayList<Lap> laps, boolean running) {
        for (Lap lap : laps) {
            addLapToLapsView(lap);
            if ((lap).isStopped()) {
                insertDividerInLapsView();
            }
        }
        setStartStopButtonText(running);
    }

    private void setStartStopButtonText(boolean timerRunning) {
        if (timerRunning) {
            startStopButton.setText(R.string.stop);
        } else {
            startStopButton.setText(R.string.start);
        }

    }

    private void clearPendingTimerDisplayUpdates() {
        handler.removeCallbacks(timerDisplayUpdater);
    }

    private void startUpdatingTimerDisplay() {
        clearPendingTimerDisplayUpdates();
        if (stopwatch.isRunning()) {
            handler.postDelayed(timerDisplayUpdater, 0);
        }
    }


    private void addLapToLapsView(Lap lap) {
        View lapTextView = createLapTextView(lap);
        lapsLayout.addView(lapTextView, 0);
    }

    private void insertDividerInLapsView() {
        View divider = getActivity().getLayoutInflater().inflate(R.layout.divider, lapsLayout, false);
        lapsLayout.addView(divider, 0);
    }

    private View createLapTextView(Lap lap) {
        View lapView = getActivity().getLayoutInflater().inflate(R.layout.lap_view, lapsLayout, false);
        TextView lapNumberTextView = lapView.findViewById(R.id.lapNumberTextView);
        lapNumberTextView.setText(String.valueOf(lap.getNumberInSeries()));
        TextView lapTextView = lapView.findViewById(R.id.lapTextView);
        lapTextView.setText(getTimeString(lap.getLap(), TimerPrecision.HUNDREDS));
        TextView splitTextView = lapView.findViewById(R.id.splitTextView);
        splitTextView.setText(getTimeString(lap.getSplit(), TimerPrecision.HUNDREDS));
        return lapView;
    }

    private void updateTimerDisplays(Lap lap) {
        updateTimerDisplays(lap.getLap(), lap.getSplit(), TimerPrecision.HUNDREDS);
    }

    private void updateTimerDisplays(long runningLapTime, long elapsedTime, TimerPrecision precision) {
        String runningLapTimeString = getTimeString(runningLapTime, precision);
        String elapsedTimeString = getTimeString(elapsedTime, precision);
        runningLapTimeTextView.setText(runningLapTimeString);
        elapsedTimeTextView.setText(elapsedTimeString);
    }

    private String getTimeString(long timeMillis, TimerPrecision precision) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) % TimeUnit.MINUTES.toSeconds(1);
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(timeMillis) % TimeUnit.SECONDS.toMillis(1);
        long dotValue = milliseconds / precision.getFactor();
        return String.format(precision == TimerPrecision.HUNDREDS ? TIME_DISPLAY_FORMAT_HUNDREDS : TIME_DISPLAY_FORMAT_TENTHS,
                minutes,
                seconds,
                dotValue);
    }

    static class TimerDisplayUpdater implements Runnable {
        private final Stopwatch stopwatch;
        private final Handler handler;
        private final UpdaterCallback callback;

        private TimerDisplayUpdater(Stopwatch stopwatch, Handler handler, UpdaterCallback callback) {
            this.stopwatch = stopwatch;
            this.handler = handler;
            this.callback = callback;
        }

        @Override
        public void run() {
            long elapsedTime = stopwatch.getElapsedTime();
            long runningLapTime = stopwatch.getRunningLapTime(elapsedTime);
            callback.onUpdateTimerDisplay(runningLapTime, elapsedTime);
            handler.postDelayed(this, 100);
        }

        private static abstract class UpdaterCallback {
            abstract void onUpdateTimerDisplay(long runningLapTime, long elapsedTime);
        }
    }

    private enum TimerPrecision {
        TENTHS(100), HUNDREDS(10);

        private final int factor;

        TimerPrecision(int factor) {
            this.factor = factor;
        }

        int getFactor() {
            return factor;
        }
    }

}
