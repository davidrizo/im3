package es.ua.dlsi.im3.omr.language.modern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.omr.io.AgnosticExporter;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.primus.conversions.ScoreGraphicalDescription;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class GraphicalModernSymbolsAutomatonTest {
    void test(String filename) throws IM3Exception {
        File file = TestFileUtils.getFile("/testdata/automaton/modern/" + filename);
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(file);

        AgnosticExporter mei2GraphicSymbols = new AgnosticExporter();
        ScoreGraphicalDescription scoreGraphicalDescription = mei2GraphicSymbols.convert(song);
        List<GraphicalToken> graphicalSymbols = scoreGraphicalDescription.getTokens();

        GraphicalModernSymbolsAutomaton automaton = new GraphicalModernSymbolsAutomaton();
        System.out.println(scoreGraphicalDescription.getTokens().toString());
        OMRTransduction transduction = automaton.probabilityOf(graphicalSymbols);
        assertTrue("File " + filename + " has non 0 probability", transduction.getProbability().getNumerator().longValue() != 0);
        //sacamos el listado de los elementos

    }


    @Test
    public void test1() throws IM3Exception {
        test("test1.xml"); //Silencios
    }

    @Test
    public void test2() throws IM3Exception {
        test("test2.xml");
    }

    @Test
    public void test3() throws IM3Exception {
        //Compasillo
        test("test3.xml");
    }
    @Test
    public void test4() throws IM3Exception {
        //sostenido
        test("test4.xml");
    }
    @Test
    public void test5() throws IM3Exception {
        //bemol
        test("test5.xml");
    }
    @Test
    public void test6() throws IM3Exception {
        //varios sostenidos
        test("test6.xml");
    }
    @Test
    public void test7() throws IM3Exception {
        //varios bemoles
        test("test7.xml");
    }
    @Test
    public void test8() throws IM3Exception {
        //varios bemoles
        test("test8.xml");
    }
    @Test
    public void test9() throws IM3Exception {
        //varios bemoles
        test("test9.xml");
    }
    @Test
    public void test10() throws IM3Exception {
        //varios bemoles
        test("test10.xml");
    }
    @Test
    public void test11() throws IM3Exception {
        //varios bemoles
        test("test11.xml");
    }
    @Test
    public void test12() throws IM3Exception {
        //varios bemoles
        test("test12.xml");
    }
    @Test
    public void test13() throws IM3Exception {
        //varios bemoles
        test("test13.xml");
    }
    @Test
    public void test14() throws IM3Exception {
        //varios bemoles
        test("test14.xml");
    }
    @Test
    public void test15() throws IM3Exception {
        //varios bemoles
        test("test15.xml");
    }
    @Test
    public void test16() throws IM3Exception {
        //varios bemoles
        test("test16.xml");
    }
    @Test
    public void test17() throws IM3Exception {
        //varios bemoles
        test("test17.xml");
    }
    @Test
    public void test18() throws IM3Exception {
        //varios bemoles
        test("test18.xml");
    }
    @Test
    public void test19() throws IM3Exception {
        //varios bemoles
        test("test19.xml");
    }
    @Test
    public void test20() throws IM3Exception {
        //varios bemoles
        test("test20.xml");
    }
    @Test
    public void test21() throws IM3Exception {
        //varios bemoles
        test("test21.xml");
    }
    @Test
    public void test22() throws IM3Exception {
        //varios bemoles
        test("test22.xml");
    }
    @Test
    public void test23() throws IM3Exception {
        //varios bemoles
        test("test23.xml");
    }
}