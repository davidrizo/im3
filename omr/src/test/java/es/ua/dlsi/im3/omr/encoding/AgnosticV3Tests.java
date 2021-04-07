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
public class AgnosticV3Tests {
  private void doTest(String meiFileName, String expectedAgnostic) throws IM3Exception {
    File file = TestFileUtils.getFile("/testdata/agnosticv3", meiFileName);
    MEISongImporter importer = new MEISongImporter();
    ScoreSong song = importer.importSong(file);
    Encoder encoder = new Encoder(AgnosticVersion.v3_advance, false, false, false);
    encoder.encode(song);

    String agnosticStringFormat = new AgnosticExporter(AgnosticVersion.v3_advance).export(encoder.getAgnosticEncoding());
    System.out.println(agnosticStringFormat);
    assertEquals("Agnostic", expectedAgnostic, agnosticStringFormat);
  }

  @Test
  public void testAgnosticV3() throws IM3Exception {
    String filename = "agnosticv3.mei";
    String expected = "clef.G:L2 + accidental.sharp:L5 + accidental.sharp:S3 + digit.3:L4 digit.4:L2 + accidental.flat:L2 accidental.sharp:S0 + note.eighth:L4 note.eighth:L2 note.eighth:S0 + dot:S4 dot:S2 dot:S0 + rest.sixteenth:L3 + note.beamedRight1_down:L4 + dot:S4 + note.beamedBoth2_down:L4 + note.beamedBoth1:L6 note.beamedBoth1:L4 + dot:S6 dot:S4 + note.beamedLeft2:S5 note.beamedLeft2:S4 + verticalLine:L1 + note.beamedRight1_down:S4 + note.beamedBoth1:L6 note.beamedBoth1:L5 digit.3:S-1 + note.beamedLeft1_down:S5 + verticalLine:L1 + note.eighth_down:S4 bracket.start:S-1 + rest.eighth:L3 digit.3:S-1 + note.eighth_down:L6 bracket.end:S-1 + verticalLine:L1 + note.beamedRight1_up:L1 + digit.3:S6 note.beamedBoth1:S2 note.beamedBoth1:S1 + note.beamedLeft1_up:L2 + verticalLine:L1 + bracket.start:S6 note.eighth_up:L1 + digit.3:S6 rest.eighth:L3 + bracket.end:S6 note.eighth_up:S2";
    doTest(filename, expected);
  }
}
