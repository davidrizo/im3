package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.score.LigaturaBinaria;

/**
 * See https://ugc.futurelearn.com/uploads/assets/a9/35/a9350066-e043-4c59-aadc-33300666fe98.jpg
 * @author drizo
 *
 */
public class LigatureCumPropietateEtCumPerfectione extends LigaturaBinaria {

	public LigatureCumPropietateEtCumPerfectione(ScientificPitch firstPitch, int dotsFirstFigure, ScientificPitch secondPitch, int dotsSecondFigure) {
		super(Figures.LONGA, 1, Figures.BREVE, dotsFirstFigure, firstPitch, Figures.LONGA, dotsSecondFigure, secondPitch);
	}

}
