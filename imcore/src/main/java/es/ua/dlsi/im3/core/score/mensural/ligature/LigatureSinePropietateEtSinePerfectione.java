package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.score.*;

/**
 * See https://ugc.futurelearn.com/uploads/assets/a9/35/a9350066-e043-4c59-aadc-33300666fe98.jpg
 * @author drizo
 *
 */
public class LigatureSinePropietateEtSinePerfectione extends Ligature {

	public LigatureSinePropietateEtSinePerfectione(ScientificPitch firstPitch, int dotsFirstFigure, ScientificPitch secondPitch, int dotsSecondFigure, LigatureType ligatureType) {
		//super(Figures.LONGA, 0, Figures.BREVE, dotsFirstFigure, firstPitch, Figures.BREVE, dotsSecondFigure, secondPitch);
		super(ligatureType);
		addSubatom(new SimpleNote(Figures.LONGA, dotsFirstFigure, firstPitch));
		addSubatom(new SimpleNote(Figures.BREVE, dotsSecondFigure, secondPitch));

	}

}
