package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MEI2GraphicSymbolsTest {
    void test(String filename, String expectedAgnostic, String expectedSemantic) throws IM3Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile(filename);
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String agnosticStringFormat = conversor.convert(song).getTokens().toString();
        assertEquals(expectedAgnostic, agnosticStringFormat);

        String semanticStringFormat = conversor.convert(song).getSemanticTokens().toString();
        System.out.println(filename);
        System.out.println(semanticStringFormat);
        assertEquals(expectedSemantic, semanticStringFormat);

    }
    @Test
    public void convertPrimusSampleLedgerLines() throws Exception {
        // Currently we don't print ledger lines
        String expectedAgnostic = "[clef.G-L2, accidental.flat-L3, accidental.flat-S4, accidental.flat-S2, digit.3-L4, digit.4-L2, note.quarter-S6, note.beamedRight1-S4, note.beamedBoth1-S6, note.beamedBoth1-L7, note.beamedLeft1-S6, barline-L1, note.beamedRight1-L6, note.beamedLeft1-L6, rest.sixteenth-L3, note.beamedRight2-L6, note.beamedBoth2-S5, note.beamedLeft2-L6, note.beamedRight2-S6, note.beamedBoth2-L6, note.beamedBoth2-S5, note.beamedLeft2-L6, barline-L1]";
        String expectedSemantic = "[clef-G2, keySignature-EbM, timeSignature-3/4, note-Bb5_quarter, note-Eb5_eighth, note-Bb5_eighth, note-C6_eighth, note-Bb5_eighth, barline, note-Ab5_eighth, note-Ab5_eighth, rest-sixteenth, note-Ab5_sixteenth, note-G5_sixteenth, note-Ab5_sixteenth, note-Bb5_sixteenth, note-Ab5_sixteenth, note-G5_sixteenth, note-Ab5_sixteenth, barline]";
        test("/testdata/primus/000051650-1_1_1/000051650-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    @Test
    public void convertPrimusSampleWithAccidentalsAndFermata() throws Exception {
        String expectedAgnostic = "[clef.C-L1, metersign.C-L3, note.quarter-S3, barline-L1, note.quarter-S3, note.beamedRight2-L3, note.beamedBoth2-S2, note.beamedBoth2-L2, note.beamedLeft2-S2, note.quarter-L2, note.beamedRight1-S3, dot-S3, accidental.flat-L4, note.beamedLeft2-L4, barline-L1, note.quarter-L3, slur.start-L3, slur.end-L3, note.beamedRight3-L3, accidental.flat-L4, note.beamedBoth3-L4, note.beamedBoth3-S3, note.beamedBoth3-L3, note.beamedBoth1-S2, note.beamedBoth3-L3, note.beamedLeft3-L2, fermata.above, note.eighth-S2]";
        String expectedSemantic = "[clef-C1, timeSignature-C, note-A4_quarter, barline, note-A4_quarter, note-G4_sixteenth, note-F4_sixteenth, note-E4_sixteenth, note-F4_sixteenth, note-E4_quarter, note-A4_eighth., note-Bb4_sixteenth, barline, note-G4_quarter, tie, note-G4_thirty_second, note-Bb4_thirty_second, note-A4_thirty_second, note-G4_thirty_second, note-F4_eighth, note-G4_thirty_second, note-E4_thirty_second, note-F4_eighth_fermata]";
        test("/testdata/primus/000051806-1_1_1/000051806-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    @Test
    public void convertPrimusSampleBeams() throws Exception {
        String expectedAgnostic = "[clef.G-L2, accidental.sharp-L5, accidental.sharp-S3, digit.2-L4, digit.4-L2, rest.sixteenth-L3, note.beamedRight2-S1, note.beamedBoth2-L2, note.beamedLeft2-S2, note.beamedRight1-S0, note.beamedLeft1-L4, slur.start-L4, barline-L1, slur.end-L4, note.beamedRight1-L4, note.beamedBoth2-S3, note.beamedLeft2-L3, note.beamedRight2-S3, note.beamedBoth2-L4, note.beamedLeft1-S4, slur.start-S4, barline-L1, slur.end-S4, note.beamedRight2-S4, note.beamedBoth2-S2, note.beamedBoth2-L3, note.beamedLeft2-S3]";
        String expectedSemantic = "[clef-G2, keySignature-DM, timeSignature-2/4, rest-sixteenth, note-F#4_sixteenth, note-G4_sixteenth, note-A4_sixteenth, note-D4_eighth, note-D5_eighth, tie, barline, note-D5_eighth, note-C#5_sixteenth, note-B4_sixteenth, note-C#5_sixteenth, note-D5_sixteenth, note-E5_eighth, tie, barline, note-E5_sixteenth, note-A4_sixteenth, note-B4_sixteenth, note-C#5_sixteenth]";
        test("/testdata/primus/000051759-1_1_1/000051759-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    @Test
    public void convertPrimusMultimeasure() throws Exception {
        String expectedAgnostic = "[clef.C-L1, accidental.flat-L4, accidental.flat-L2, accidental.flat-S3, digit.3-L4, digit.4-L2, digit.1-S5, digit.0-S5, multirest-L3, barline-L1, note.quarter-L4, note.eighth-L2, note.eighth-S5, note.eighth-S5, note.eighth-S4, barline-L1, note.eighth-S3, note.eighth-S3, rest.quarter-L3, rest.quarter-L3, barline-L1, note.quarter-L6, note.eighth-S5, dot-S5, note.sixteenth-S4, note.eighth-L4, dot-S4, note.sixteenth-S3, barline-L1, note.eighth-S3, note.eighth-L3, rest.quarter-L3]";
        String expectedSemantic = "[clef-C1, keySignature-EbM, timeSignature-3/4, multirest-10, barline, note-Bb4_quarter, note-Eb4_eighth, note-Eb5_eighth, note-Eb5_eighth, note-C5_eighth, barline, note-Ab4_eighth, note-Ab4_eighth, rest-quarter, rest-quarter, barline, note-F5_quarter, note-Eb5_eighth., note-C5_sixteenth, note-Bb4_eighth., note-Ab4_sixteenth, barline, note-Ab4_eighth, note-G4_eighth, rest-quarter]";
        test("/testdata/primus/000051650-1_1_2/000051650-1_1_2.mei", expectedAgnostic, expectedSemantic);
    }

    @Test
    public void convertPrimusMultimeasureDoubleWholeGlyph() throws Exception { 
        String expectedAgnostic = "[clef.C-L1, accidental.flat-L4, accidental.flat-L2, accidental.flat-S3, metersign.C-L3, fermata.above, rest.whole-L4, barline-L1, note.half-S5, note.quarter-L4, dot-S4, note.eighth-L3, barline-L1, note.quarter-L2, note.quarter-L2, fermata.above, rest.half-L3, barline-L1, digit.2-S5, multirest-L3, barline-L1, note.half-L6, note.quarter-L5, dot-S5, note.eighth-L4, barline-L1, note.quarter-S3, note.quarter-S3, fermata.above, rest.half-L3, barline-L1]";
        String expectedSemantic = "[clef-C1, keySignature-EbM, timeSignature-C, rest-whole_fermata, barline, note-Eb5_half, note-Bb4_quarter., note-G4_eighth, barline, note-Eb4_quarter, note-Eb4_quarter, rest-half_fermata, barline, multirest-2, barline, note-F5_half, note-D5_quarter., note-Bb4_eighth, barline, note-Ab4_quarter, note-Ab4_quarter, rest-half_fermata, barline]";
        test("/testdata/primus/000051653-1_2_1/000051653-1_2_1.mei", expectedAgnostic, expectedSemantic);
    }

    @Test
    public void convertPrimusAcciaccatura() throws Exception {
        String expectedAgnostic = "[clef.C-L1, accidental.flat-L4, accidental.flat-L2, digit.3-L4, digit.4-L2, note.eighth-L6, dot-S6, note.sixteenth-L5, barline-L1, note.quarter-L4, rest.eighth-L3, note.eighth-L4, note.eighth-L4, note.eighth-L5, barline-L1, note.eighth-S4, note.eighth-S4, rest.eighth-L3, note.eighth-S4, note.eighth-S4, note.eighth-S5, barline-L1, note.eighth-L5, note.eighth-L5, rest.quarter-L3, note.eighth-L5, dot-S5, note.sixteenth-L6, barline-L1, gracenote.eighth-L6, note.quarter-S5, dot-S5]";
        String expectedSemantic = "[clef-C1, keySignature-BbM, timeSignature-3/4, note-F5_eighth., note-D5_sixteenth, barline, note-Bb4_quarter, rest-eighth, note-Bb4_eighth, note-Bb4_eighth, note-D5_eighth, barline, note-C5_eighth, note-C5_eighth, rest-eighth, note-C5_eighth, note-C5_eighth, note-Eb5_eighth, barline, note-D5_eighth, note-D5_eighth, rest-quarter, note-D5_eighth., note-F5_sixteenth, barline, gracenote-F5_eighth, note-Eb5_quarter.]";
        test("/testdata/primus/000051660-1_2_1/000051660-1_2_1.mei", expectedAgnostic, expectedSemantic);
    }

    @Test
    public void convertPrimusTrill() throws Exception {
        String expectedAgnostic = "[clef.G-L2, accidental.sharp-L5, metersign.C-L3, note.half-L4, slur.start-L4, slur.end-L4, note.eighth-L4, note.quarter-L4, note.beamedRight2-L4, note.beamedLeft2-S5, barline-L1, rest.sixteenth-L3, trill, note.beamedRight1-L6, note.beamedLeft1-S5, note.quarter-S7, slur.start-S7, barline-L1, slur.end-S7, note.beamedRight1-S7, note.beamedLeft1-L7, note.quarter-S6, note.eighth-L6, slur.start-L6, slur.end-L6, note.beamedRight2-L6, note.beamedLeft2-S5, note.quarter-L4, slur.start-L4, slur.end-L4, note.eighth-L4]";
        String expectedSemantic = "[clef-G2, keySignature-GM, timeSignature-C, note-D5_half, tie, note-D5_eighth, note-D5_quarter, note-D5_sixteenth, note-G5_sixteenth, barline, rest-sixteenth, note-A5_eighth_trill, note-G5_eighth, note-D6_quarter, tie, barline, note-D6_eighth, note-C6_eighth, note-B5_quarter, note-A5_eighth, tie, note-A5_sixteenth, note-G5_sixteenth, note-D5_quarter, tie, note-D5_eighth]";
        test("/testdata/primus/000100001-1_1_1/000100001-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }
}