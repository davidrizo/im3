package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.TestScoreUtils;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class Semantic2ScoreSongTest {

    @Test
    public void convert() throws IM3Exception, IOException {
        //TODO Integrate into PrIMuSSemanticAgnosticExporterTest
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/primus/000051759-1_1_1/000051759-1_1_1.mei");
        ScoreSong meiSongImported = importer.importSong(file);
        Encoder encoder = new Encoder(AgnosticVersion.v2, false);
        encoder.encode(meiSongImported);

        //TODO Hacer esto mismo con agnostic 2 semantic
        Semantic2IMCore semantic2ScoreSong = new Semantic2IMCore();
        //SemanticConversionContext semanticConversionContext = new SemanticConversionContext(NotationType.eModern); //NOT used

        SemanticEncoding semanticEncoding = encoder.getSemanticEncoding();
        ScoreSong decodedFromSemantic = semantic2ScoreSong.convertToSingleVoicedSong(NotationType.eModern, semanticEncoding);

        System.out.println(meiSongImported.getAtomPitches().size());

        TestScoreUtils.Configuration testScoreConfiguration = new TestScoreUtils.Configuration();
        testScoreConfiguration.setAssertStems(false); // in semantic??
        testScoreConfiguration.setAssertExplicitAccidentals(false);

        MEISongExporter meiExporter = new MEISongExporter();
        meiExporter.exportSong(new File("/tmp/semantic.mei"), decodedFromSemantic);

        TestScoreUtils.checkEqual("mei", meiSongImported, "exported from semantic", decodedFromSemantic, testScoreConfiguration);

        String kern = encoder.getSemanticEncoding().generateKernSemanticString(NotationType.eModern);
        Files.write(Paths.get("/tmp", "semantic.krn"), Collections.singleton(kern));
    }
}
