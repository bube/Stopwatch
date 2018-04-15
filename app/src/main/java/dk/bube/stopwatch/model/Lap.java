package dk.bube.stopwatch.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Lap implements Parcelable {
    private final int numberInSeries;
    private final long split;
    private final long lap;
    private final boolean stopped;

    public Lap(int numberInSeries, long split, long lap, boolean stopped) {
        this.numberInSeries = numberInSeries;
        this.split = split;
        this.lap = lap;
        this.stopped = stopped;
    }

    private Lap(Parcel in) {
        numberInSeries = in.readInt();
        split = in.readLong();
        lap = in.readLong();
        stopped = (boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Lap> CREATOR = new Creator<Lap>() {
        @Override
        public Lap createFromParcel(Parcel in) {
            return new Lap(in);
        }

        @Override
        public Lap[] newArray(int size) {
            return new Lap[size];
        }
    };

    public int getNumberInSeries() {
        return numberInSeries;
    }

    public long getSplit() {
        return split;
    }

    public long getLap() {
        return lap;
    }

    public boolean isStopped() {
        return stopped;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(numberInSeries);
        parcel.writeLong(split);
        parcel.writeLong(lap);
        parcel.writeValue(stopped);
    }
}
