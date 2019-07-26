package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.score.*;

/**
 * See Willi Apel, page 90
 * @autor drizo
 */
public class LigaturaCumOppositaPropietate extends Ligature {

    /*public LigaturaCumOppositaPropietate(ScientificPitch firstPitch, int dotsFirstFigure, ScientificPitch secondPitch, int dotsSecondFigure) {
        super(Figures.BREVE, 0, Figures.SEMIBREVE, dotsFirstFigure, firstPitch, Figures.SEMIBREVE, dotsSecondFigure, secondPitch);
    }*/

    public LigaturaCumOppositaPropietate(ScientificPitch firstPitch, int dotsFirstFigure, ScientificPitch secondPitch, int dotsSecondFigure, LigatureType ligatureType) {
        super(ligatureType);
        addSubatom(new SimpleNote(Figures.SEMIBREVE, dotsFirstFigure, firstPitch));
        addSubatom(new SimpleNote(Figures.SEMIBREVE, dotsSecondFigure, secondPitch));
    }

}
