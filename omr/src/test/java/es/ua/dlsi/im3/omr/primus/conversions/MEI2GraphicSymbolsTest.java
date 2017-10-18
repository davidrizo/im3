package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MEI2GraphicSymbolsTest {
    @Test
    public void convertSimple1() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/simple1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        System.out.println(stringFormat);
        /*assertEquals("clef-L2\taccidental.#-L5\taccidental.#-S3\ttimeSig.4-L4\ttimeSig.4-L2\tnote.WHOLE-L2\tbarline-L1\tnote.QUARTER-S1\trest.QUARTER-L3\taccidental.#-L2\tnote.EIGHTH-L2\trest.EIGHTH-L3\tnote.QUARTER-L6\tbarline-L1",
                stringFormat);*/
    }

    @Test
    public void convertPrimusSample() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051650-1_1_1/000051650-1_1_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        System.out.println(stringFormat);
    }

    @Test
    public void convertPrimusSampleWithAccidentalsAndFermata() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051806-1_1_1/000051806-1_1_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        String expected = "[clef.C-L1, metersign.C-L3, note.quarter-S3, barline-L1, note.quarter-S3, note.beamedRight2-L3, note.beamedBoth2-S2, note.beamedBoth2-L2, note.beamedLeft2-S2, note.quarter-L2, note.beamedRight1-S3, dot-S3, accidental.b-L4, note.beamedLeft2-L4, barline-L1, note.quarter-L3, slur.start-L3, slur.end-L3, note.beamedRight3-L3, accidental.b-L4, note.beamedBoth3-L4, note.beamedBoth3-S3, note.beamedBoth3-L3, note.beamedBoth1-S2, note.beamedBoth3-L3, note.beamedLeft3-L2, fermata.above, note.eighth-S2]";
        assertEquals(expected, stringFormat);
    }

    @Test
    public void convertPrimusSampleBeams() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051759-1_1_1/000051759-1_1_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        String expected = "[clef.G-L2, accidental.#-L5, accidental.#-S3, digit.2-L4, digit.4-L2, rest.SIXTEENTH-L3, note.beamedRight2-S1, note.beamedBoth2-L2, note.beamedLeft2-S2, note.beamedRight1-S0, note.beamedLeft1-L4, slur.start-L4, barline-L1, slur.end-L4, note.beamedRight1-L4, note.beamedBoth2-S3, note.beamedLeft2-L3, note.beamedRight2-S3, note.beamedBoth2-L4, note.beamedLeft1-S4, slur.start-S4, barline-L1, slur.end-S4, note.beamedRight2-S4, note.beamedBoth2-S2, note.beamedBoth2-L3, note.beamedLeft2-S3]";
        assertEquals(expected, stringFormat);

    }


}