package es.ua.dlsi.im3.omr.transduction;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.NotationType;

public class SemanticToScoreSongTransducerFactory {
    private static SemanticToScoreSongTransducerFactory ourInstance = new SemanticToScoreSongTransducerFactory();

    public static SemanticToScoreSongTransducerFactory getInstance() {
        return ourInstance;
    }

    private SemanticToScoreSongTransducerFactory() {
    }

    public ISemanticToScoreSongTransducer create(NotationType notationType) {
        if (notationType == NotationType.eMensural) {
            return new SemanticToMensuralSongTransducer();
        } else {
            throw new IM3RuntimeException("Notation " + notationType + " not implemented");
        }
    }
}
