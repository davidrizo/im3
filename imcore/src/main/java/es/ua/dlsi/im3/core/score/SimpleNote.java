package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 * @author drizo
 */
public class SimpleNote extends SingleFigureAtom {
	AtomPitch atomPitch;
		
	public SimpleNote(Figures figure, int dots, ScientificPitch pitch) {
		super(figure, dots);
		atomPitch = new AtomPitch(this.atomFigure, pitch);
	}

	/**
	 * Note it does not copy ties, marks and connectors
	 * @param simpleNote
	 */
	public SimpleNote(SimpleNote simpleNote) throws IM3Exception {
		this(simpleNote.getAtomFigure().getFigure(), simpleNote.getAtomFigure().getDots(), simpleNote.getDuration(), simpleNote.getPitch());
		this.setGrace(simpleNote.isGrace());
		this.getAtomFigure().setColored(simpleNote.getAtomFigure().getColored());
		if (simpleNote.getAtomFigure().isExplicitMensuralPerfection()) {
			this.getAtomFigure().setExplicitMensuralPerfection(simpleNote.getAtomFigure().getMensuralPerfection());
		} else {
			this.getAtomFigure().setComputedMensuralPerfection(simpleNote.getAtomFigure().getMensuralPerfection(), simpleNote.getAtomFigure().getPerfectionRuleApplied());
		}
		this.setWrittenExplicitAccidental(simpleNote.getWrittenExplicitAccidental());
		this.getAtomFigure().setFollowedByMensuralDivisionDot(simpleNote.getAtomFigure().isFollowedByMensuralDivisionDot());
		//TODO otros parámetros?
	}

	/**
	 * Note it does not clone ties and connectors
	 * @return
	 */
	public SimpleNote clone() {
		try {
			return new SimpleNote(this);
		} catch (IM3Exception e) {
			throw new IM3RuntimeException("Cannot clone", e);
		}
	}
	/**
	 * Package visibility, used by tuplets and mensural
	 * @param figure
	 * @param dots
	 * @param alteredDuration
	 * @param pitch
	 */
	SimpleNote(Figures figure, int dots, Time alteredDuration, ScientificPitch pitch) {
		super(figure, dots, alteredDuration);
		atomPitch = new AtomPitch(this.atomFigure, pitch);
	}
	public ScientificPitch getPitch() {
		return atomPitch.getScientificPitch();
	}

	public final AtomPitch getAtomPitch() {
		return atomPitch;
	}

	/**
	 * Explicitly shown accidental
	 * @param writtenExplicitAccidental
	 * @throws IM3Exception 
	 */
	public void setWrittenExplicitAccidental(Accidentals writtenExplicitAccidental)  {
		atomPitch.setWrittenExplicitAccidental(writtenExplicitAccidental);		
	}

	public Accidentals getWrittenExplicitAccidental() {
		return atomPitch.getWrittenExplicitAccidental();
	}

	/**
	 * Accidental that should be played
	 * @param accidental
	 * @throws IM3Exception 
	 */
	public void setAccidental(Accidentals accidental) throws IM3Exception {
		atomPitch.setAccidental(accidental);
	}
	@Override
	public List<PlayedScoreNote> computePlayedNotes() {
		if (atomPitch.isTiedFromPrevious()) {
			return null;
		} else {
			Time duration = getDuration();
			ArrayList<AtomPitch> tiedPitches = new ArrayList<>();
			AtomPitch ap = atomPitch.getTiedToNext();
			while (ap != null) {
				duration = duration.add(ap.getAtomFigure().getDuration());
				tiedPitches.add(ap);
				ap = ap.getTiedToNext();
			}
			PlayedScoreNote pn = new PlayedScoreNote(ap, getPitch(), getTime(), duration);
			pn.addCorrespondingPitches(tiedPitches);
			return Arrays.asList(pn);
		}
	}
	
	/**
	 * @param to Pitch at left of this one
	 * @throws IM3Exception 
	 */
	public void tieToNext(AtomPitch to) throws IM3Exception {
		if (to == this.atomPitch) {
			throw new IM3Exception("Tying a note to itself");
		}

		this.atomPitch.setTiedToNext(to); 
	}
	public void tieToNext(SimpleNote to) throws IM3Exception {
		if (to == this) {
			throw new IM3Exception("Tying a note to itself");
		}

		this.atomPitch.setTiedToNext(to.getAtomPitch());
	}
	/**
	 * @param from fromPitch Pitch at right of this one
	 * @throws IM3Exception 
	 */
	public void tieFromPrevious(AtomPitch from) throws IM3Exception {
		if (from == this.atomPitch) {
			throw new IM3Exception("Tying a note to itself");
		}
		this.atomPitch.setTiedFromPrevious(from);
	}
	public void tieFromPrevious(SimpleNote from) throws IM3Exception {
		if (from == this) {
			throw new IM3Exception("Tying a note to itself");
		}
		this.atomPitch.setTiedFromPrevious(from.getAtomPitch());
	}
	@Override
	public List<AtomPitch> getAtomPitches() {
		return Arrays.asList(this.atomPitch);
	}
	/**
	 * It creates a new successor note tied to this one 
	 * @return
	 */
	public SimpleNote createTiedNote(Figures figure, int dots) {
		SimpleNote tiedNote = new SimpleNote(figure, dots, atomPitch.getScientificPitch());
		try {
			atomPitch.setTiedToNext(tiedNote.atomPitch);
		} catch (IM3Exception e) {
			throw new IM3RuntimeException("This shouldn't happen: " + e);
		}
		return tiedNote;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", " + atomPitch;
	}
	
}
