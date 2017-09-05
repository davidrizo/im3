package es.ua.dlsi.im3.core.played;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.Time;
import org.apache.commons.lang3.math.Fraction;

public class Measure {
    private final int number;
    private final long time;
    private long duration;

    public Measure(int number, long time, long duration) {
        this.number = number;
        this.time = time;
        this.duration = duration;
    }

    /**
     * Starting from 0
     * @return
     */
    public int getNumber() {
        return number;
    }

    public long getDuration() {
        return duration;
    }

    public long getTime() {
        return time;
    }

    public long getEndTime() {
        return time+duration;
    }

    //TODO Test unitario
    public int getBeat(long atime, int resolution) {
        if (atime < this.time) {
            throw new IM3RuntimeException("Time (" + atime + ") less than measure time (" + this.time + ")");
        }
        return (int) ((atime - time) / resolution);
    }

    /**
     *
     * @param atime
     * @param resolution
     * @param beatSubdivisions If two, we are working at 8th resolution, if beatSubdivisions is 4, we are working at 16th
     * @return
     */
    /*TODO public int getSubBeat(long atime, int resolution, int beatSubdivisions) {
        if (atime < this.time) {
            throw new IM3RuntimeException("Time (" + atime + ") less than measure time (" + this.time + ")");
        }
        int beat = (int) ((atime - time) / resolution);
        int subbeat
    }*/

}
