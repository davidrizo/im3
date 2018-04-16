package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.score.LigaturaBinaria;

/**
 * See Willi Apel, page 90
 * @autor drizo
 */
public class LigaturaCumOppositaPropietate extends LigaturaBinaria {

    public LigaturaCumOppositaPropietate(ScientificPitch firstPitch, int dotsFirstFigure, ScientificPitch secondPitch, int dotsSecondFigure) {
        super(Figures.BREVE, 0, Figures.SEMIBREVE, dotsFirstFigure, firstPitch, Figures.SEMIBREVE, dotsSecondFigure, secondPitch);
    }

}
