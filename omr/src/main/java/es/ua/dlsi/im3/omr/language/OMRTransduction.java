package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.Transduction;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.apache.commons.math3.fraction.BigFraction;

public class OMRTransduction extends Transduction {
    ScoreSong song;
    Staff staff; // TODO: 4/10/17 Deberíamos quizás pasarle el staff con el que estamos 

    public OMRTransduction(BigFraction initialProbability, NotationType notationType) throws IM3Exception {
        super(initialProbability);
        song = new ScoreSong();
        staff = new Pentagram(song, "1", 1); // TODO: 4/10/17 Esto no debe estar aquí
        staff.setNotationType(notationType);
        song.addStaff(staff);
    }

    public ScoreSong getSong() {
        return song;
    }

    public Staff getStaff() {
        return staff;
    }
}
