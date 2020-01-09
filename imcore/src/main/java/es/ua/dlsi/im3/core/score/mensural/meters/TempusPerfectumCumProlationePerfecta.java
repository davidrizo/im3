package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

import java.util.List;

/**
 * Visually rendered as O  with a dot inside
 * 1 breve = 3 semibreves, 1 semibreve = 3 minim
 */
public class TempusPerfectumCumProlationePerfecta extends TimeSignatureMensural {
    public TempusPerfectumCumProlationePerfecta() {
        super(Perfection.imperfectum, Perfection.imperfectum, Perfection.perfectum, Perfection.perfectum);
    }

    @Override
    public Time getDuration() {
        return getBreveDuration();
    }

    @Override
    public SignTimeSignature clone() {
        return new TempusPerfectumCumProlationePerfecta();
    }

    @Override
    public String getSignString() {
        return "O.";
    }

    @Override
    public void applyImperfectionRules(List<AtomFigure> figureList) {

    }

}
