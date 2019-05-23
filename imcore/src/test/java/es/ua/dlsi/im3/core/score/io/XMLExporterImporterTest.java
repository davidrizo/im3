package es.ua.dlsi.im3.core.score.io;

import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.*;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLExporter;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import org.apache.commons.lang3.math.Fraction;
import org.junit.Before;
import org.junit.Test;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
import es.ua.dlsi.im3.core.score.mensural.meters.TimeSignatureMensural;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.io.ImportException;

/**
 * MusicXML and MEI common tests
 * @author drizo
 */
public class XMLExporterImporterTest {
	boolean testMEIExportImport;
    private boolean testMusicXMLExportImport;

    @Before
	public void setUp() {
        testMEIExportImport = true;
        testMusicXMLExportImport = true;
	}

	private ScoreSong importMEI(File file) throws ImportException {
		MEISongImporter importer = new MEISongImporter();
		ScoreSong song = importer.importSong(file);
		return song;
	}
	
	private ScoreSong importMusicXML(File file) throws ImportException {
		MusicXMLImporter importer = new MusicXMLImporter();
		ScoreSong song = importer.importSong(file);
		return song;
	}
	
	private void doTest(Function<ScoreSong, Void> validationFunction, ScoreSong song) throws Exception {		
		validationFunction.apply(song);

		if (testMEIExportImport) {
            MEISongExporter exporter = new MEISongExporter();
            //File file = File.createTempFile("export", "mei");
            File file = TestFileUtils.createTempFile("aa.mei");
            exporter.exportSong(file, song);

			ScoreSong importedSong = importMEI(file);
			validationFunction.apply(importedSong);
		}

        if (testMusicXMLExportImport) {
            MusicXMLExporter exporter = new MusicXMLExporter();
            //File file = File.createTempFile("export", "mei");
            File file = TestFileUtils.createTempFile("aa.xml");
            exporter.exportSong(file, song);

            ScoreSong importedSong = importMusicXML(file);
            validationFunction.apply(importedSong);
        }


	}
		
