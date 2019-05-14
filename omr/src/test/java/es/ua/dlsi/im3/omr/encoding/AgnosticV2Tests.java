package es.ua.dlsi.im3.omr.encoding;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @autor drizo
 */
public class AgnosticV2Tests {
    private void doTest(String meiFileName, String expectedAgnostic) throws IM3Exception {
        File file = TestFileUtils.getFile("/testdata/agnosticv2", meiFileName);
        MEISongImporter importer = new MEISongImporter();
        ScoreSong song = importer.importSong(file);
        Encoder encoder = new Encoder(AgnosticVersion.v2, false);
        encoder.encode(song);

        String agnosticStringFormat = new AgnosticExporter(AgnosticVersion.v2).export(encoder.getAgnosticEncoding());
        System.out.println(agnosticStringFormat);
        assertEquals("Agnostic", expectedAgnostic, agnosticStringFormat);
    }

    @Test
    public void testChord() throws IM3Exception {
        String filename = "chord.mei";
        String expected = "clef.G:L2, digit.3:L4/digit.4:L2, note.half:L0/note.half:L1/note.half:L2, slur.start:L0/slur.start:L1/slur.start:L2, slur.end:L0/slur.end:L1/slur.end:L2, fermata.above:S6/trill:S6/note.half:L0/note.half:L1/note.half:L2";
        doTest(filename, expected);
    }

    @Test
    public void testTupletWithoutBracket() throws IM3Exception {
        String filename = "tuplet_without_bracket.mei";
        String expected = "clef.G:L2, digit.3:L4/digit.4:L2, note.beamedRight1_up:L0, digit.3:S6, note.beamedBoth1_up:S0, note.beamedLeft1_up:L1, rest.half:L3, verticalLine:L1";
        doTest(filename, expected);
    }
    @Test
    public void testTupletWithtBracket() throws IM3Exception {
        String filename = "tuplet_with_bracket.mei";
        String expected = "clef.G:L2, digit.3:L4/digit.4:L2, bracket.start:S6/note.eighth_up:L0, digit.3:S6/note.eighth_up:S0, bracket.end:S6/note.eighth_up:L1";
        doTest(filename, expected);
    }


}
