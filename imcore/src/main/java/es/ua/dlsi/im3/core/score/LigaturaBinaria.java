package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;
/**
 * It is not a compound atom, visually it just has a note. The method getPlayedNotes will explode this single element 
 * in the notes it contains.
 * The therm LigatureBinaria comes from Willi Apel, page. 91
 * @author drizo
 *
 */
public abstract class LigaturaBinaria extends Atom {
	/**
	 * figures and pitches lists are parallel, i-th element in figure contains the i-th figure of the i-th pitch
	 * It allows for concatenated complex formations
	 */
	protected List<AtomFigure> figures;
	protected List<AtomPitch> pitches;
	protected AtomFigure wholeDurationFigure;
	
	/*public LigaturaBinaria(Figures wholeDurationFig, int wholeDurationDots, Figures firstFigure, ScientificPitch firstPitch, Figures secondFigure, ScientificPitch secondPitch) {
		figures = new ArrayList<AtomFigure>();
		pitches = new ArrayList<AtomPitch>();
		wholeDurationFigure = new AtomFigure(this, wholeDurationFig, wholeDurationDots);
		addDuration(wholeDurationFigure.getDuration());
		
		addComponent(firstFigure, 0, firstPitch);
		addComponent(secondFigure, 0, secondPitch);
	}*/

    public LigaturaBinaria(Figures wholeDurationFig, int wholeDurationDots, Figures firstFigure, int dotsFirstFigure, ScientificPitch firstPitch, Figures secondFigure, int dotsSecondFigure, ScientificPitch secondPitch) {
        figures = new ArrayList<AtomFigure>();
        pitches = new ArrayList<AtomPitch>();
        wholeDurationFigure = new AtomFigure(this, wholeDurationFig, wholeDurationDots);
        addDuration(wholeDurationFigure.getDuration());

        addComponent(firstFigure, dotsFirstFigure, firstPitch);
        addComponent(secondFigure, dotsSecondFigure, secondPitch);
    }

	/**
	 * This method cannot be public for the classes that inherit SimpleLigature
     * @param fig
     * @param dots
     * @param pitch
     */
	private void addComponent(Figures fig, int dots, ScientificPitch pitch) {
		AtomFigure af = new AtomFigure(this, fig, dots);
		figures.add(af);
		pitches.add(new AtomPitch(af, pitch));
	}

	@Override
	public List<PlayedScoreNote> computePlayedNotes() throws IM3Exception {
		ArrayList<PlayedScoreNote> result = new ArrayList<>();
		for (int i=0; i<figures.size(); i++) {
			PlayedScoreNote pn = new PlayedScoreNote(pitches.get(i), pitches.get(i).getScientificPitch(), figures.get(i).getTime(), figures.get(i).getDuration());
			result.add(pn);
		}
		return result;
	}
	
	@Override
	public List<AtomPitch> getAtomPitches() {
		return pitches;
	}

	@Override
	public List<AtomFigure> getAtomFigures() {
		return figures;
	}

    /**
     * Returns this element (1 item)
     * @return
     */
	@Override
	public List<Atom> getAtoms() {
		return Arrays.asList(this);
	}
}
