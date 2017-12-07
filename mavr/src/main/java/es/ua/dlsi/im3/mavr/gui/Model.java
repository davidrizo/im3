package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.mavr.model.Motive;

import java.util.LinkedList;
import java.util.List;

public class Model {
    ScoreSong scoreSong;

    public Model(ScoreSong scoreSong) {
        this.scoreSong = scoreSong;
    }

    public ScoreSong getScoreSong() {
        return scoreSong;
    }

    //TODO Devuelvo como motivo el pentagrama completo
    public List<Motive> getMotives() throws IM3Exception {
        LinkedList<Motive> result = new LinkedList<>();

        for (Staff staff: scoreSong.getStaves()) {
            List<Atom> atoms = staff.getAtoms();
            Motive motive = new Motive("Staff " + staff.getNumberIdentifier(), atoms);
            result.add(motive);
        }

        return result;
    }
}
