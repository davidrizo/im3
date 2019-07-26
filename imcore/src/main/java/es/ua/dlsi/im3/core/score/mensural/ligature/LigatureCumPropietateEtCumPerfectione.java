package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.score.*;

/**
 * See https://ugc.futurelearn.com/uploads/assets/a9/35/a9350066-e043-4c59-aadc-33300666fe98.jpg
 * @author drizo
 *
 */
public class LigatureCumPropietateEtCumPerfectione extends Ligature {
	/**
	 * Both note heads are on the same x position
	 */
	boolean stacked = false;

	public LigatureCumPropietateEtCumPerfectione(ScientificPitch firstPitch, int dotsFirstFigure, ScientificPitch secondPitch, int dotsSecondFigure) {
		//super(Figures.LONGA, 1, Figures.BREVE, dotsFirstFigure, firstPitch, Figures.LONGA, dotsSecondFigure, secondPitch);
		super(LigatureType.recta);
		addSubatom(new SimpleNote(Figures.BREVE, dotsFirstFigure, firstPitch));
		addSubatom(new SimpleNote(Figures.LONGA, dotsSecondFigure, secondPitch));

	}

	public boolean isStacked() {
		return stacked;
	}

	public void setStacked(boolean stacked) {
		this.stacked = stacked;
	}
}
