package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.analyzers.tonal.ImporterExporter;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.interactivepm.AccuracyManager;
import org.junit.Test;
import weka.classifiers.trees.RandomForest;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by drizo on 23/6/17.
 */
public class MelodicAnalyzerWekaTest {
    @Test
    public void test() throws MelodicAnalysisException, ImportException {
        MelodicAnalyzerWeka melodicAnalyzerWeka = new MelodicAnalyzerWeka("Random Forest", new RandomForest(), false, 1, false,
                EClassificationMode.eAll);

        File[] trainingFiles = new File [] {
                TestFileUtils.getFile("/testdata/analysis/tonal/academic/euromac2014/musicxml/272.xml")
        };

        File testFile = TestFileUtils.getFile("/testdata/analysis/tonal/academic/euromac2014/musicxml/26.xml");

        melodicAnalyzerWeka.learn(trainingFiles, null);
        ImporterExporter importer = new ImporterExporter();
        ScoreSong testSong = importer.readMusicXML(testFile, false);
        MelodicAnalysis ma = melodicAnalyzerWeka.melodicAnalysis(testSong, null);


        //TODO Algún assert
        //ma.print();


        MelodicAnalysis expected = new MelodicAnalysis(testSong);
        expected.loadFromSong();
        Accuracy accuracy = new Accuracy(expected, ma);
        System.out.println("Success rate: " + accuracy.getSuccessRate());
        System.out.println("Notes without expected analysis: " + accuracy.getNotesWithoutExpectedAnalysis());
        System.out.println("OK=" + accuracy.getOk());
        System.out.println("KO=" + accuracy.getKo());
        //expected.print();
    }

}