package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

import java.util.List;

public class ProportioTripla extends TimeSignatureMensural {
    public ProportioTripla() {
        super(Perfection.imperfectum, Perfection.imperfectum, Perfection.perfectum, Perfection.imperfectum);
    }

    @Override
    public void applyImperfectionRules(List<AtomFigure> figureList)  {

    }

    @Override
    public SignTimeSignature clone() {
        ProportioTripla timeSignatureMensural = new ProportioTripla();
        timeSignatureMensural.cloneValues(this);
        return this;
    }

    @Override
    public String getSignString() {
        return "3";
    }

    @Override
    public Time getDuration() {
        return getSemibreveDuration(); //TODO Comprobar
    }
}
