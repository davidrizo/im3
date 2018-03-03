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
}