	// ------------------------------------------------------------------------------------------
	private static Void assertSimple1(ScoreSong song) {
		try {
			assertEquals("Parts", 1, song.getParts().size());
			assertEquals("Staves", 1, song.getStaves().size());
			assertEquals("Voices", 1, song.getStaves().get(0).getLayers().size());
			List<ITimedElementInStaff> coreSymbols = song.getStaves().get(0).getCoreSymbolsOrdered();
			assertEquals("Symbols in staff", 9, coreSymbols.size());
			Staff firstStaff = song.getStaves().get(0);
			assertEquals("KeySignatures", 1, firstStaff.getKeySignatures().size());
			assertEquals("Key", Accidentals.SHARP, firstStaff.getKeySignatureWithOnset(Time.TIME_ZERO).getAccidental());
			assertEquals("Key notes", 2, firstStaff.getKeySignatureWithOnset(Time.TIME_ZERO).getInstrumentKey().getAlteredNoteNames().length);
			assertEquals("Meters", 1, firstStaff.getTimeSignatures().size());
			TimeSignature ts = firstStaff.getTimeSignatureWithOnset(Time.TIME_ZERO);
			assertEquals("Meter num", 4, ((FractionalTimeSignature)ts).getNumerator());
			assertEquals("Meter den", 4, ((FractionalTimeSignature)ts).getDenominator());
			assertEquals("Measures", 2, song.getMeaureCount());
			List<AtomFigure> atomFigures = song.getParts().get(0).getUniqueVoice().getAtomFigures();		
			Figures [] expectedFigs = {Figures.WHOLE, Figures.QUARTER, Figures.QUARTER, Figures.EIGHTH,
					Figures.EIGHTH, Figures.QUARTER};
			assertEquals("Num figures", expectedFigs.length, atomFigures.size());
					
			for (int i=0; i<expectedFigs.length; i++) {
				assertEquals("Figure #" + i, expectedFigs[i], atomFigures.get(i).getFigure());
			}
	
			PitchClasses [] expectedPitchClasses = {PitchClasses.G, PitchClasses.F_SHARP, PitchClasses.G_SHARP,
					PitchClasses.A
			};
			int [] expectedOctaves = {4,4,4,5};
			List<AtomPitch> atomPitches = song.getParts().get(0).getUniqueVoice().getAtomPitches();
			assertEquals(expectedPitchClasses.length, atomPitches.size());
			for (int i=0; i<expectedPitchClasses.length; i++) {
				assertEquals(expectedPitchClasses[i].getPitchClass(), atomPitches.get(i).getScientificPitch().getPitchClass());
				assertEquals("Pitch #i=" + i, expectedOctaves[i], atomPitches.get(i).getScientificPitch().getOctave());
			}
			
			//assertTrue(atomFigures.get(2).isRest());
			//assertTrue(atomFigures.get(4).isRest());
			
			assertEquals("Measures", 2, song.getNumMeasures());
			
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.toString());
		}
		return null;
	}
	

	@Test
	public void testSingle1() throws Exception {
        testMusicXMLExportImport = false; // TODO: 20/2/18 Para Pierre - ponerlo a true para probar el MusicXML 
		doTest(XMLExporterImporterTest::assertSimple1, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple1.xml")));
		doTest(XMLExporterImporterTest::assertSimple1, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple1.mei")));
	}
	
	// ------------------------------------------------------------------------------------------

	// this song contains a different time signature at the same time in different staves
	private static Void assertMensural1(ScoreSong song) {
    	Perfection [] modusMinor = new Perfection[] {Perfection.perfectum, Perfection.imperfectum, Perfection.perfectum};
		Perfection [] tempus = new Perfection[] {Perfection.perfectum, Perfection.imperfectum, Perfection.perfectum};

		try {
			List<Staff> staves = song.getStaves(); 
			assertEquals("Staves", 3, staves.size());
			assertEquals("triplum", staves.get(0).getName());
			assertEquals("motetus", staves.get(1).getName());
			assertEquals("tenor", staves.get(2).getName());
			
			Time lastMeterChangeTime = null;
			for (Staff staff: staves) {
				assertEquals("Time signature changes at " + staff.getName(), 3, staff.getTimeSignatures().size());
				TimeSignature lastTS = null;
				int mensurChange=0;

				for (TimeSignature ts: staff.getTimeSignatures()) {
					assertTrue("Time singnature mensural", ts instanceof TimeSignatureMensural);
					TimeSignatureMensural tsm = (TimeSignatureMensural) ts;
					assertEquals("Staff " + staff.getName() + ", Modus minor #" + mensurChange, modusMinor[mensurChange], tsm.getModusMinor());
					assertEquals("Staff " + staff.getName() + ", Prolatio", Perfection.perfectum, tsm.getProlatio());
					assertEquals("Staff " + staff.getName() + ", Tempus #" + mensurChange, tempus[mensurChange], tsm.getTempus());
					mensurChange++;
				}
			}
			
			//assertEquals("Time signatures", 3, song.getMeters().size());
			/*List<ISymbolInLayer> tenorSymbols = staves.get(2).getNotationSymbolsOrdered();
			
			Class<?>[] expectedClasses = {
					ClefG2OttavaBassa.class, KeySignature.class, TimeSignature.class,
					SimpleNote.class, Barline.class, 
					SimpleNote.class, Barline.class, 
					SimpleNote.class, Barline.class, 
					SimpleRest.class, Barline.class, 
					TimeSignature.class,
					SimpleNote.class, Barline.class, 
					SimpleNote.class, Barline.class, 
					SimpleNote.class, Barline.class, 
					SimpleNote.class, Barline.class, 
					SimpleRest.class, Barline.class,
					SimpleRest.class, Barline.class,
					TimeSignature.class,
					SimpleNote.class, Barline.class					
			};
			for (int i=0; i<expectedClasses.length; i++) {
				assertEquals("Class #" + i, expectedClasses[i], tenorSymbols.get(i).getClass());
			}*/
			
			/*assertEquals("Tenor Clef", ClefG2OttavaBassa.class, tenorSymbols.get(0).getClass());
			assertEquals("Tenor First (empty) Key Signature", KeySignature.class, tenorSymbols.get(1).getClass());
			//TODO Qué tipo de time signature
			assertEquals("Tenor First Time Signature", TimeSignature.class, tenorSymbols.get(2).getClass());
			for (int i=3; i<=5; i++) {
				assertEquals("Tenor Notes (3-5)", SimpleNote.class, tenorSymbols.get(i).getClass());
				assertEquals("Tenor notes figure", Figures.LONGA, ((SimpleNote)tenorSymbols.get(i)).getAtomFigure().getFigure());
			}
			assertEquals("Tenor Rest 6", SimpleRest.class, tenorSymbols.get(6).getClass());
			assertEquals("Tenor Rest figure", Figures.LONGA, ((SimpleRest)tenorSymbols.get(6)).getAtomFigure().getFigure());
			for (int i=7; i<=10; i++) {
				assertEquals("Tenor Notes (7-10)", SimpleNote.class, tenorSymbols.get(i).getClass());
				assertEquals("Tenor notes figure", Figures.LONGA, ((SimpleNote)tenorSymbols.get(i)).getAtomFigure().getFigure());
			}
			for (int i=11; i<=12; i++) {
				assertEquals("Tenor Rest (11-12)", SimpleRest.class, tenorSymbols.get(i).getClass());
				assertEquals("Tenor Rest figure", Figures.LONGA, ((SimpleRest)tenorSymbols.get(i)).getAtomFigure().getFigure());				
			}
			//TODO Qué tipo de time signature
			assertEquals("Tenor Time Signature Change", TimeSignature.class, tenorSymbols.get(13).getClass());
			
			assertEquals("Tenor Note 14", SimpleNote.class, tenorSymbols.get(14).getClass());
			assertEquals("Tenor notes figure", Figures.LONGA, ((SimpleNote)tenorSymbols.get(14)).getAtomFigure().getFigure());
			
			assertEquals("Tenor symbols (with barlines)", 25, tenorSymbols.size());*/
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		}
		return null;
	}
	@Test
	public void testMensural1() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		//TODO Urgent doTest(XMLExporterImporterTest::assertMensural1, importMEI(TestFileUtils.getFile("/testdata/core/score/io/garisonMEI.mei")));
	}

	// ------------------------------------------------------------------------------------------
	// this song contains a different time signature at the same staff in different staves 
	private static Void assertSpanishMensuralWithTransduction1(ScoreSong song) {
		try {
			List<Staff> staves = song.getStaves(); 
			assertEquals("Staves", 2, staves.size());
			assertEquals("Mensural meter", TimeSignatureProporcionMenor.class, staves.get(0).getTimeSignatureWithOnset(Time.TIME_ZERO).getClass());
			assertEquals("Mondern meter numerator", 3, ((FractionalTimeSignature)staves.get(1).getTimeSignatureWithOnset(Time.TIME_ZERO)).getNumerator());
			assertEquals("Mondern meter denominator", 2, ((FractionalTimeSignature)staves.get(1).getTimeSignatureWithOnset(Time.TIME_ZERO)).getDenominator());
			
			/*for (Staff staff: staves) {
				System.out.println("STAFF " + staff.getNumberIdentifier());
				assertEquals("Num layers", 1, staff.getLayers().size());
				// first atom is a rest
				for (int i=0; i<staff.getLayers().get(0).size(); i++) {
					Atom atom = staff.getLayers().get(0).getAtom(i);
					System.out.println(staff.getName() + " " + i  + ", time " + atom.getTime() + " " +  atom);
				}				
			}*/
			assertEquals("Num atoms at modern staff", 19, staves.get(1).getLayers().get(0).size());
			assertEquals("Num pitch onsets at modern staff ", 16, staves.get(1).getLayers().get(0).getPlayedNotes().size());
			
			assertEquals("Modern bars", 7, song.getMeaureCount());
			
			//ArrayList<AtomPitch> pitchesStaff1 = staves.get(0).getLayers().get(0).getOnsetPitches();
			//ArrayList<AtomPitch> pitchesStaff2 = staves.get(1).getLayers().get(0).getOnsetPitches();
			/*for (int i=0; i<16; i++) {
				System.out.println(pitchesStaff1.get(i).getTime());
				System.out.println(pitchesStaff2.get(i).getTime());
				System.out.println("---");
				//assertEquals("Onset of pitch #" +i, pitchesStaff1.get(i).getTime(), pitchesStaff2.get(i).getTime());
			}*/
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		}
		return null;
	}	
	@Test
	public void testSpanishMensural1() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		doTest(XMLExporterImporterTest::assertSpanishMensuralWithTransduction1, importMEI(TestFileUtils.getFile("/testdata/core/score/io/nodiviertanllantoninyo.mei")));
	}
	// ------------------------------------------------------------------------------------------
	private static Void assertCrossStaff(ScoreSong song) {
		//try {
			assertEquals(2, song.getStaves().size());
			assertEquals(DiatonicPitch.G, song.getStaves().get(0).getClefAtTime(Time.TIME_ZERO).getNote());
            assertSame(song.getStaves().get(0), song.getStaves().get(0).getClefAtTime(Time.TIME_ZERO).getStaff());
			assertEquals(DiatonicPitch.F, song.getStaves().get(1).getClefAtTime(Time.TIME_ZERO).getNote());
			assertEquals("Atoms in first staff", 4, song.getStaves().get(0).getAtoms().size());
			assertEquals("Atom pitches in first staff", 2, song.getStaves().get(0).getAtomPitches().size());
			assertEquals("Atom pitches in second< staff", 1, song.getStaves().get(1).getAtomPitches().size());
			// MEI encodes a mRest, musicXML not, don't check atoms but atom pitches 
		/*} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}*/
		return null;
	}
	@Test
	public void testCrossStaff() throws Exception {
        testMusicXMLExportImport = false;
		//doTest(XMLExporterImporterTest::assertCrossStaff, importMEI(TestFileUtils.getFile("/testdata/core/score/io/cross-staff.mei")));
		doTest(XMLExporterImporterTest::assertCrossStaff, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/cross-staff.xml")));
	}
	
	// ------------------------------------------------------------------------------------------	
	private static Void assertCrossStaffMultilayer(ScoreSong song) {
		//try {
			assertEquals(2, song.getStaves().size());
			assertEquals(5, song.getStaves().get(0).getAtoms().size());
			assertEquals(3, song.getStaves().get(0).getAtomPitches().size());
			assertEquals(4, song.getStaves().get(1).getAtoms().size());
			assertEquals(2, song.getStaves().get(1).getAtomPitches().size());
		/*} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}*/
		return null;
	}
	@Test
	public void testCrossStaffMultilayer() throws Exception {
        testMusicXMLExportImport = false;
		//TODO URGENT doTest(XMLExporterImporterTest::assertCrossStaffMultilayer, importMEI(TestFileUtils.getFile("/testdata/core/score/io/cross-staff-multilayer.mei")));
		//TODO URGENT doTest(XMLExporterImporterTest::assertCrossStaffMultilayer, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/cross-staff-multilayer.xml")));
	}
	
	// ------------------------------------------------------------------------------------------	
	private static Void assertMultipleTies(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			ScoreLayer uniqueVoice = song.getParts().get(0).getUniqueVoice();
			assertEquals(5, uniqueVoice.size());
			assertNull(uniqueVoice.getAtom(0).getAtomPitches());
			assertNull(uniqueVoice.getAtom(1).getAtomPitches());
			assertEquals(3, uniqueVoice.getAtom(2).getAtomPitches().size());
			assertEquals(3, uniqueVoice.getAtom(3).getAtomPitches().size());
			assertEquals(2, uniqueVoice.getAtom(4).getAtomPitches().size());
			List<PlayedScoreNote> pn = uniqueVoice.getPlayedNotes();
			assertEquals(4, pn.size());
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void testMultipleTies() throws Exception {
		doTest(XMLExporterImporterTest::assertMultipleTies, importMEI(TestFileUtils.getFile("/testdata/core/score/io/multiple_ties.mei")));
		doTest(XMLExporterImporterTest::assertMultipleTies, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/multiple_ties.xml")));
	}
	
	// ------------------------------------------------------------------------------------------
	private static Void assertpartially_tied_chord_two_measures(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(5, song.getParts().get(0).getUniqueVoice().size());
			assertEquals(3, song.getParts().get(0).getUniqueVoice().getAtom(2).getAtomPitches().size());
			assertEquals(3, song.getParts().get(0).getUniqueVoice().getAtom(3).getAtomPitches().size());
			assertEquals(4, song.getParts().get(0).getUniqueVoice().getPlayedNotes().size());
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void testpartially_tied_chord_two_measures() throws Exception {
		doTest(XMLExporterImporterTest::assertpartially_tied_chord_two_measures, importMEI(TestFileUtils.getFile("/testdata/core/score/io/partially_tied_chord_two_measures.mei")));
		doTest(XMLExporterImporterTest::assertpartially_tied_chord_two_measures, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/partially_tied_chord_two_measures.xml")));
	}
	
	// ------------------------------------------------------------------------------------------
	private static Void assertsimple_chord(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(1, song.getParts().get(0).getUniqueVoice().size());
			assertEquals(3, song.getParts().get(0).getUniqueVoice().getAtom(0).getAtomPitches().size());
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void testsimple_chord() throws Exception {
		doTest(XMLExporterImporterTest::assertsimple_chord, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_chord.mei")));
		doTest(XMLExporterImporterTest::assertsimple_chord, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_chord.xml")));
	}
	
	// ------------------------------------------------------------------------------------------
	private static Void assertsimple_tied_chord_same_measure(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(2, song.getParts().get(0).getUniqueVoice().size());
			List<PlayedScoreNote> pn = song.getParts().get(0).getUniqueVoice().getPlayedNotes();
			assertEquals(3, pn.size());
			for (int i=0; i<3; i++) {
				assertEquals(4, pn.get(i).getDuration().getComputedTime(), 0.001);
				assertEquals(0, pn.get(i).getOnset().getComputedTime(), 0.001);
			}
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void simple_tied_chord_same_measure() throws Exception {
		doTest(XMLExporterImporterTest::assertsimple_tied_chord_same_measure, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_tied_chord_same_measure.xml")));
		doTest(XMLExporterImporterTest::assertsimple_tied_chord_same_measure, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_tied_chord_same_measure.mei")));
	}
	
	// ------------------------------------------------------------------------------------------
	private static Void assertsimple_tied_chord_two_measures(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(4, song.getParts().get(0).getUniqueVoice().size()); // rests and chords
			List<PlayedScoreNote> pn = song.getParts().get(0).getUniqueVoice().getPlayedNotes();
			assertEquals(3, pn.size());
			for (int i=0; i<3; i++) {
				assertEquals(5, pn.get(i).getDuration().getComputedTime(), 0.001);
				assertEquals(3, pn.get(i).getOnset().getComputedTime(), 0.001);
			}
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
		return null;
	}
	
	@Test
	public void simple_tied_chord_two_measures() throws Exception {
		doTest(XMLExporterImporterTest::assertsimple_tied_chord_two_measures, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_tied_chord_two_measures.mei")));
		doTest(XMLExporterImporterTest::assertsimple_tied_chord_two_measures, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_tied_chord_two_measures.xml")));
	}

	// ------------------------------------------------------------------------------------------
	private static Void assertsimple_tuplet(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(2, song.getParts().get(0).getUniqueVoice().size()); // rest and tuplet 
			assertEquals(3, song.getParts().get(0).getUniqueVoice().getAtomPitches().size());
			assertEquals(4, song.getParts().get(0).getUniqueVoice().getAtomFigures().size()); // rest and tuplet
			
			assertEquals(new Time(3,1), song.getParts().get(0).getUniqueVoice().getAtom(0).getDuration());
			assertEquals(new Time(1,1), song.getParts().get(0).getUniqueVoice().getAtom(1).getDuration());
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		return null;
	}
	@Test
	public void simple_tuplet() throws Exception {
		doTest(XMLExporterImporterTest::assertsimple_tuplet, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_tuplet.mei")));
		doTest(XMLExporterImporterTest::assertsimple_tuplet, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_tuplet.xml")));
	}
	
	// ------------------------------------------------------------------------------------------
	private static Void assertTwoNotesTriplet(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(2, song.getParts().get(0).getUniqueVoice().size()); // rest and triplet  
			assertEquals(2, song.getParts().get(0).getUniqueVoice().getAtomPitches().size());
			assertEquals(3, song.getParts().get(0).getUniqueVoice().getAtomFigures().size()); // including rest 
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		return null;
	}
	@Test
	public void twoNotesTriplet() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		doTest(XMLExporterImporterTest::assertTwoNotesTriplet, importMEI(TestFileUtils.getFile("/testdata/core/score/io/two_notes_tuplet.mei")));
		doTest(XMLExporterImporterTest::assertTwoNotesTriplet, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/two_notes_tuplet.xml")));
	}	
	// ------------------------------------------------------------------------------------------
	private static Void asserttuplet_rest_chord_tied(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			ScoreLayer voice = song.getParts().get(0).getUniqueVoice();
			assertEquals(3, voice.size());  
			assertEquals(5, song.getParts().get(0).getUniqueVoice().getAtomPitches().size());
			assertEquals(5, song.getParts().get(0).getUniqueVoice().getAtomFigures().size());
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		return null;
	}
	@Test
	public void tuplet_rest_chord_tied() throws Exception {
		doTest(XMLExporterImporterTest::asserttuplet_rest_chord_tied, importMEI(TestFileUtils.getFile("/testdata/core/score/io/tuplet_rest_chord_tied.mei")));
		doTest(XMLExporterImporterTest::asserttuplet_rest_chord_tied, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/tuplet_rest_chord_tied.xml")));
	}

	// ------------------------------------------------------------------------------------------
	private static Void asserSimpleTie(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(2, song.getParts().get(0).getUniqueVoice().size());
			assertTrue("Tied to next", song.getParts().get(0).getUniqueVoice().getAtom(0).getAtomPitches().get(0).isTiedToNext());
			assertTrue("Tied from previous", song.getParts().get(0).getUniqueVoice().getAtom(1).getAtomPitches().get(0).isTiedFromPrevious());
			List<PlayedScoreNote> pn = song.getParts().get(0).getUniqueVoice().getPlayedNotes();
			assertEquals(1, pn.size());
			assertEquals(8, pn.get(0).getDuration().getComputedTime(), 0.001);
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void simpleTie() throws Exception {
		doTest(XMLExporterImporterTest::asserSimpleTie, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_tie.mei")));
		doTest(XMLExporterImporterTest::asserSimpleTie, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_tie.xml")));
	}

	// ------------------------------------------------------------------------------------------
	private static Void asserTwoTies(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(3, song.getParts().get(0).getUniqueVoice().size());
			List<PlayedScoreNote> pn = song.getParts().get(0).getUniqueVoice().getPlayedNotes();
			assertEquals(1, pn.size());
			assertEquals(12, pn.get(0).getDuration().getComputedTime(), 0.001);
		} catch (IM3Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void twoTies() throws Exception {
		doTest(XMLExporterImporterTest::asserTwoTies, importMEI(TestFileUtils.getFile("/testdata/core/score/io/two_ties.mei")));
		doTest(XMLExporterImporterTest::asserTwoTies, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/two_ties.xml")));
	}
	
	// ------------------------------------------------------------------------------------------
	private static Void assertMeterChange(ScoreSong song) {
		try {
			assertEquals(4, song.getStaves().size());
			for (Staff staff: song.getStaves()) {
				assertEquals(3, staff.getTimeSignatures().size());
				assertTrue(staff.getTimeSignatureWithOnset(Time.TIME_ZERO) instanceof TimeSignatureCommonTime);				
				assertEquals(3, ((FractionalTimeSignature)staff.getTimeSignatureWithOnset(new Time(Fraction.getFraction(4,1)))).getNumerator());
				assertEquals(4, ((FractionalTimeSignature)staff.getTimeSignatureWithOnset(new Time(Fraction.getFraction(4,1)))).getDenominator());
				assertEquals(6, ((FractionalTimeSignature)staff.getTimeSignatureWithOnset(new Time(Fraction.getFraction(10,1)))).getNumerator());
				assertEquals(8, ((FractionalTimeSignature)staff.getTimeSignatureWithOnset(new Time(Fraction.getFraction(10,1)))).getDenominator());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	// It also evaluates whole measure rests (mrest in MEI)
	@Test
	public void meterChange() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		doTest(XMLExporterImporterTest::assertMeterChange, importMEI(TestFileUtils.getFile("/testdata/core/score/io/meter_change.mei")));
		doTest(XMLExporterImporterTest::assertMeterChange, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/meter_change.xml")));
	}	
	
	// ------------------------------------------------------------------------------------------
	private static Void assertKeyChange(ScoreSong song) {
		try {
			assertEquals(3, song.getStaves().size());
			for (Staff staff: song.getStaves()) {
				assertEquals(5, staff.getKeySignatures().size());
				assertEquals(0, staff.getKeySignatureWithOnset(Time.TIME_ZERO).getAccidentals().size());				
				assertEquals(KeysEnum.GM.getKey(), staff.getKeySignatureWithOnset(new Time(4, 1)).getInstrumentKey());
				assertEquals(KeysEnum.Em.getKey(), staff.getKeySignatureWithOnset(new Time(13, 1)).getInstrumentKey());
				assertEquals(KeysEnum.EbM.getKey(), staff.getKeySignatureWithOnset(new Time(23, 1)).getInstrumentKey());
				
				Time t = new Time(23,1).add(new Time(9,2).multiply(3));
				assertEquals(KeysEnum.Dm.getKey(), staff.getKeySignatureWithOnset(t).getInstrumentKey());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void keyChange() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		// it does not use mRest
		doTest(XMLExporterImporterTest::assertKeyChange, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/key_changes_musescore.xml")));
		// it uses mRest
		doTest(XMLExporterImporterTest::assertKeyChange, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/key_changes.xml")));

		doTest(XMLExporterImporterTest::assertKeyChange, importMEI(TestFileUtils.getFile("/testdata/core/score/io/key_changes.mei")));
	}

	// ------------------------------------------------------------------------------------------
	private static Void assertTransposingInstruments(ScoreSong song) {
		try {
			assertEquals(3, song.getStaves().size());
			assertEquals(0, song.getUniqueKeyWithOnset(Time.TIME_ZERO).getFifths());
			assertEquals(1, song.getUniqueKeyWithOnset(new Time(8,1)).getFifths());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void transposingInstruments() {
		System.err.println("Pendiente TEST cambios tonalidad en medio con transposing instruments respuesta MEI-L");
		/*doTest(XMLExporterImporterTest::assertTransposingInstruments, importMEI(TestFileUtils.getFile("/testdata/score/io/transposing_instruments.mei")));
		doTest(XMLExporterImporterTest::assertTransposingInstruments, importMusicXML(TestFileUtils.getFile("/testdata/score/io/transposing_instruments.xml")));*/
	}			
	
	// ------------------------------------------------------------------------------------------
	private static Void assertAnacrusis(ScoreSong song) {
		try {
			assertEquals(1, song.getParts().size());
			assertEquals(1, song.getStaves().size());
			assertTrue(song.isAnacrusis());
			assertEquals(new Time(3, 1), song.getAnacrusisOffset());
			assertEquals(2, song.getMeaureCount());
			ArrayList<Measure> measures = song.getMeasuresSortedAsArray();
			assertEquals(Time.TIME_ZERO, measures.get(0).getTime());
			assertEquals(new Time(1, 1), measures.get(1).getTime());
			assertEquals(2, song.getParts().get(0).size());
			List<Atom> atoms = song.getPart(0).getAtomsSortedByTime();
			assertEquals(2, atoms.size());
			assertEquals(Time.TIME_ZERO, atoms.get(0).getTime());
			assertEquals(new Time(1, 1), atoms.get(1).getTime());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		
		return null;
	}
	@Test
	public void anacrusis() throws Exception {
		doTest(XMLExporterImporterTest::assertAnacrusis, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_anacrusis.mei")));
		doTest(XMLExporterImporterTest::assertAnacrusis, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_anacrusis.xml")));
	}			

	// ------------------------------------------------------------------------------------------
	private static Void assertAnacrusisStaves(ScoreSong song) {
		try {
			assertEquals(4, song.getStaves().size());
			assertTrue(song.isAnacrusis());
			assertEquals(new Time(3, 1), song.getAnacrusisOffset());
			assertEquals(2, song.getMeaureCount());
			ArrayList<Measure> measures = song.getMeasuresSortedAsArray();
			assertEquals(Time.TIME_ZERO, measures.get(0).getTime());
			assertEquals(new Time(1, 1), measures.get(1).getTime());
			assertTrue(song.getStaves().get(0).getAtoms().get(0) instanceof SimpleRest);
			assertTrue(song.getStaves().get(0).getAtoms().get(1) instanceof SimpleTuplet);
			assertEquals(Time.TIME_ZERO, song.getStaves().get(0).getAtoms().get(0).getTime());
			assertEquals(new Time(1,1), song.getStaves().get(0).getAtoms().get(1).getTime());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		return null;
	}
	@Test
	public void anacrusisStaves() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		doTest(XMLExporterImporterTest::assertAnacrusisStaves, importMEI(TestFileUtils.getFile("/testdata/core/score/io/anacrusis_staves.mei")));
		doTest(XMLExporterImporterTest::assertAnacrusisStaves, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/anacrusis_staves.xml")));
	}
	
	// ------------------------------------------------------------------------------------------
	private static Void assertMultimeasureRest(ScoreSong song) {
		try {
			assertEquals("Staves", 3, song.getStaves().size());
			assertEquals("Measures", 4, song.getNumMeasures());
			for (Staff staff: song.getStaves()) {
                //System.out.println("STAFF: " + staff);
                //for (Atom atom: staff.getAtoms()) {
                //    System.out.println(atom.getClass() + "  " + atom.getTime());
                //}
				assertEquals("Atoms", 3, staff.getAtoms().size());
				SimpleMultiMeasureRest smr = (SimpleMultiMeasureRest) staff.getAtoms().get(1);
				assertEquals("Multimeasure number of measures", 2, smr.getNumMeasures());
				assertEquals("Duration", 8.0, smr.getQuarterRatioDuration(), 0.001);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		return null;
	}
	@Test
	public void multimeasureRest() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		doTest(XMLExporterImporterTest::assertMultimeasureRest, importMEI(TestFileUtils.getFile("/testdata/core/score/io/multimeasure_rest.mei")));
        doTest(XMLExporterImporterTest::assertMultimeasureRest, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/multimeasure_rest.xml")));
		//TODO Import from MusicXML        <measure-style>
        //TODO  <multiple-rest>2</multiple-rest>
        //TODO</measure-style>
	}


    // ------------------------------------------------------------------------------------------
    private static Void assertMultimeasureRestBeginning(ScoreSong song) {
        try {
            assertEquals("Staves", 1, song.getStaves().size());
            assertEquals("Measures", 7, song.getNumMeasures());
            Staff staff = song.getStaves().get(0);
            //for (Atom atom: staff.getAtoms()) {
            //    System.out.println(atom.getClass());
            //}
            assertEquals("Atoms", 4, staff.getAtoms().size());
            SimpleMultiMeasureRest smr = (SimpleMultiMeasureRest) staff.getAtoms().get(0);
            assertEquals("Multimeasure number of measures", 6, smr.getNumMeasures());
            assertEquals("Duration", 18, smr.getQuarterRatioDuration(), 0.001);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }

    @Test
    public void multimeasureRestBeginning() throws Exception {
        testMusicXMLExportImport = false;
        doTest(XMLExporterImporterTest::assertMultimeasureRestBeginning, importMEI(TestFileUtils.getFile("/testdata/core/score/io/multimeasure_rest_beginning.mei")));
        doTest(XMLExporterImporterTest::assertMultimeasureRestBeginning, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/multimeasure_rest_beginning.xml")));
    }




    // ------------------------------------------------------------------------------------------
    private static Void assertAccidentals(ScoreSong song) {
        try {
            assertEquals("Parts", 1, song.getParts().size());
            assertEquals("Measures", 2, song.getNumMeasures());
            ScorePart part = song.getParts().get(0);
            assertEquals("Layers", 1, part.getLayers().size());
            ScoreLayer layer = part.getUniqueVoice();
            assertEquals(-3, song.getUniqueKeyWithOnset(Time.TIME_ZERO).getFifths());

            boolean testWrittenExplicit = layer.getAtomPitches().size() == 11;
            if (!testWrittenExplicit) {
                assertEquals(10, layer.getAtomPitches().size());
            } // else it is 9 - // TODO: 30/4/18 El test de MusicXML sólo tiene 9 notas
            TreeSet<AtomPitch> pitches = layer.getAtomPitchesSortedByTime();
            List<PitchClasses> expectedPitchClasses = Arrays.asList(
                    PitchClasses.A_FLAT, PitchClasses.B_FLAT, PitchClasses.C, PitchClasses.D,
                    PitchClasses.B, PitchClasses.C_SHARP, PitchClasses.C_SHARP, PitchClasses.B_FLAT,PitchClasses.B_FLAT, PitchClasses.G,
                    PitchClasses.G_SHARP
                    );

            int i=0;
            for (AtomPitch atomPitch: pitches) {
                if (i<9 || testWrittenExplicit) { // MusicXML is not representing played != represented accidentals
                    assertEquals("Pitch #" + i, expectedPitchClasses.get(i).getPitchClass(), atomPitch.getScientificPitch().getPitchClass());
                }
                if (i<9) {
                    assertNull("Explicit written accidental of note #" + i, atomPitch.getWrittenExplicitAccidental());
                } else {
                    if (testWrittenExplicit && i==9) {
                        assertEquals("Explicit written accidental of last note", Accidentals.SHARP, atomPitch.getWrittenExplicitAccidental());
                    }
                }
                i++;
            }


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    @Test
    public void accidentals() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
        // in this file the pitches in key signature do not have the accid explicitly encoded
        doTest(XMLExporterImporterTest::assertAccidentals, importMEI(TestFileUtils.getFile("/testdata/core/score/io/accidentals_non_explicit_accid.mei")));

        // in this file the pitches in key signature have the accid explicitly encoded
        doTest(XMLExporterImporterTest::assertAccidentals, importMEI(TestFileUtils.getFile("/testdata/core/score/io/accidentals.mei")));

        // TODO: 30/4/18 Incluir alteraciones escritas distintas de las interpretadas como en MEI (accid.ges = interpretadas, accid = escritas - nosotros usamos writtenExplicitAccidental para la impresa cuando es distinta de la interpretada)
        doTest(XMLExporterImporterTest::assertAccidentals, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/accidentals.xml")));
    }

    // ------------------------------------------------------------------------------------------
    private static Void assertSystemBeginnings(ScoreSong song) {
        try {
            assertEquals(2, song.getStaves().size());
            ArrayList<Measure> measures = song.getMeasuresSortedAsArray();
            assertEquals(11, measures.size());

            // just one staff contains the system break
            boolean found = false;
            for (ScorePart scorePart: song.getParts()) {
            	if (scorePart.getPageSystemBeginnings().getSystemBeginnings().size() == 2) {
                    found = true;
                    assertTrue(scorePart.getPageSystemBeginnings().hasSystemBeginning(measures.get(2).getTime()));
                    assertTrue(scorePart.getPageSystemBeginnings().hasSystemBeginning(measures.get(7).getTime()));
                }
            }
            if (!found) {
                fail("No part does not contain 2 system breaks");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    @Test
    public void systemBreaks() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		System.err.println("TO-DO System breaks tests!!!!!!!!!!!!!");
        //doTest(XMLExporterImporterTest::assertSystemBeginnings, importMEI(TestFileUtils.getFile("/testdata/core/score/layout/manual_system_break.mei")));
        //doTest(XMLExporterImporterTest::assertSystemBeginnings, importMusicXML(TestFileUtils.getFile("/testdata/core/score/layout/manual_system_break.xml")));
    }


	// ------------------------------------------------------------------------------------------
	private static Void assertMelisma(ScoreSong song) {
		try {
			assertEquals(1, song.getStaves().size());
			List<AtomPitch> atomPitches = song.getStaves().get(0).getAtomPitches();
			assertEquals(9, atomPitches.size());
			assertNotNull("Has lyrics", atomPitches.get(0).getLyrics());
            assertEquals(1, atomPitches.get(0).getLyrics().size());
            assertEquals("Glo", atomPitches.get(0).getLyrics().firstEntry().getValue().getText());
			assertEquals(Syllabic.begin, atomPitches.get(0).getLyrics().firstEntry().getValue().getSyllabic());
			for (int i=1; i<7; i++) {
				assertNull(atomPitches.get(i).getLyrics());
			}
			assertEquals("ri", atomPitches.get(7).getLyrics().firstEntry().getValue().getText());
			assertEquals(Syllabic.middle, atomPitches.get(7).getLyrics().firstEntry().getValue().getSyllabic());
			assertEquals("a", atomPitches.get(8).getLyrics().firstEntry().getValue().getText());
			assertEquals(Syllabic.end, atomPitches.get(8).getLyrics().firstEntry().getValue().getSyllabic());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return null;
	}
	@Test
	public void melisma() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		doTest(XMLExporterImporterTest::assertMelisma, importMEI(TestFileUtils.getFile("/testdata/core/score/io/melisma.mei")));
		doTest(XMLExporterImporterTest::assertMelisma, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/melisma.xml")));
	}


    // ------------------------------------------------------------------------------------------
    private static Void assertNaturals(ScoreSong song) {
        try {
            assertEquals(1, song.getStaves().size());
            assertEquals("Key signature with 2 sharps", 2, song.getUniqueKeyWithOnset(Time.TIME_ZERO).getFifths());
            List<AtomPitch> atomPitches = song.getStaves().get(0).getAtomPitches();
            assertEquals(6, atomPitches.size());

            PitchClasses [] expected = new PitchClasses[] {
              PitchClasses.F_SHARP, PitchClasses.F, PitchClasses.F_SHARP, PitchClasses.F, PitchClasses.F_SHARP, PitchClasses.F_SHARP
            };

            for (int i=0; i<atomPitches.size(); i++) {
                assertEquals("Pitch #" + i, expected[i].getPitchClass(), atomPitches.get(i).getScientificPitch().getPitchClass());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    @Test
    public void naturals() throws Exception {
        doTest(XMLExporterImporterTest::assertNaturals, importMEI(TestFileUtils.getFile("/testdata/core/score/io/naturals.mei")));
        doTest(XMLExporterImporterTest::assertNaturals, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/naturals.xml")));
    }

    // ------------------------------------------------------------------------------------------
    private static Void assertNaturals2(ScoreSong song) {
        try {
            assertEquals(1, song.getStaves().size());
            assertEquals("Key signature with 4 flats", -4, song.getUniqueKeyWithOnset(Time.TIME_ZERO).getFifths());
            List<AtomPitch> atomPitches = song.getStaves().get(0).getAtomPitches();
            assertEquals(8, atomPitches.size());

            PitchClasses [] expected = new PitchClasses[] {
                    PitchClasses.G, PitchClasses.A_SHARP, PitchClasses.C_DSHARP, PitchClasses.A_DFLAT,
                    PitchClasses.C_FLAT, PitchClasses.C, PitchClasses.G, PitchClasses.G
            };

            for (int i=0; i<atomPitches.size(); i++) {
                assertEquals("Pitch #" + i, expected[i].getPitchClass(), atomPitches.get(i).getScientificPitch().getPitchClass());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    @Test
    public void naturals2() throws Exception {
        doTest(XMLExporterImporterTest::assertNaturals2, importMEI(TestFileUtils.getFile("/testdata/core/score/io/naturals2.mei")));
        doTest(XMLExporterImporterTest::assertNaturals2, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/naturals2.xml")));
    }

    // ------------------------------------------------------------------------------------------
    private static Void assertFermateTrills(ScoreSong song) {
        try {
            assertEquals(1, song.getStaves().size());
            Staff staff = song.getStaves().get(0);
            List<Atom> atoms  = song.getStaves().get(0).getAtoms();
            assertEquals(3, atoms.size());

            assertEquals("Fermate", 2, staff.getFermate().size());
            Fermate f0 = staff.getFermateWithOnset(Time.TIME_ZERO);
            assertNotNull("First fermate", f0);
            assertEquals(1, f0.getFermate().size());
            assertNotNull("First fermate position", f0.getFermata(PositionAboveBelow.BELOW));

            Fermate f1 = staff.getFermateWithOnset(new Time(1));
            assertNotNull("Second fermate", f1);
            assertEquals(1, f1.getFermate().size());
            assertNotNull("Second fermate position", f1.getFermata(PositionAboveBelow.ABOVE));

            int count = 0;
            int [] expectedTrillBeats = new int[] {0, 2};
            List<StaffMark> marks = staff.getMarksOrderedByTime();
            for (int i=0; i<marks.size(); i++) {
                if (marks.get(i) instanceof Trill) {
                    assertEquals("Trill #" + i + " time", new Time(expectedTrillBeats[count]),marks.get(i).getTime());
                    count++;
                }
            }
            assertEquals("Trills", 2, count);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    @Test
    public void fermateTrills() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
	    // the .mei has been exported from Sibelius and modified manually to encode the fermate in to different ways
        doTest(XMLExporterImporterTest::assertFermateTrills, importMEI(TestFileUtils.getFile("/testdata/core/score/io/fermata_trill.mei")));
        doTest(XMLExporterImporterTest::assertFermateTrills, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/fermata_trill.xml")));
    }
    // ------------------------------------------------------------------------------------------
	private static Void assertSimpleBeam(ScoreSong song) {
		try {
			assertEquals(1, song.getStaves().size());
			List<Atom> atoms = song.getStaves().get(0).getAtoms();
			assertEquals(7, atoms.size());

            ArrayList<BeamGroup> beams = new ArrayList<>();
			for (int i=0; i<6; i++) {
                assertEquals(SimpleNote.class, atoms.get(i).getClass());
                BeamGroup beam = ((SimpleNote) atoms.get(i)).getBelongsToBeam();
                assertNotNull(beam);
                beams.add(beam);
            }
            assertEquals(SimpleRest.class, atoms.get(6).getClass());
            assertSame(beams.get(0), beams.get(1));
            assertNotSame(beams.get(0), beams.get(2));
            assertSame(beams.get(2), beams.get(3));
            assertSame(beams.get(2), beams.get(4));
            assertSame(beams.get(2), beams.get(5));

            List<ITimedElementInStaff> coreSymbolsInStaff = song.getStaves().get(0).getCoreSymbolsOrdered();
            assertEquals(10, coreSymbolsInStaff.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return null;
	}

    // TODO: 1/10/17 Import beams in MusicXML
    @Test
	public void simpleBeam() throws Exception {
        this.testMusicXMLExportImport = false; //TODO
		doTest(XMLExporterImporterTest::assertSimpleBeam, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_beam.mei")));
		//doTest(XMLExporterImporterTest::assertSimpleBeam, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_beam.xml")));
	}

    // ------------------------------------------------------------------------------------------
    private static Void assertStemDir(ScoreSong song) {
        try {
            assertEquals(1, song.getStaves().size());
            List<Atom> atoms = song.getStaves().get(0).getAtoms();
            assertEquals(2, atoms.size());

            assertTrue(atoms.get(0) instanceof  SimpleNote);
            assertTrue(atoms.get(1) instanceof  SimpleNote);

            SimpleNote s0 = (SimpleNote) atoms.get(0);
            SimpleNote s1 = (SimpleNote) atoms.get(1);

            assertNull("S0 explicit stem", s0.getExplicitStemDirection());
            assertNotNull("S1 explicit stem", s1.getExplicitStemDirection());

            assertEquals("Stem 1 direction up", StemDirection.up, s1.getExplicitStemDirection());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }

    // TODO: 1/10/17 Import beams in MusicXML
    @Test
    public void stemDir() throws Exception {
        doTest(XMLExporterImporterTest::assertStemDir, importMEI(TestFileUtils.getFile("/testdata/core/score/io/stemdir.mei")));
        doTest(XMLExporterImporterTest::assertStemDir, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/stemdir.xml")));
    }



	//TODO Grace notes, slurs, tuplet dentro de tuplet
	// tuplet con elementos en distintos staves, tuplet con acordes con notas en distintos staves
	// tuplet: negra_puntillo + corchea?: https://i.stack.imgur.com/OLiOH.png
	// test staff.getAtomPitch ....
	// test mrest vacíos - ver mrest en MEI guidelines
	// two dots (e.g. 9/8)
	// multiple measure rest

	// partial measures (repetitions in the middle of the measure)
	//TODO puntillo_stacatto.xml -- lleva multimeasure rest
	//https://github.com/cuthbertLab/musicxmlTestSuite/blob/master/xmlFiles/02d-Rests-Multimeasure-TimeSignatures.xml
	
	// ScoreGap
}
