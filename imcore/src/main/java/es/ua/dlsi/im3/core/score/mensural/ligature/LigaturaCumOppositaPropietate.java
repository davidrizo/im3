package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.score.SimpleLigature;

/**
 * See Willi Apel, page 90
 * @autor drizo
 */
public class LigaturaCumOppositaPropietate extends SimpleLigature {

    public LigaturaCumOppositaPropietate(ScientificPitch firstPitch, ScientificPitch secondPitch) {
        super(Figures.BREVE, 0, Figures.SEMIBREVE, firstPitch, Figures.SEMIBREVE, secondPitch);
    }

}
