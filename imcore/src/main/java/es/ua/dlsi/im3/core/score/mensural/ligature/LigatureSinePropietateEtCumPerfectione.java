package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.LigaturaBinaria;
import es.ua.dlsi.im3.core.score.ScientificPitch;

/**
 * See https://ugc.futurelearn.com/uploads/assets/a9/35/a9350066-e043-4c59-aadc-33300666fe98.jpg
 * @author drizo
 *
 */
public class LigatureSinePropietateEtCumPerfectione extends LigaturaBinaria {

	public LigatureSinePropietateEtCumPerfectione(ScientificPitch firstPitch, int dotsFirstFigure, ScientificPitch secondPitch, int dotsSecondFigure) {
		super(Figures.MAXIMA, 0, Figures.LONGA, dotsFirstFigure, firstPitch, Figures.LONGA, dotsSecondFigure, secondPitch);
	}

}
