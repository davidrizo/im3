package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * This class should be used just as a preset. The main class to be used is Atom
 * @author drizo
 *
 */
public class SimpleChord extends SingleFigureAtom {
	ArrayList<AtomPitch> atomPitches;
		
	
	public SimpleChord(Figures figure, int dots, ScientificPitch ... pitches) {
		super(figure, dots);
		atomPitches = new ArrayList<>();
		for (ScientificPitch scientificPitch : pitches) {
			atomPitches.add(new AtomPitch(this.atomFigure, scientificPitch));			
		}
	}

	/**
	 * Package visibility, used by tuplets and mensural
	 */
	SimpleChord(Figures figure, int dots, Time alteredDuration, ScientificPitch ... pitches) {
		super(figure, dots, alteredDuration);
		atomPitches = new ArrayList<>();
		for (ScientificPitch scientificPitch : pitches) {
			atomPitches.add(new AtomPitch(this.atomFigure, scientificPitch));			
		}
	}
	
	public void addPitches(ScientificPitch ... pitches) {
		for (ScientificPitch scientificPitch : pitches) {
			atomPitches.add(new AtomPitch(this.atomFigure, scientificPitch));			
		}
	}
	
	public AtomPitch addPitch(ScientificPitch pitch) {
		AtomPitch ap = new AtomPitch(this.atomFigure, pitch);
		atomPitches.add(ap);
		return ap;
	}

	
	
	public ScientificPitch[] getPitches() {
		ScientificPitch [] result = new ScientificPitch[atomPitches.size()];
		int i=0;
		for (AtomPitch atomPitch: atomPitches) {
			result[i++] = atomPitch.getScientificPitch();
		}
		return result;
	}
	
	@Override
	public List<PlayedScoreNote> computePlayedNotes() {
		ArrayList<PlayedScoreNote> result = new ArrayList<>();
		Time time = getTime();
		Time duration = getDuration();
		for (AtomPitch atomPitch: atomPitches) {
			if (!atomPitch.isTiedFromPrevious()) {
				Time apDuration = duration;
				AtomPitch ap = atomPitch.getTiedToNext();
				ArrayList<AtomPitch> tiedPitches = new ArrayList<>();
				while (ap != null) {
					tiedPitches.add(ap);
					apDuration = apDuration.add(ap.getAtomFigure().getDuration());
					ap = ap.getTiedToNext();
				}
				PlayedScoreNote pn = new PlayedScoreNote(atomPitch, atomPitch.getScientificPitch(), time, apDuration);
				pn.addCorrespondingPitches(tiedPitches);
				result.add(pn);				
			}
		}
		return result;
	}

	@Override
	public List<AtomPitch> getAtomPitches() {
		return atomPitches;
	}

	public boolean containsPitch(ScientificPitch sp) {
		for (AtomPitch atomPitch : atomPitches) {
			if (atomPitch.getScientificPitch().equals(sp)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return super.toString() + ", atomPitches=" + atomPitches;
	}


	//TODO David - esto lo ha escrito Pierre para exportar MusicXML - quizás deberíamos mejor en el exportador
    // de MusicXML exportar directamente AtomPitch y no tener que hacer esto
    /**
     * Returns the notes of the chord
     * @return a list containing the notes of the chord
     */
    public List<SimpleNote> getNotesForMusicXML() {
        ArrayList<SimpleNote> notes = new ArrayList<>();
        for (AtomPitch apitch : getAtomPitches()) {
            SimpleNote snote = new SimpleNote(atomFigure.getFigure(), atomFigure.getDots(), null);
            snote.atomPitch = apitch;
            snote.atomFigure = atomFigure;
            snote.connectorCollection = connectorCollection;
            snote.belongsToBeam = belongsToBeam;
            snote.setTime(getTime());
            snote.setDuration(getDuration());
            snote.setExplicitStemDirection(getExplicitStemDirection());
            snote.setParentAtom(getParentAtom());
            snote.setLayer(getLayer());
            notes.add(snote);
        }
        return notes;
    }
}
