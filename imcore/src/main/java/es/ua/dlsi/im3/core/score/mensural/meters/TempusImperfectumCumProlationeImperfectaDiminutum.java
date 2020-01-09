package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

import java.util.List;

//TODO Duraciones
/**
 * Visually rendered as cut time (see https://en.wikipedia.org/wiki/Mensural_notation, unicode U+1D1CD)
 * 1 breve = 2 semibreves, 1 semibreve = 2 minim
 */
public class TempusImperfectumCumProlationeImperfectaDiminutum extends TimeSignatureMensural {
    public TempusImperfectumCumProlationeImperfectaDiminutum() {
        super(Perfection.imperfectum, Perfection.imperfectum, Perfection.imperfectum, Perfection.imperfectum);
    }

    @Override
    public Time getDuration() {
        return getBreveDuration();
    }

    @Override
    public SignTimeSignature clone() {
        return new TempusImperfectumCumProlationeImperfectaDiminutum();
    }

    @Override
    public String getSignString() {
        return "C|";
    }

    @Override
    public void applyImperfectionRules(List<AtomFigure> figureList) {

    }

}
