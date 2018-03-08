package es.ua.dlsi.im3.omr.muret.model;

import java.util.Set;
import java.util.TreeSet;

public class OMRInstruments {
    Set<OMRInstrument> instrumentSet;

    public OMRInstruments() {
        instrumentSet = new TreeSet<>();
    }

    public Set<OMRInstrument> getInstrumentSet() {
        return instrumentSet;
    }

    public void addInstrument(OMRInstrument instrument) {
        instrumentSet.add(instrument);
    }

    public void removeInstrument(OMRInstrument instrument) {
        instrumentSet.remove(instrument);
    }
}
