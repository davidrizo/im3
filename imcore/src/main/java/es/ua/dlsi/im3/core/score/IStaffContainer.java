package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.List;

public interface IStaffContainer {
    List<Staff> getStaves();
    PageSystemBeginnings getPageSystemBeginnings();

    /**
     * It returns the common instrumentKey for all staves at that time. For transposing instruments the
     * concert pitch key will be used.
     * @param time
     * @return
     */
    default Key getUniqueKeyActiveAtTime(Time time) throws IM3Exception {
        Key key = null;
        for (Staff staff: getStaves()) {
            KeySignature ks = staff.getRunningKeySignatureAt(time);
            if (key == null && ks != null) {
                key = ks.getConcertPitchKey();
            } else if (ks != null && !ks.getConcertPitchKey().equals(key)) {
                throw new IM3Exception("Two different concert pitch keys in different staves: " + key + " and " + ks.getConcertPitchKey() + " in time " + time);
            } // else it is the same
        }
        return key;
    }

    /**
     * It returns the common instrumentKey for all staves at that time. For transposing instruments the
     * concert pitch key will be used.
     * @param time
     * @return null if not found
     * @exception IM3Exception If several concert keys are found for the same time in different staves
     */
    default Key getUniqueKeyWithOnset(Time time) throws IM3Exception {
        Key key = null;
        for (Staff staff: getStaves()) {
            KeySignature ks = staff.getKeySignatureWithOnset(time);
            if (key == null && ks != null) {
                key = ks.getConcertPitchKey();
            } else if (ks != null && !ks.getConcertPitchKey().equals(key)) {
                throw new IM3Exception("Two different concert pitch keys in different staves: " + key + " and " + ks.getConcertPitchKey() + " in time " + time);
            } // else it is the same
        }
        return key;
    }

    /**
     * It returns the common time signature for all staves at that time when possible
     * @param time
     * @return null if not found
     * @exception IM3Exception If several time signatures are found for the same time in different staves
     */
    default TimeSignature getUniqueMeterWithOnset(Time time) throws IM3Exception {
        TimeSignature meter = null;
        for (Staff staff: getStaves()) {
            TimeSignature ts = staff.getTimeSignatureWithOnset(time);
            if (meter == null && ts != null) {
                meter = ts;
            } else if (ts != null && !ts.equals(meter)) {
                throw new IM3Exception("Two different meters in different staves: " + meter + " and " + ts + " in time " + time);
            } // else it is the same
        }
        return meter;
    }
}
