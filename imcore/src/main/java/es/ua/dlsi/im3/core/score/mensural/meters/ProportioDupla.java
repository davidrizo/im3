package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

import java.util.List;

public class ProportioDupla extends TimeSignatureMensural {
    public ProportioDupla() {
        super(Perfection.imperfectum, Perfection.imperfectum);
    }

    @Override
    public void applyImperfectionRules(List<AtomFigure> figureList)  {

    }

    @Override
    public SignTimeSignature clone() {
        return new ProportioDupla();
    }

    @Override
    public String getSignString() {
        return "2";
    }

    @Override
    public Time getDuration() {
        return getSemibreveDuration(); //TODO Comprobar
    }
}