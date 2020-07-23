package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

import java.util.List;

/**
 * Visually rendered as C
 * 1 breve = 2 semibreves, 1 semibreve = 2 minim
 */
public class TempusImperfectumCumProlationeImperfecta extends TimeSignatureMensural {
    public TempusImperfectumCumProlationeImperfecta() {
        super(Perfection.imperfectum, Perfection.imperfectum, Perfection.imperfectum, Perfection.imperfectum);
    }

    @Override
    public Time getDuration() {
        return getBreveDuration();
    }

    @Override
    public SignTimeSignature clone() {
        TempusImperfectumCumProlationeImperfecta timeSignatureMensural = new TempusImperfectumCumProlationeImperfecta();
        timeSignatureMensural.cloneValues(this);
        return this;
    }

    @Override
    public String getSignString() {
        return "C";
    }

    @Override
    public void applyImperfectionRules(List<AtomFigure> figureList) {

    }

}
