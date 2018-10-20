package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class HumdrumMatrix2ScoreSongTest {

    @Test
    public void convert() throws IM3Exception {
        File file = TestFileUtils.getFile("/testdata/core/score/io/kern/e_oric06_18v_extract.mens");
        MensImporter importer = new MensImporter();
        HumdrumMatrix humdrumMatrix = importer.importMens(file);

        HumdrumMatrix2ScoreSong humdrumMatrix2ScoreSong = new HumdrumMatrix2ScoreSong();
        ScoreSong song = humdrumMatrix2ScoreSong.convert(humdrumMatrix);
    }
}