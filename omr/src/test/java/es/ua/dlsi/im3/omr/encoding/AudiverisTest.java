package es.ua.dlsi.im3.omr.encoding;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import org.junit.Test;

import java.io.File;

/**
 * Read from audiveris output MusicXML
 * @autor drizo
 */
public class AudiverisTest {
    @Test
    public void readMusicXMLOutput() throws IM3Exception {
        File file = TestFileUtils.getFile("/testdata/primus/audiveris/000051652-1_2_1.xml");
        ScoreSongImporter importer = new ScoreSongImporter();
        Encoder encoder = new Encoder(AgnosticVersion.v1, false, false, false);
        encoder.encode(importer.importSong(file));
    }
}
