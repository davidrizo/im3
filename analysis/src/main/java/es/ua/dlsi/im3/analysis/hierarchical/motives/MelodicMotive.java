package es.ua.dlsi.im3.analysis.hierarchical.motives;

import es.ua.dlsi.im3.core.score.AtomFigure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MelodicMotive extends Motive {
	List<AtomFigure> atomFigures;
	
	
	public MelodicMotive() {
        atomFigures = new LinkedList<>();
	}

	public List<AtomFigure> getAtomFigures() {
		return atomFigures;
	}

	public void setAtomFigures(List<AtomFigure> atomFigures) {
		this.atomFigures = atomFigures;
	}
	

	@Override
	public String toString() {
		return "MelodicMotive [atomFigures=" + atomFigures + "]";
	}

	
}
