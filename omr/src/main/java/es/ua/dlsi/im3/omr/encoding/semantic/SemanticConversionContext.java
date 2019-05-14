package es.ua.dlsi.im3.omr.encoding.semantic;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.KeySignature;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.TimeSignature;

import java.util.ArrayList;
import java.util.List;

/**
 * Information required to convert to IM3 IMCore
 */
public class SemanticConversionContext {
    NotationType notationType;
    KeySignature currentKeySignature;
    TimeSignature currentTimeSignature;
    int pendingTies; //TODO - acordes - no podemos usar el pitch con un map porque en manuscrito a veces no están bien alineados
    /**
     * This can also be used to tie from previous systems
     */
    List<AtomPitch> pendingPitchesToTie;

    public SemanticConversionContext(NotationType notationType) {
        this.notationType = notationType;
        this.pendingTies = 0;
        this.pendingPitchesToTie = new ArrayList<>();
    }

    public List<AtomPitch> getPendingPitchesToTie() {
        return pendingPitchesToTie;
    }

    public KeySignature getCurrentKeySignature() {
        return currentKeySignature;
    }

    public void setCurrentKeySignature(KeySignature currentKeySignature) {
        this.currentKeySignature = currentKeySignature;
    }

    public TimeSignature getCurrentTimeSignature() {
        return currentTimeSignature;
    }

    public void setCurrentTimeSignature(TimeSignature currentTimeSignature) {
        this.currentTimeSignature = currentTimeSignature;
    }

    public NotationType getNotationType() {
        return notationType;
    }

    public boolean hasPendingTie() {
        return pendingTies > 0;
    }

    public void removePendingTie() throws IM3Exception {
        if (!hasPendingTie()) {
            throw new IM3Exception("Cannot remove non pending ties");
        }
        pendingTies--;
    }

    public void addPendingTie() {
        pendingTies++;
    }
}
