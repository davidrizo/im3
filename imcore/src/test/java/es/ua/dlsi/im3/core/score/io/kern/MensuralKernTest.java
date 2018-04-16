package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefF4;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.meters.TempusImperfectumCumProlationeImperfecta;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
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

        File file2 = TestFileUtils.createTempFile("aa.mei");
        MEISongExporter meiSongExporter = new MEISongExporter();
        meiSongExporter.exportSong(file2, song);
        if (testExportImport) {
            ScoreSong importedSong = importMEI(file2);
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

            assertEquals("Staves", 1, song.getStaves().size());
            Staff staff = song.getStaves().get(0);
            List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();
            assertTrue(symbols.get(0) instanceof Clef);
            assertTrue(symbols.get(3) instanceof SimpleRest);
            assertTrue(symbols.get(4) instanceof SimpleNote);
            assertTrue(symbols.get(5) instanceof MarkBarline);
            assertTrue(symbols.get(6) instanceof SimpleNote);
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

    // ------------------------------------------------------------------------------------------
    private static Void assertPatriarca2(ScoreSong song) {
        try {
            ScorePart part1 = song.getParts().get(0);

            assertEquals("Staves", 1, song.getStaves().size());
            Staff staff = song.getStaves().get(0);
            assertEquals("Staff name", "Soprano", staff.getName());
            List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();

            assertEquals("Time signatures", 1, staff.getTimeSignatures().size());
            assertTrue(staff.getTimeSignatureWithOnset(Time.TIME_ZERO) instanceof TempusImperfectumCumProlationeImperfecta);
            assertTrue(symbols.get(0) instanceof Clef);
            assertTrue(symbols.get(3) instanceof SimpleRest);
            assertTrue(symbols.get(4) instanceof SimpleRest);
            assertTrue(symbols.get(5) instanceof MarkBarline);
            assertTrue(symbols.get(6) instanceof SimpleRest);
            assertTrue(symbols.get(7) instanceof SimpleNote);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
        return null;
    }

    @Test
    public void testPatriarca2() throws Exception {
        doTest(MensuralKernTest::assertPatriarca2, importKern(TestFileUtils.getFile("/testdata/core/score/io/kern/mensural_binary.krn")));
    }

    private void test(String file) throws IM3Exception {
        String path = "/testdata/core/score/io/kern/apel/";
        ScoreSong kernSong = importKern(TestFileUtils.getFile(path + file + ".krn"));
        ScoreSong meiSong = importMEI(TestFileUtils.getFile(path + file + ".mei"));
        TestScoreUtils.checkEqual("kern", kernSong, "mei", meiSong);

        // TODO: 10/4/18 Exportar y comprobar que son iguales
    }

    //// tests for Wili Apel
    @Test
    public void testWiliApel() throws IM3Exception {
        test("pag87_fig1");
        test("pag89_fig1");
        test("pag90_fig1");
        test("pag96_fig1");
        test("pag101_fig1");
        test("pag108_fig1");
        test("pag108_fig2");
    }


}