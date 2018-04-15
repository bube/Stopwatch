package dk.bube.stopwatch.model;

import android.os.Bundle;
import android.os.SystemClock;

import java.util.ArrayList;

public class Stopwatch {
    /* Bundle parameter names */
    private static final String BUNDLE_NAME_CURRENT_LAP_NUMBER_IN_SERIES = "currentLapNumberInSeries";
    private static final String BUNDLE_NAME_IS_RUNNING = "running";
    private static final String BUNDLE_NAME_START_TIME = "startTime";
    private static final String BUNDLE_NAME_PREVIOUS_SPLIT_TIME = "previousSplitTime";
    private static final String BUNDLE_NAME_LAPS = "laps";

    private long startTime;

    private ArrayList<Lap> laps;

    private long previousSplitTime;

    private boolean running;

    private int currentLapNumberInSeries;

    public Stopwatch() {
        this.currentLapNumberInSeries = 1;
        this.startTime = 0;
        this.previousSplitTime = 0;
        this.running = false;
        this.laps = new ArrayList<>();
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        previousSplitTime = 0;
        currentLapNumberInSeries = 1;
        running = true;
    }

    public void stop() {
        running = false;
    }

    public long getElapsedTime() {
        long uptime = SystemClock.uptimeMillis();
        return uptime - startTime;
    }

    public Lap recordLap() {
        long elapsedTime = getElapsedTime();
        long lapTime = elapsedTime - previousSplitTime;
        Lap lap = new Lap(currentLapNumberInSeries, elapsedTime, lapTime, !running);
        previousSplitTime = elapsedTime;
        currentLapNumberInSeries++;
        laps.add(lap);
        return lap;
    }

    public long getRunningLapTime(long elapsedTime) {
        return elapsedTime - previousSplitTime;
    }

    public ArrayList<Lap> getLaps() {
        return laps;
    }

    public boolean isRunning() {
        return running;
    }

    public Stopwatch restoreStateFromBundle(Bundle savedInstanceState) {
        currentLapNumberInSeries = savedInstanceState.getInt(BUNDLE_NAME_CURRENT_LAP_NUMBER_IN_SERIES);
        running = savedInstanceState.getBoolean(BUNDLE_NAME_IS_RUNNING);
        startTime = savedInstanceState.getLong(BUNDLE_NAME_START_TIME);
        previousSplitTime = savedInstanceState.getLong(BUNDLE_NAME_PREVIOUS_SPLIT_TIME);
        laps = savedInstanceState.getParcelableArrayList(BUNDLE_NAME_LAPS);
        return this;
    }

    public Bundle addStateToBundle(Bundle outState) {
        outState.putInt(BUNDLE_NAME_CURRENT_LAP_NUMBER_IN_SERIES, getCurrentLapNumberInSeries());
        outState.putBoolean(BUNDLE_NAME_IS_RUNNING, isRunning());
        outState.putLong(BUNDLE_NAME_START_TIME, getStartTime());
        outState.putLong(BUNDLE_NAME_PREVIOUS_SPLIT_TIME, getPreviousSplitTime());
        outState.putParcelableArrayList(BUNDLE_NAME_LAPS, getLaps());
        return outState;
    }

    private int getCurrentLapNumberInSeries() {
        return currentLapNumberInSeries;
    }


    private long getStartTime() {
        return startTime;
    }

    private long getPreviousSplitTime() {
        return previousSplitTime;
    }
}
