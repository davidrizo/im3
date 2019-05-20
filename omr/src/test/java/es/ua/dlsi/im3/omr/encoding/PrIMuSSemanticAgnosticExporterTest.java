package es.ua.dlsi.im3.omr.encoding;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.Semantic2ScoreSongImporter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class PrIMuSSemanticAgnosticExporterTest {
    static TestScoreUtils.Configuration testScoreConfiguration;

    @BeforeClass
    public static void init() {
        testScoreConfiguration = new TestScoreUtils.Configuration();
        testScoreConfiguration.setAssertStems(false); // in semantic??
        testScoreConfiguration.setAssertExplicitAccidentals(false);
    }

    void test(AgnosticVersion agnosticVersion, String filename, String expectedAgnostic, String expectedSemantic) throws IM3Exception, IOException {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile(filename);
        ScoreSong song = importer.importSong(file);
        Encoder encoder = new Encoder(agnosticVersion, false);
        encoder.encode(song);

        String agnosticStringFormat = new AgnosticExporter(agnosticVersion).export(encoder.getAgnosticEncoding());
        assertEquals("Agnostic", expectedAgnostic, agnosticStringFormat);

        String semanticStringFormat = new SemanticExporter().export(encoder.getSemanticEncoding());
        System.out.println(filename);
        System.out.println(semanticStringFormat);
        assertEquals(expectedSemantic, semanticStringFormat);

        // replace all "unknown" key signatures for Major
        for (Staff staff: song.getStaves()) {
            for (KeySignature keySignature: staff.getKeySignatures()) {
                if (keySignature.getInstrumentKey().getMode() == Mode.UNKNOWN) {
                    Key newKey = new Key(keySignature.getInstrumentKey().getFifths(), Mode.MAJOR);
                    keySignature.getInstrumentKey().overwriteWith(newKey);
                }
            }
        }

        Semantic2ScoreSongImporter semantic2ScoreSongImporter = new Semantic2ScoreSongImporter();
        ScoreSong importedSong = semantic2ScoreSongImporter.importSong(semanticStringFormat + "\n");

        TestScoreUtils.checkEqual("mei", song, "semantic", importedSong, testScoreConfiguration);

        MEISongExporter meiSongExporter = new MEISongExporter();
        File meiOut = TestFileUtils.createTempFile(FileUtils.getFileWithoutPathOrExtension(file) + "_semantic.mei");
        System.out.println(meiOut.getAbsolutePath());
        meiSongExporter.exportSong(meiOut, importedSong);

        MEISongImporter meiSongImporter = new MEISongImporter();
        ScoreSong meiSongImported = meiSongImporter.importSong(meiOut);
        TestScoreUtils.checkEqual("mei", song, "mei exported from semantic", meiSongImported, testScoreConfiguration);

    }
   /* @Test
    public void convertPrimusSampleLedgerLinesPrimusV1() throws Exception {
        // Currently we don't print ledger lines
        String expectedAgnostic = "clef.G-L2\taccidental.flat-L3\taccidental.flat-S4\taccidental.flat-S2\tdigit.3-L4\tdigit.4-L2\tnote.quarter-S6\tnote.beamedRight1-S4\tnote.beamedBoth1-S6\tnote.beamedBoth1-L7\tnote.beamedLeft1-S6\tbarline-L1\tnote.beamedRight1-L6\tnote.beamedLeft1-L6\trest.sixteenth-L3\tnote.beamedRight2-L6\tnote.beamedBoth2-S5\tnote.beamedLeft2-L6\tnote.beamedRight2-S6\tnote.beamedBoth2-L6\tnote.beamedBoth2-S5\tnote.beamedLeft2-L6\tbarline-L1";
        String expectedSemantic = "clef-G2 keySignature-EbM timeSignature-3/4 note-Bb5_quarter note-Eb5_eighth note-Bb5_eighth note-C6_eighth note-Bb5_eighth barline note-Ab5_eighth note-Ab5_eighth rest-sixteenth note-Ab5_sixteenth note-G5_sixteenth note-Ab5_sixteenth note-Bb5_sixteenth note-Ab5_sixteenth note-G5_sixteenth note-Ab5_sixteenth barline";
        test(AgnosticVersion.v1, "/testdata/primus/000051650-1_1_1/000051650-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }*/

    // stems ok
    @Test
    public void convertPrimusSampleLedgerLinesPrimusV2() throws Exception {
        // Currently we don't print ledger lines
        String expectedAgnostic = "clef.G:L2, accidental.flat:L3, accidental.flat:S4, accidental.flat:S2, digit.3:L4/digit.4:L2, note.quarter_down:S6, note.beamedRight1_down:S4, note.beamedBoth1_down:S6, note.beamedBoth1_down:L7, note.beamedLeft1_down:S6, verticalLine:L1, note.beamedRight1_down:L6, note.beamedLeft1_down:L6, rest.sixteenth:L3, note.beamedRight2_down:L6, note.beamedBoth2_down:S5, note.beamedLeft2_down:L6, note.beamedRight2_down:S6, note.beamedBoth2_down:L6, note.beamedBoth2_down:S5, note.beamedLeft2_down:L6, verticalLine:L1";
        String expectedSemantic = "clef-G2 keySignature-EbM timeSignature-3/4 note-Bb5_quarter note-Eb5_eighth note-Bb5_eighth note-C6_eighth note-Bb5_eighth barline note-Ab5_eighth note-Ab5_eighth rest-sixteenth note-Ab5_sixteenth note-G5_sixteenth note-Ab5_sixteenth note-Bb5_sixteenth note-Ab5_sixteenth note-G5_sixteenth note-Ab5_sixteenth barline";
        test(AgnosticVersion.v2, "/testdata/primus/000051650-1_1_1/000051650-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok. The MEI file contains the explicit stem direction
    @Test
    public void convertPrimusSampleWithAccidentalsAndFermata() throws Exception {
        String expectedAgnostic = "clef.C:L1, metersign.Ct:L3, note.quarter_down:S3, verticalLine:L1, note.quarter_down:S3, note.beamedRight2_up:L3, note.beamedBoth2_up:S2, note.beamedBoth2_up:L2, note.beamedLeft2_up:S2, note.quarter_up:L2, note.beamedRight1_down:S3, dot:S3, accidental.flat:L4, note.beamedLeft2_down:L4, verticalLine:L1, note.quarter_down:L3, slur.start:L3, slur.end:L3, note.beamedRight3_down:L3, accidental.flat:L4, note.beamedBoth3_down:L4, note.beamedBoth3_down:S3, note.beamedBoth3_down:L3, note.beamedBoth1_down:S2, note.beamedBoth3_down:L3, note.beamedLeft3_down:L2, fermata.above:S6/note.eighth_up:S2";
        String expectedSemantic = "clef-C1 timeSignature-Ct note-A4_quarter barline note-A4_quarter note-G4_sixteenth note-F4_sixteenth note-E4_sixteenth note-F4_sixteenth note-E4_quarter note-A4_eighth. note-Bb4_sixteenth barline note-G4_quarter tie note-G4_thirty_second note-Bb4_thirty_second note-A4_thirty_second note-G4_thirty_second note-F4_eighth note-G4_thirty_second note-E4_thirty_second note-F4_eighth_fermata";
        test(AgnosticVersion.v2, "/testdata/primus/000051806-1_1_1/000051806-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok. The MEI file contains the explicit stem direction
    @Test
    public void convertPrimusSampleBeams() throws Exception {
        String expectedAgnostic = "clef.G:L2, accidental.sharp:L5, accidental.sharp:S3, digit.2:L4/digit.4:L2, rest.sixteenth:L3, note.beamedRight2_up:S1, note.beamedBoth2_up:L2, note.beamedLeft2_up:S2, note.beamedRight1_up:S0, note.beamedLeft1_up:L4, slur.start:L4, verticalLine:L1, slur.end:L4, note.beamedRight1_down:L4, note.beamedBoth2_down:S3, note.beamedLeft2_down:L3, note.beamedRight2_down:S3, note.beamedBoth2_down:L4, note.beamedLeft1_down:S4, slur.start:S4, verticalLine:L1, slur.end:S4, note.beamedRight2_down:S4, note.beamedBoth2_down:S2, note.beamedBoth2_down:L3, note.beamedLeft2_down:S3";
        String expectedSemantic = "clef-G2 keySignature-DM timeSignature-2/4 rest-sixteenth note-F#4_sixteenth note-G4_sixteenth note-A4_sixteenth note-D4_eighth note-D5_eighth tie barline note-D5_eighth note-C#5_sixteenth note-B4_sixteenth note-C#5_sixteenth note-D5_sixteenth note-E5_eighth tie barline note-E5_sixteenth note-A4_sixteenth note-B4_sixteenth note-C#5_sixteenth";
        test(AgnosticVersion.v2, "/testdata/primus/000051759-1_1_1/000051759-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertPrimusMultimeasure() throws Exception {
        String expectedAgnostic = "clef.C:L1, accidental.flat:L4, accidental.flat:L2, accidental.flat:S3, digit.3:L4/digit.4:L2, multirest:L3, digit.1:S5, digit.0:S5, verticalLine:L1, note.quarter_down:L4, note.eighth_up:L2, note.eighth_down:S5, note.eighth_down:S5, note.eighth_down:S4, verticalLine:L1, note.eighth_down:S3, note.eighth_down:S3, rest.quarter:L3, rest.quarter:L3, verticalLine:L1, note.quarter_down:L6, note.eighth_down:S5, dot:S5, note.sixteenth_down:S4, note.eighth_down:L4, dot:S4, note.sixteenth_down:S3, verticalLine:L1, note.eighth_down:S3, note.eighth_down:L3, rest.quarter:L3";
        String expectedSemantic = "clef-C1 keySignature-EbM timeSignature-3/4 multirest-10 barline note-Bb4_quarter note-Eb4_eighth note-Eb5_eighth note-Eb5_eighth note-C5_eighth barline note-Ab4_eighth note-Ab4_eighth rest-quarter rest-quarter barline note-F5_quarter note-Eb5_eighth. note-C5_sixteenth note-Bb4_eighth. note-Ab4_sixteenth barline note-Ab4_eighth note-G4_eighth rest-quarter";
        test(AgnosticVersion.v2, "/testdata/primus/000051650-1_1_2/000051650-1_1_2.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertPrimusMultimeasureDoubleWholeGlyph() throws Exception {
        String expectedAgnostic = "clef.C:L1, accidental.flat:L4, accidental.flat:L2, accidental.flat:S3, metersign.Ct:L3, fermata.above:S6/rest.whole:L4, verticalLine:L1, note.half_down:S5, note.quarter_down:L4, dot:S4, note.eighth_down:L3, verticalLine:L1, note.quarter_up:L2, note.quarter_up:L2, fermata.above:S6/rest.half:L3, verticalLine:L1, digit.2:S5/rest.breve:L3, verticalLine:L1, note.half_down:L6, note.quarter_down:L5, dot:S5, note.eighth_down:L4, verticalLine:L1, note.quarter_down:S3, note.quarter_down:S3, fermata.above:S6/rest.half:L3, verticalLine:L1";
        String expectedSemantic = "clef-C1 keySignature-EbM timeSignature-Ct rest-whole_fermata barline note-Eb5_half note-Bb4_quarter. note-G4_eighth barline note-Eb4_quarter note-Eb4_quarter rest-half_fermata barline multirest-2 barline note-F5_half note-D5_quarter. note-Bb4_eighth barline note-Ab4_quarter note-Ab4_quarter rest-half_fermata barline";
        test(AgnosticVersion.v2, "/testdata/primus/000051653-1_2_1/000051653-1_2_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertPrimusAcciaccatura() throws Exception {
        String expectedAgnostic = "clef.C:L1, accidental.flat:L4, accidental.flat:L2, digit.3:L4/digit.4:L2, note.eighth_down:L6, dot:S6, note.sixteenth_down:L5, verticalLine:L1, note.quarter_down:L4, rest.eighth:L3, note.eighth_down:L4, note.eighth_down:L4, note.eighth_down:L5, verticalLine:L1, note.eighth_down:S4, note.eighth_down:S4, rest.eighth:L3, note.eighth_down:S4, note.eighth_down:S4, note.eighth_down:S5, verticalLine:L1, note.eighth_down:L5, note.eighth_down:L5, rest.quarter:L3, note.eighth_down:L5, dot:S5, note.sixteenth_down:L6, verticalLine:L1, gracenote.eighth_up:L6, note.quarter_down:S5, dot:S5";
        String expectedSemantic = "clef-C1 keySignature-BbM timeSignature-3/4 note-F5_eighth. note-D5_sixteenth barline note-Bb4_quarter rest-eighth note-Bb4_eighth note-Bb4_eighth note-D5_eighth barline note-C5_eighth note-C5_eighth rest-eighth note-C5_eighth note-C5_eighth note-Eb5_eighth barline note-D5_eighth note-D5_eighth rest-quarter note-D5_eighth. note-F5_sixteenth barline gracenote-F5_eighth note-Eb5_quarter.";
        test(AgnosticVersion.v2, "/testdata/primus/000051660-1_2_1/000051660-1_2_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertPrimusTrill() throws Exception {
        String expectedAgnostic = "clef.G:L2, accidental.sharp:L5, metersign.Ct:L3, note.half_down:L4, slur.start:L4, slur.end:L4, note.eighth_down:L4, note.quarter_down:L4, note.beamedRight2_down:L4, note.beamedLeft2_down:S5, verticalLine:L1, rest.sixteenth:L3, trill:S6/note.beamedRight1_down:L6, note.beamedLeft1_down:S5, note.quarter_down:S7, slur.start:S7, verticalLine:L1, slur.end:S7, note.beamedRight1_down:S7, note.beamedLeft1_down:L7, note.quarter_down:S6, note.eighth_down:L6, slur.start:L6, slur.end:L6, note.beamedRight2_down:L6, note.beamedLeft2_down:S5, note.quarter_down:L4, slur.start:L4, slur.end:L4, note.eighth_down:L4";
        String expectedSemantic = "clef-G2 keySignature-GM timeSignature-Ct note-D5_half tie note-D5_eighth note-D5_quarter note-D5_sixteenth note-G5_sixteenth barline rest-sixteenth note-A5_eighth_trill note-G5_eighth note-D6_quarter tie barline note-D6_eighth note-C6_eighth note-B5_quarter note-A5_eighth tie note-A5_sixteenth note-G5_sixteenth note-D5_quarter tie note-D5_eighth";
        test(AgnosticVersion.v2, "/testdata/primus/000100001-1_1_1/000100001-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertG1() throws Exception {
        String expectedAgnostic = "clef.G:L1, accidental.flat:L2, metersign.Ct:L3, note.beamedRight2_down:L4, note.beamedBoth2_up:S1, note.beamedBoth2_up:L1, note.beamedLeft2_up:S0, note.beamedRight2_up:S0, note.beamedBoth2_up:S1, note.beamedBoth2_up:L1, note.beamedLeft2_up:S0, note.beamedRight2_up:S0, note.beamedBoth2_up:S1, note.beamedBoth2_up:L1, note.beamedLeft2_up:S0, note.beamedRight2_up:S0, note.beamedBoth2_up:L1, note.beamedBoth2_up:S1, accidental.natural:L2, note.beamedLeft2_up:L2, verticalLine:L1";
        String expectedSemantic = "clef-G1 keySignature-FM timeSignature-Ct note-F5_sixteenth note-A4_sixteenth note-G4_sixteenth note-F4_sixteenth note-F4_sixteenth note-A4_sixteenth note-G4_sixteenth note-F4_sixteenth note-F4_sixteenth note-A4_sixteenth note-G4_sixteenth note-F4_sixteenth note-F4_sixteenth note-G4_sixteenth note-A4_sixteenth note-B4_sixteenth barline";
        test(AgnosticVersion.v2, "/testdata/primus/000108149-1_1_1/000108149-1_1_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertC1KeySignatureAndExplicitAccidentals() throws Exception {
        String expectedAgnostic = "clef.C:L1, accidental.sharp:S2, accidental.sharp:L1, accidental.sharp:L3, metersign.Ccut:L3, rest.eighth:L3, accidental.sharp:L3, note.eighth_down:L3, accidental.sharp:S4, note.eighth_down:S4, note.eighth_down:S4, rest.quarter:L3, accidental.sharp:S4, note.quarter_down:S4, accidental.sharp:L3, note.quarter_down:L3, verticalLine:L1, note.quarter_down:S3, rest.quarter:L3, note.quarter_down:S3, note.eighth_down:L4, accidental.sharp:S4, note.eighth_down:S4, verticalLine:L1, note.eighth_down:L4, note.eighth_down:L4";
        String expectedSemantic = "clef-C1 keySignature-AM timeSignature-Ccut rest-eighth note-G#4_eighth note-C#5_eighth note-C#5_eighth rest-quarter note-C#5_quarter note-G#4_quarter barline note-A4_quarter rest-quarter note-A4_quarter note-B4_eighth note-C#5_eighth barline note-B4_eighth note-B4_eighth";
        test(AgnosticVersion.v2, "/testdata/primus/000102277-1_2_1/000102277-1_2_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertClefChanges() throws Exception {
        String expectedAgnostic = "clef.C:L4, accidental.flat:S3, metersign.Ct:L3, rest.eighth:L3, note.eighth_down:L4, note.beamedRight1_down:S5, note.beamedBoth2_down:L5, note.beamedLeft2_down:S4, note.beamedRight1_down:L4, note.beamedBoth2_down:S4, note.beamedLeft2_down:L4, note.beamedRight2_down:S3, note.beamedBoth2_down:S4, note.beamedBoth2_down:L4, note.beamedLeft2_down:S3, clef.F:L4, note.beamedRight1_down:L5, note.beamedBoth2_down:S5, note.beamedLeft2_down:L5, note.beamedRight2_down:S4, note.beamedBoth2_down:S5, note.beamedBoth2_down:L5, note.beamedLeft2_down:S4, note.beamedRight2_down:L4, note.beamedBoth2_down:L5, note.beamedBoth2_down:S4, note.beamedLeft2_down:L4, note.beamedRight1_down:L6, note.beamedLeft1_up:S2, verticalLine:L1, note.quarter_down:L4";
        String expectedSemantic = "clef-C4 keySignature-FM timeSignature-Ct rest-eighth note-C4_eighth note-F4_eighth note-E4_sixteenth note-D4_sixteenth note-C4_eighth note-D4_sixteenth note-C4_sixteenth note-Bb3_sixteenth note-D4_sixteenth note-C4_sixteenth note-Bb3_sixteenth clef-F4 note-A3_eighth note-Bb3_sixteenth note-A3_sixteenth note-G3_sixteenth note-Bb3_sixteenth note-A3_sixteenth note-G3_sixteenth note-F3_sixteenth note-A3_sixteenth note-G3_sixteenth note-F3_sixteenth note-C4_eighth note-C3_eighth barline note-F3_quarter";
        test(AgnosticVersion.v2, "/testdata/primus/200021502-1_11_1/200021502-1_11_1.mei", expectedAgnostic, expectedSemantic);
    }

    // stems ok
    @Test
    public void convertDifferentMultirests() throws Exception {
        String expectedAgnostic = "clef.C:L2, accidental.sharp:S3, accidental.sharp:L2, accidental.sharp:L4, digit.2:L4/digit.4:L2, multirest:L3, digit.2:S5, digit.0:S5, verticalLine:L1, note.quarter_down:S4, note.quarter_down:L3, verticalLine:L1, digit.1:S5/rest.whole:L4, verticalLine:L1, note.quarter_down:S4, note.quarter_down:L3, verticalLine:L1, rest.quarter:L3, verticalLine:L1, note.quarter_down:S5, note.quarter_down:S5, verticalLine:L1, note.quarter_down:S5, dot:S5, note.eighth_down:L6, verticalLine:L1, note.beamedRight1_down:S5, note.beamedLeft1_down:L5, note.beamedRight1_down:S4, note.beamedLeft1_down:L4, verticalLine:L1";
        String expectedSemantic = "clef-C2 keySignature-AM timeSignature-2/4 multirest-20 barline note-A4_quarter note-E4_quarter barline multirest-1 barline note-A4_quarter note-E4_quarter barline rest-quarter barline note-C#5_quarter note-C#5_quarter barline note-C#5_quarter. note-D5_eighth barline note-C#5_eighth note-B4_eighth note-A4_eighth note-G#4_eighth barline";
        test(AgnosticVersion.v2, "/testdata/primus/000100196-1_2_2/000100196-1_2_2.mei", expectedAgnostic, expectedSemantic);
    }

}

