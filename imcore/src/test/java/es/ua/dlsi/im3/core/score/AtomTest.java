package es.ua.dlsi.im3.core.score;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Before;
import org.junit.Test;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.mensural.ligature.LigatureCumPropietateEtCumPerfectione;

/**
 * Different tests that show how the Atom construct works
 * @author drizo
 */
public class AtomTest {
	ArrayList<ScientificPitch> expectedPitches;
	ArrayList<Fraction> expectedOnsets;
	
	@Before
	public void setUp() throws Exception {
	}
	
	private ScientificPitch createPitch(PitchClasses pc, int octave, Fraction onset) {
		ScientificPitch pitch = new ScientificPitch(pc, octave);
		expectedPitches.add(pitch);
		expectedOnsets.add(onset);
		return pitch;
	}

	@Test
	public void testSimpleConstructsInVoice() throws IM3Exception {
		ScorePart part = new ScorePart(new ScoreSong(), 1);
		ScoreLayer voice = new ScoreLayer(part, 1);
		
		expectedPitches = new ArrayList<>();
		expectedOnsets = new ArrayList<>();
		ArrayList<Fraction> expectedDurations = new ArrayList<>();
		
		SimpleNote note = new SimpleNote(Figures.HALF, 0, createPitch(PitchClasses.A, 3, Fraction.ZERO));
		voice.add(note);
		expectedDurations.add(Fraction.getFraction(2,1));
		
		SimpleNote noteWith2Dots = new SimpleNote(Figures.QUARTER, 2, createPitch(PitchClasses.B, 3, Fraction.getFraction(2, 1)));
		voice.add(noteWith2Dots);
		expectedDurations.add(Fraction.getFraction(7,4));
		
		SimpleRest rest = new SimpleRest(Figures.SIXTEENTH, 0);
		voice.add(rest);
		expectedDurations.add(Fraction.getFraction(1,4));
		
		SimpleChord chord = new SimpleChord(Figures.WHOLE, 0, createPitch(PitchClasses.C, 3, Fraction.getFraction(4, 1)), 
				createPitch(PitchClasses.D, 3, Fraction.getFraction(4, 1)));
		voice.add(chord);
		expectedDurations.add(Fraction.getFraction(4,1));
		
		SimpleTuplet tuplet = new SimpleTuplet(3, 2, Figures.EIGHTH, 
				createPitch(PitchClasses.E, 3, Fraction.getFraction(8, 1)), 
				createPitch(PitchClasses.F, 3, Fraction.getFraction(26, 3)),
				createPitch(PitchClasses.G, 3, Fraction.getFraction(28, 3)));
		expectedDurations.add(Fraction.getFraction(1,1));
		
		voice.add(tuplet);
		
		assertEquals(5, voice.size());
		assertEquals(5, expectedDurations.size()); // inner check
		
		
		for (int i=0; i<expectedDurations.size(); i++) {
			assertEquals("Duration of " + voice.getAtom(i).getClass(), expectedDurations.get(i), voice.getAtom(i).getDuration().getExactTime());
		}
		
		List<AtomPitch> pitches = voice.getAtomPitches();
		assertEquals(expectedPitches.size(), pitches.size());
		assertEquals(expectedOnsets.size(), pitches.size());
		for (int i=0; i<expectedPitches.size(); i++) {
			assertEquals("#"+i, expectedPitches.get(i), pitches.get(i).getScientificPitch());
            assertEquals("#"+i, expectedOnsets.get(i), pitches.get(i).getTime().getExactTime());
		}
		
		assertEquals(9, voice.getDuration().getComputedTime(), 0.0001);
	}

	/**
	 * Mensural ligature
	 * @throws IM3Exception
	 */
	@Test
	public void testSimpleLigatures() throws IM3Exception {
		LigatureCumPropietateEtCumPerfectione l1 = new LigatureCumPropietateEtCumPerfectione(
				new ScientificPitch(PitchClasses.G, 3), new ScientificPitch(PitchClasses.A, 3));
		
		assertEquals(2, l1.getAtomFigures().size());
		//TODO
	}
	
	@Test
	public void testSimpleChord() throws IM3Exception {
		SimpleChord chord = new SimpleChord(Figures.QUARTER, 1, 
				new ScientificPitch(PitchClasses.C, 3), 
				new ScientificPitch(PitchClasses.E, 3),
				new ScientificPitch(PitchClasses.G, 3));
		assertEquals(1, chord.getAtomFigures().size());
		assertEquals(3, chord.getAtomPitches().size());
		assertEquals(Figures.QUARTER.getDurationWithDots(1), chord.getDuration());
	}
	
	@Test
	public void testSimpleNote() throws IM3Exception {
		SimpleNote note = new SimpleNote(Figures.EIGHTH, 0, 
				new ScientificPitch(PitchClasses.C, 3));
		assertEquals(1, note.getAtomFigures().size());
		assertEquals(1, note.getAtomPitches().size());
	}

