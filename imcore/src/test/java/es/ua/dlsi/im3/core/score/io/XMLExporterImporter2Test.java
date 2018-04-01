package es.ua.dlsi.im3.core.score.io;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLExporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
import es.ua.dlsi.im3.core.score.mensural.meters.TimeSignatureMensural;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import org.apache.commons.lang3.math.Fraction;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.*;

//TODO Poner junto a XMLExporterImporterTest después del merge
/**
 * @author drizo
 */
public class XMLExporterImporter2Test {
	boolean testMEIExportImport = true;
    private boolean testMusicXMLExportImport = false;

    @Before
	public void setUp() throws Exception {
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
	private static Void assertAtomsInLayers(ScoreSong song) {
        for (ScorePart part: song.getParts()) {
            for (ScoreLayer layer: part.getLayers()) {
                for (Atom atom: layer.getAtoms()) {
                    assertNotNull(atom.getLayer());
                    assertSame(layer, atom.getLayer());
                }
            }
        }
        return null;
    }
	@Test
	public void layersTest() throws Exception {
		doTest(XMLExporterImporter2Test::assertAtomsInLayers, importMEI(TestFileUtils.getFile("/testdata/core/score/io/simple_tie.mei")));
		doTest(XMLExporterImporter2Test::assertAtomsInLayers, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/simple_tie.xml")));

        doTest(XMLExporterImporter2Test::assertAtomsInLayers, importMEI(TestFileUtils.getFile("/testdata/core/score/io/cross-staff-multilayer.mei")));
        doTest(XMLExporterImporter2Test::assertAtomsInLayers, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/cross-staff-multilayer.xml")));

        doTest(XMLExporterImporter2Test::assertAtomsInLayers, importMEI(TestFileUtils.getFile("/testdata/core/score/io/cross-staff.mei")));
        doTest(XMLExporterImporter2Test::assertAtomsInLayers, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/cross-staff.xml")));

	}
	
	// ----
// ------------------------------------------------------------------------------------------	
    private static Void assertCrossStaffMultilayer(ScoreSong song) {
        //try {
            assertEquals("One part", 1, song.getParts().size());
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

    // TODO: 6/3/18 Ponerlo con XMLExporterImporterTest
    @Test
    public void testCrossStaffMultilayer() throws Exception {
        testMEIExportImport = false;
        testMusicXMLExportImport = false;
        doTest(XMLExporterImporter2Test::assertCrossStaffMultilayer, importMEI(TestFileUtils.getFile("/testdata/core/score/io/cross-staff-multilayer.mei")));
        //doTest(XMLExporterImporter2Test::assertCrossStaffMultilayer, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/cross-staff-multilayer.xml")));
    }

    // TODO: 20/3/18 Ponerlo con XMLExporterImporterTest
    // ------------------------------------------------------------------------------------------
    private static Void assertMultimeasureRest(ScoreSong song) {
        try {
            assertEquals("Measures", 4, song.getNumMeasures());
            ArrayList<Measure> measures = song.getMeasuresSortedAsArray();
            for (int i=0; i<song.getNumMeasures(); i++) {
                Measure measure = measures.get(i);
                Integer number = i+1;
                assertEquals("Measure number", number, measure.getNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    @Test
    public void multimeasureRest() throws Exception {
        doTest(XMLExporterImporter2Test::assertMultimeasureRest, importMEI(TestFileUtils.getFile("/testdata/core/score/io/multimeasure_rest.mei")));
        doTest(XMLExporterImporter2Test::assertMultimeasureRest, importMusicXML(TestFileUtils.getFile("/testdata/core/score/io/multimeasure_rest.xml")));
        //TODO Import from MusicXML        <measure-style>
        //TODO  <multiple-rest>2</multiple-rest>
        //TODO</measure-style>
    }

}
