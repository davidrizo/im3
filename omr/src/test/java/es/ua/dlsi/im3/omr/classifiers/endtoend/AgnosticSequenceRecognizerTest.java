package es.ua.dlsi.im3.omr.classifiers.endtoend;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AgnosticSequenceRecognizerTest {

    //@Test
    public void recognize() throws IOException, IM3Exception {
        File input = TestFileUtils.getFile("/testdata/imageprocessing/48.jpg");
        AgnosticSequenceRecognizerOLD agnosticSequenceRecognizer = new AgnosticSequenceRecognizerOLD();

        //TODO De momento sólo funciona en el ordenador de David - está pendiente de migración
        if (System.getProperty("user.home").equals("/Users/drizo")) {
            List<AgnosticSymbol> recognize = agnosticSequenceRecognizer.recognize(input);
            System.out.println(recognize); //TODO assert
        }
    }
}