	@Test
	public void testSimpleRest() throws IM3Exception {
		SimpleRest rest = new SimpleRest(Figures.WHOLE, 0);
		assertEquals(1, rest.getAtomFigures().size());
		assertNull(rest.getAtomPitches());
	}
	
	@Test
	public void testSimpleTuplet() throws IM3Exception {
		SimpleTuplet quintuplet = new SimpleTuplet(5, 4, Figures.EIGHTH,
				new ScientificPitch(PitchClasses.G, 3), 
				new ScientificPitch(PitchClasses.A, 3),
				new ScientificPitch(PitchClasses.B, 3),
				new ScientificPitch(PitchClasses.C, 4),
				new ScientificPitch(PitchClasses.D, 4)
				);
		assertEquals(5, quintuplet.getAtomFigures().size());
		assertEquals(5, quintuplet.getAtomPitches().size());
		assertEquals(Figures.HALF.getDuration(), quintuplet.getDuration());
	}
	
	@Test
	public void testSimpleChordTuplet() throws IM3Exception {
		SimpleTuplet triplet = new SimpleTuplet(3, 2, Figures.EIGHTH,				
				new ScientificPitch [] {new ScientificPitch(PitchClasses.C, 4), new ScientificPitch(PitchClasses.E, 4)},
				new ScientificPitch [] {new ScientificPitch(PitchClasses.D, 4), new ScientificPitch(PitchClasses.F_FLAT, 4)},
				new ScientificPitch [] {new ScientificPitch(PitchClasses.E, 4), new ScientificPitch(PitchClasses.G, 4)});
		
		assertEquals(3, triplet.getAtomFigures().size());
		assertEquals(6, triplet.getAtomPitches().size());
		
		assertEquals(Figures.QUARTER.getDuration(), triplet.getDuration());
	}
	
	/**
	 * Two tied notes built from an existing note
	 * @throws IM3Exception
	 */
	@Test
	public void tieNoteTest() throws IM3Exception {
		SimpleNote note = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.A, 3));
		SimpleNote tiedNote = note.createTiedNote(Figures.QUARTER, 1);
		assertEquals("Pitches", note.getAtomPitch().getScientificPitch(), tiedNote.getAtomPitch().getScientificPitch());
		List<PlayedScoreNote> pn = note.computePlayedNotes();
		assertEquals(1, pn.size());
		assertEquals(2.0, pn.get(0).getDuration().getComputedTime(), 0.001);
	}	
	
	/**
	 * Check cannot tie different pitch notes
	 * @throws IM3Exception
	 */
	@Test (expected = IM3Exception.class)
	public void tieInvalidNotesTest() throws IM3Exception {
		SimpleNote note = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.A, 3));
		SimpleNote tiedNote = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.B, 3));
		tiedNote.getAtomPitch().setTiedFromPrevious(note.getAtomPitch());
	}	

	
	/**
	 * Two chords (eigth _ dotted quarter) with all their notes tied
	 * @throws IM3Exception
	 */
	@Test
	public void tiedChordsTest() throws IM3Exception {
		SimpleChord chord = new SimpleChord(Figures.EIGHTH, 0, 
				new ScientificPitch(PitchClasses.A, 3),
				new ScientificPitch(PitchClasses.C, 4),
				new ScientificPitch(PitchClasses.E, 4));
		
		SimpleChord secondChord = new SimpleChord(Figures.QUARTER, 1, 
				new ScientificPitch(PitchClasses.A, 3),
				new ScientificPitch(PitchClasses.C, 4),
				new ScientificPitch(PitchClasses.E, 4));

		for (int i=0; i<3; i++) {
			chord.getAtomPitches().get(i).setTiedToNext(secondChord.getAtomPitches().get(i));
		}
		List<PlayedScoreNote> pn = chord.computePlayedNotes();
		assertEquals(3, pn.size());
		for (PlayedScoreNote playedNote : pn) {
			assertEquals(2, playedNote.getDuration().getComputedTime(), 0.001);
		}
		
		assertEquals(0, secondChord.computePlayedNotes().size());
	}
	
	/**
	 * Two chords (eigth _ dotted quarter) with all their notes tied but one
	 * @throws IM3Exception
	 */
	@Test
	public void partiallyTiedChordsFromScratchTest() throws IM3Exception {
		SimpleChord chord = new SimpleChord(Figures.EIGHTH, 0, 
				new ScientificPitch(PitchClasses.A, 3),
				new ScientificPitch(PitchClasses.C, 4),
				new ScientificPitch(PitchClasses.E, 4));
		
		SimpleChord secondChord = new SimpleChord(Figures.EIGHTH, 0, 
				new ScientificPitch(PitchClasses.A, 3),
				new ScientificPitch(PitchClasses.C, 4),
				new ScientificPitch(PitchClasses.E, 4));

		for (int i=0; i<2; i++) {
			chord.getAtomPitches().get(i).setTiedToNext(secondChord.getAtomPitches().get(i));
		}

		assertEquals(3, chord.computePlayedNotes().size());
		assertEquals(1, secondChord.computePlayedNotes().size());
	}
	
	//TODO hashCode, equals de todos los objetos.  
}
