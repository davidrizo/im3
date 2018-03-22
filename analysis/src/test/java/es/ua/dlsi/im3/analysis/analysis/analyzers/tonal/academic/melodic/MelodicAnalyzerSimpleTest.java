package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Test;

import java.io.File;

/**
 * Created by drizo on 13/6/17.
 */
public class MelodicAnalyzerSimpleTest {
    @Test
    public void melodicAnalysis() throws Exception {
        File file = TestFileUtils.getFile("/testdata/analysis/tonal/academic/simple_melodic.xml");
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(file);

        MelodicAnalyzerSimple analyzerSimple = new MelodicAnalyzerSimple("Simple");
        MelodicAnalysis melodicAnalysis = analyzerSimple.melodicAnalysis(song, null);
        melodicAnalysis.print();

        //TODO Assert values

        melodicAnalysis.putInSong();

        MEISongExporter exporter = new MEISongExporter();
        File meiFILE = TestFileUtils.createTempFile("mf.mei");
        exporter.exportSong(meiFILE, song);
    }

}