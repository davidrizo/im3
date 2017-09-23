package es.ua.dlsi.im3.core.score;


import java.util.ArrayList;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;


/**
 * @author drizo
 *
 */
public class SimpleTuplet extends CompoundAtom {	
	private Time wholeDuration;
	private Figures eachFigure;
	/**
	 * E.g. 3 in a triplet. Note that in a 8th triplet made of a quarter + eigth, tupletCardinality is 3, not 2
	 */
	private int cardinality;
	/**
	 * E.g. 2 in triplet
	 */
	private int inSpaceOfAtoms;

	/**
	 * @param pitches If a rest is required, use null in its position //TODO Probar esto
	 */
	public SimpleTuplet(int cardinality, int inSpaceOfAtoms, Figures eachFigure, ScientificPitch ...pitches) throws IM3Exception {
		this.cardinality = cardinality;
		this.inSpaceOfAtoms = inSpaceOfAtoms;
		
		wholeDuration = eachFigure.getDuration().multiplyBy(Fraction.getFraction(inSpaceOfAtoms, 1)); 
		this.eachFigure = eachFigure;
		
		Time eachNoteDuration = wholeDuration.divideBy(Fraction.getFraction(cardinality, 1));
		Time currentRelativeOnset = Time.TIME_ZERO;
		
		for (ScientificPitch scientificPitch : pitches) {
			SingleFigureAtom element;
			if (scientificPitch != null) {
				element = new SimpleNote(eachFigure, 0, eachNoteDuration, scientificPitch);
			} else {
				element = new SimpleRest(eachFigure, 0, eachNoteDuration);
			}
			element.setParentAtom(this);			
			element.setRelativeOnset(currentRelativeOnset);
			//element.setDuration(eachNoteDuration);
			element.setDuration(computeDuration(element, eachNoteDuration, eachFigure));
			addSubatom(element);
			currentRelativeOnset = currentRelativeOnset.add(element.getDuration());
		}

	}

	/**
	 * 
	 * @param cardinality
	 * @param inSpaceOfAtoms
	 * @param eachFigure
	 * @param chordPitches pitches[i] represents a chord, pitches[i][j] is the j_th pitch in i_th
	 */
	public SimpleTuplet(int cardinality, int inSpaceOfAtoms, Figures eachFigure, ScientificPitch [] ... chordPitches) throws IM3Exception {
		this.cardinality = cardinality;
		this.inSpaceOfAtoms = inSpaceOfAtoms;
		wholeDuration = eachFigure.getDuration().multiplyBy(Fraction.getFraction(inSpaceOfAtoms, 1)); 
		this.eachFigure = eachFigure;
		
		Time eachNoteDuration = wholeDuration.divideBy(Fraction.getFraction(cardinality, 1));
		Time currentRelativeOnset = Time.TIME_ZERO;
		
		for (ScientificPitch [] scientificPitches : chordPitches) {
			SingleFigureAtom element;
			if (scientificPitches == null || scientificPitches.length == 0) {
				element = new SimpleRest(eachFigure, 0, eachNoteDuration);
			} else if (scientificPitches.length == 1) {
				element = new SimpleNote(eachFigure, 0, eachNoteDuration, scientificPitches[0]);
			} else {
				element = new SimpleChord(eachFigure, 0, eachNoteDuration, scientificPitches);
			}
			element.setParentAtom(this);
			element.setRelativeOnset(currentRelativeOnset);
			//element.setDuration(eachNoteDuration);
			element.setDuration(computeDuration(element, eachNoteDuration, eachFigure));
			addSubatom(element);
			currentRelativeOnset = currentRelativeOnset.add(element.getDuration());
		}		

	}

	private Time computeDuration(Atom element, Time eachNoteDuration, Figures baseFigure) {
		if (!(element instanceof SingleFigureAtom)) {
			throw new IM3RuntimeException("Unsupported nested tuplets or similar: " + element.getClass());
		}
		SingleFigureAtom sf = (SingleFigureAtom) element;
		Time span = sf.getAtomFigure().getFigure().getDuration().divideBy(baseFigure.getDuration()); // e.g. a quarter note in a 8th triplet (quarter + 8th instead of 8th+8th8th)
		Time result = eachNoteDuration.multiplyBy(span);
		return result;
	}

	/**
	 * 
	 * @param tupletElements
	 * @param cardinality
	 * @param inSpaceOfAtoms
	 * @throws IM3Exception
	 */
	public SimpleTuplet(int cardinality, int inSpaceOfAtoms, Figures eachFigure, ArrayList<Atom> tupletElements) throws IM3Exception {
		this.cardinality = cardinality;
		this.inSpaceOfAtoms = inSpaceOfAtoms;
		wholeDuration = eachFigure.getDuration().multiplyBy(Fraction.getFraction(inSpaceOfAtoms, 1)); 
		this.eachFigure = eachFigure;

		Time eachNoteDuration = wholeDuration.divideBy(Fraction.getFraction(cardinality, 1));
		Time currentRelativeOnset = Time.TIME_ZERO;
		for (Atom element : tupletElements) {
			element.setParentAtom(this);
			element.setTime(currentRelativeOnset);
			//element.setDuration(eachNoteDuration);
			element.setDuration(computeDuration(element, eachNoteDuration, eachFigure));
			addSubatom(element);
			currentRelativeOnset = currentRelativeOnset.add(element.getDuration());
		}

	}

	public final Time getWholeDuration() {
		return wholeDuration;
	}

	public final Figures getEachFigure() {
		return eachFigure;
	}

	/**
	 * In a triplet = 3
	 * @return
	 */
	public final int getCardinality() {
		return cardinality;
	}
	/**
	 * In a triplet = 2
	 * @return
	 */
	public final int getInSpaceOfAtoms() {
		return inSpaceOfAtoms;
	}
	@Override
	public String toString() {
		return "tuplet " + cardinality + "/" + inSpaceOfAtoms + ", " + super.toString();
	}

}
