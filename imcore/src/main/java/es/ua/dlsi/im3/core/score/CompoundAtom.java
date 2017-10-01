package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;

public class CompoundAtom extends Atom {
	private List<Atom> subatoms;
	
	public CompoundAtom() {
	    subatoms = new ArrayList<>();
	}
	public void addSubatom(Atom subatom) throws IM3Exception {
		this.subatoms.add(subatom);
		subatom.setParentAtom(this);
        //1-Oct
        Time currentDuration = this.getDuration();
        /*
        for (AtomFigure atomFigure: subatom.getAtomFigures()) {
            atomFigure.setRelativeOnset(currentDuration);
        }*/
        subatom.setTime(currentDuration);


		addDuration(subatom.getDuration());
	}
	
	public void removeSubatom(SingleFigureAtom atom) {
		this.subatoms.remove(atom);
		substractDuration(atom.getDuration());
		atom.setParentAtom(null);
	}	
	
	//TODO Ver si lo podemos mantener en una estructura interna (usando dirty flags) para no tenerlo que recalcular cada vez	
	public List<PlayedScoreNote> computePlayedNotes() throws IM3Exception {
		ArrayList<PlayedScoreNote> result = new ArrayList<>();
		if (subatoms != null) {
			for (Atom sa: subatoms) {
				List<PlayedScoreNote> pn = sa.computePlayedNotes();
				if (pn != null) {
					result.addAll(pn); //TODO ¿creamos menos objetos con iteradores?
				}
			}
		}
		return result;
	}
	@Override
	public List<AtomPitch> getAtomPitches() {
		ArrayList<AtomPitch> result = new ArrayList<>();
		if (subatoms != null) {
			for (Atom sa: subatoms) {
				List<AtomPitch> aps = sa.getAtomPitches();
				if (aps != null) {
					result.addAll(aps); //TODO ¿creamos menos objetos con iteradores?
				}
			}
		}
		return result;
	}
	@Override
	public List<AtomFigure> getAtomFigures() {
		ArrayList<AtomFigure> result = new ArrayList<>();
		if (subatoms != null) {
			for (Atom sa: subatoms) {
				result.addAll(sa.getAtomFigures()); //TODO ¿creamos menos objetos con iteradores?
			}
		}
		return result;
	}
	@Override
	public List<Atom> getAtoms() {
		ArrayList<Atom> result = new ArrayList<>();
		if (subatoms != null) {
			for (Atom sa: subatoms) {
				result.addAll(sa.getAtoms()); //TODO ¿creamos menos objetos con iteradores?
			}
		}
		return result;
	}
	@Override
	public String toString() {
		return super.toString() + ", subatoms=" + subatoms;
	}
	

}
