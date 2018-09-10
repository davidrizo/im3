package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;
import java.util.TreeSet;

/**
 * Used to avoid adding several times the same instrument name with a slight difference
 */
public class OMRInstruments {
    ObservableSet<OMRInstrument> instrumentSet;

    public OMRInstruments() {
        instrumentSet = FXCollections.observableSet(new TreeSet<>());
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

    public OMRInstrument addInstrument(String name) {
        for (OMRInstrument instrument: instrumentSet) { // the size is tiny
            if (instrument.getName().equals(name)) {
                return instrument;
            }
        }
        // not exist
        OMRInstrument instrument = new OMRInstrument(name);
        instrumentSet.add(instrument);
        return instrument;
    }

    /**
     *
     * @param name
     * @return
     */
    public OMRInstrument getInstrument(String name)  {
        for (OMRInstrument instrument: instrumentSet) { // the size is tiny
            if (instrument.getName().equals(name)) {
                return instrument;
            }
        }
        // not found, add it
        OMRInstrument omrInstrument = new OMRInstrument(name);
        instrumentSet.add(omrInstrument);
        return omrInstrument;
    }
}
