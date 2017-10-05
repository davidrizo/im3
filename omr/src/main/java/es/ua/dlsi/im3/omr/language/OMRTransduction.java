package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.Transduction;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.apache.commons.math3.fraction.BigFraction;

public class OMRTransduction extends Transduction {
    private final ScoreLayer layer;
    private final ScorePart part;
    ScoreSong song;
    Staff staff; // TODO: 4/10/17 Deberíamos quizás pasarle el staff con el que estamos 

    public OMRTransduction(BigFraction initialProbability, NotationType notationType) throws IM3Exception {
        super(initialProbability);
        song = new ScoreSong();
        part = song.addPart();
        staff = new Pentagram(song, "1", 1); // TODO: 4/10/17 Esto no debe estar aquí
        staff.setNotationType(notationType);
        song.addStaff(staff);
        layer = part.addScoreLayer(staff);
        staff.addLayer(layer);
    }

    public ScoreSong getSong() {
        return song;
    }

    public Staff getStaff() {
        return staff;
    }

    public ScoreLayer getLayer() {
        return layer;
    }
}
