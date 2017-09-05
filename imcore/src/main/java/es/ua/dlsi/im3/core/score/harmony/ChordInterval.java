package es.ua.dlsi.im3.core.score.harmony;

/**
 * Created by drizo on 20/6/17.
 */
public class ChordInterval {
    ChordIntervalQuality quality;
    int interval;

    public ChordIntervalQuality getQuality() {
        return quality;
    }

    public void setQuality(ChordIntervalQuality quality) {
        this.quality = quality;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "ChordInterval{" +
                "quality=" + quality +
                ", interval=" + interval +
                '}';
    }
}
