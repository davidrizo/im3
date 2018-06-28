package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.TestScoreUtils;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import org.junit.Test;

import java.io.File;

/**
 * We check here that we can import / export from / to Kern / MEI
 * @autor drizo
 */
public class PatriarcaTest {
    //@Test
    public void testImportExport() throws IM3Exception {
        // TODO: 30/4/18 Poner este fichero en el github cuando lo tengamos acabado y publicado
        File inputMEI = new File("/Users/drizo/Documents/EASD.A/docencia/alicante-2017-2018/inv/imagenes_patriarca/PATRIARCA2017/patriarca.mei");
        MEISongImporter meiImporter = new MEISongImporter();
        ScoreSong meiSong = meiImporter.importSong(inputMEI);
        File outputMEI = TestFileUtils.createTempFile("outputpatriarca.mei");
        File outputKern = TestFileUtils.createTempFile("outputpatriarca.krn");

        MEISongExporter meiSongExporter = new MEISongExporter();
        meiSongExporter.exportSong(outputMEI, meiSong);
        KernExporter kernExporter = new KernExporter();
        kernExporter.exportSong(outputKern, meiSong);

        MEISongImporter meiImporter2 = new MEISongImporter();
        ScoreSong importedMEI = meiImporter2.importSong(outputMEI);
        TestScoreUtils.checkEqual("original mei (vs mei)", meiSong, "exported mei", importedMEI);

        KernImporter kernImporter = new KernImporter();
        ScoreSong importedKern = kernImporter.importSong(outputKern);
        TestScoreUtils.checkEqual("original mei (vs krn)", meiSong, "exported krn", importedKern);
    }
}
