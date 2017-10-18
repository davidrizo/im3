package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MEI2GraphicSymbolsTest {
    @Test
    public void convertPrimusSampleLedgerLines() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051650-1_1_1/000051650-1_1_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        // Currently we don't print ledger lines
        String expected = "[clef.G-L2, accidental.b-L3, accidental.b-S4, accidental.b-S2, digit.3-L4, digit.4-L2, note.quarter-S6, note.beamedRight1-S4, note.beamedBoth1-S6, note.beamedBoth1-L7, note.beamedLeft1-S6, barline-L1, note.beamedRight1-L6, note.beamedLeft1-L6, rest.SIXTEENTH-L3, note.beamedRight2-L6, note.beamedBoth2-S5, note.beamedLeft2-L6, note.beamedRight2-S6, note.beamedBoth2-L6, note.beamedBoth2-S5, note.beamedLeft2-L6, barline-L1]";
        assertEquals(expected, stringFormat);
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

    @Test
    public void convertPrimusMultimeasure() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051650-1_1_2/000051650-1_1_2.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        String expected = "[clef.C-L1, accidental.b-S0, accidental.b-L2, accidental.b-L0, digit.3-L4, digit.4-L2, digit.1-S5, digit.0-S5, multirest-L3, barline-L1, note.quarter-L4, note.eighth-L2, note.eighth-S5, note.eighth-S5, note.eighth-S4, barline-L1, note.eighth-S3, note.eighth-S3, rest.QUARTER-L3, rest.QUARTER-L3, barline-L1, note.quarter-L6, note.eighth-S5, dot-S5, note.sixteenth-S4, note.eighth-L4, dot-S4, note.sixteenth-S3, barline-L1, note.eighth-S3, note.eighth-L3, rest.QUARTER-L3]";
        assertEquals(expected, stringFormat);
    }

    @Test
    public void convertPrimusMultimeasureDoubleWholeGlyph() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051653-1_2_1/000051653-1_2_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        String expected = "[clef.C-L1, accidental.b-L4, accidental.b-L2, accidental.b-S3, metersign.C-L3, fermata.above, rest.WHOLE-L4, barline-L1, note.half-S5, note.quarter-L4, dot-S4, note.eighth-L3, barline-L1, note.quarter-L2, note.quarter-L2, fermata.above, rest.HALF-L3, barline-L1, digit.2-S5, multirest-L3, barline-L1, note.half-L6, note.quarter-L5, dot-S5, note.eighth-L4, barline-L1, note.quarter-S3, note.quarter-S3, fermata.above, rest.HALF-L3, barline-L1]";
        assertEquals(expected, stringFormat);
    }

    @Test
    public void convertPrimusAcciaccatura() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051660-1_2_1/000051660-1_2_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        String expected = "[clef.C-L1, accidental.b-L4, accidental.b-L2, digit.3-L4, digit.4-L2, note.eighth-L6, dot-S6, note.sixteenth-L5, barline-L1, note.quarter-L4, rest.EIGHTH-L3, note.eighth-L4, note.eighth-L4, note.eighth-L5, barline-L1, note.eighth-S4, note.eighth-S4, rest.EIGHTH-L3, note.eighth-S4, note.eighth-S4, note.eighth-S5, barline-L1, note.eighth-L5, note.eighth-L5, rest.QUARTER-L3, note.eighth-L5, dot-S5, note.sixteenth-L6, barline-L1, gracenote.eighth-L6, note.quarter-S5, dot-S5]";
        assertEquals(expected, stringFormat);
    }

    @Test
    public void convertPrimusTrill() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000100001-1_1_1/000100001-1_1_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song).toString();
        String expected = "[clef.G-L2, accidental.#-L5, metersign.C-L3, note.half-L4, slur.start-L4, slur.end-L4, note.eighth-L4, note.quarter-L4, note.beamedRight2-L4, note.beamedLeft2-S5, barline-L1, rest.SIXTEENTH-L3, trill, note.beamedRight1-L6, note.beamedLeft1-S5, note.quarter-S7, slur.start-S7, barline-L1, slur.end-S7, note.beamedRight1-S7, note.beamedLeft1-L7, note.quarter-S6, note.eighth-L6, slur.start-L6, slur.end-L6, note.beamedRight2-L6, note.beamedLeft2-S5, note.quarter-L4, slur.start-L4, slur.end-L4, note.eighth-L4]";
        assertEquals(expected, stringFormat);
    }
}