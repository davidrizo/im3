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
        String stringFormat = conversor.convert(song);
        assertEquals("clef-L2\taccidental.#-L5\taccidental.#-S3\ttimeSig.4-L4\ttimeSig.4-L2\tnote.WHOLE-L2\tbarline-L1\tnote.QUARTER-S1\trest.QUARTER-L3\taccidental.#-L2\tnote.EIGHTH-L2\trest.EIGHTH-L3\tnote.QUARTER-L6\tbarline-L1",
                stringFormat);
    }

    @Test
    public void convertPrimusSample() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051650-1_1_1/000051650-1_1_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song);
        System.out.println(stringFormat);
    }

    @Test
    public void convertPrimusSampleWithAccidentals() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051806-1_1_1/000051806-1_1_1.mei");
        ScoreSong song = importer.importSong(file);
        MEI2GraphicSymbols conversor = new MEI2GraphicSymbols();
        String stringFormat = conversor.convert(song);
        System.out.println(stringFormat);
    }

}