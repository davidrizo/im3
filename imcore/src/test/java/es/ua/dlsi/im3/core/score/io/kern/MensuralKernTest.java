package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefF4;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import org.apache.commons.lang3.math.Fraction;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * @author drizo
 */
public class MensuralKernTest {
    boolean testExportImport = true;

    private ScoreSong importMEI(File file) throws ImportException {
        MEISongImporter importer = new MEISongImporter();
        ScoreSong song = importer.importSong(file);
        return song;
    }

    private ScoreSong importKern(File file) throws ImportException {
        KernImporter importer = new KernImporter();
        ScoreSong song = importer.importSong(file);
        return song;
    }

    private void doTest(Function<ScoreSong, Void> validationFunction, ScoreSong song) throws Exception {
        validationFunction.apply(song);
        KernExporter exporter = new KernExporter();
        File file = TestFileUtils.createTempFile("aa.krn");
        exporter.exportSong(file, song);

        if (testExportImport) {
            ScoreSong importedSong = importKern(file);
            validationFunction.apply(importedSong);
        }
    }

    // ------------------------------------------------------------------------------------------
    private static Void assertPatriarca1(ScoreSong song) {
        try {
            ScorePart part1 = song.getParts().get(0);
            ScoreLayer voice = part1.getUniqueVoice();
            assertTrue("First rest", voice.getAtom(0) instanceof SimpleRest);
            SimpleRest rest1 = (SimpleRest) voice.getAtom(0);
            assertTrue("First rest duration", rest1.getAtomFigure().getFigure() == Figures.MINIM);

            assertTrue("First note", voice.getAtom(1) instanceof SimpleNote);
            SimpleNote note1 = (SimpleNote) voice.getAtom(1);
            assertTrue("First note duration", note1.getAtomFigure().getFigure() == Figures.MINIM);
            assertEquals("First note pitch octave", 5, note1.getPitch().getOctave());
            assertEquals("First note pitch class", PitchClasses.D.getPitchClass(), note1.getPitch().getPitchClass());
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
        return null;
    }


    @Test
    public void testPatriarca1() throws Exception {
        doTest(MensuralKernTest::assertPatriarca1, importMEI(TestFileUtils.getFile("/testdata/core/score/layout/patriarca/16-1544_ES-VC_1-3-1_00003.mei")));
        //doTest(MensuralKernTest::assertPatriarca1, importKern(TestFileUtils.getFile("/testdata/core/score/io/kern/guide02-example2-1.krn")));
    }
}