package es.ua.dlsi.im3.analysis.hierarchical.motives;

import es.ua.dlsi.im3.core.score.AtomFigure;

import java.util.ArrayList;

public class MelodicMotive extends Motive {
	ArrayList<AtomFigure> atomFigures;
	
	
	public MelodicMotive() {
        atomFigures = new ArrayList<>();
	}

	public ArrayList<AtomFigure> getAtomFigures() {
		return atomFigures;
	}

	public void setAtomFigures(ArrayList<AtomFigure> atomFigures) {
		this.atomFigures = atomFigures;
	}
	

	@Override
	public String toString() {
		return "MelodicMotive [atomFigures=" + atomFigures + "]";
	}

	
}
