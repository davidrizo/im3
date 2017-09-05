package es.ua.dlsi.im3.core.played;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.TimedElementCollection;
import es.ua.dlsi.im3.core.score.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Measures {
    TreeMap<Long, Measure> measures;

    public Measures() {
        measures = new TreeMap<>();
    }

    public void add(Measure m) throws IM3Exception {
        measures.put(m.getTime(), m);
    }

    public Measure getMeasureAtTime(long time) throws IM3Exception {
        Map.Entry<Long, Measure> measureEntry = measures.floorEntry(time);
        if (measureEntry == null) {
            throw new IM3Exception("Cannot find a meter at time " + time);
        }
        Measure measure = measureEntry.getValue();
        if (time > measure.getEndTime()) {
            throw new IM3Exception("The time (" + time + ") is higher than the end time of the measure (" + measure.getEndTime()+ ")");
        }
        return measure;
    }

    /**
     * First measure has number 0
     * @return
     */
    public Collection<Measure> getMeasures() {
        return measures.values();
    }
}
