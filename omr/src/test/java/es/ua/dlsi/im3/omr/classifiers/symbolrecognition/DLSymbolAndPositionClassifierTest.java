package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DLSymbolAndPositionClassifierTest {

    @Test
    public void recognize() throws IM3Exception {
        File localClassifierPath = new File("/Users/drizo/cmg/investigacion/software/github/repositorioHispamus/python-classifiers/symbol-classification");

        // just execute test in drizo's computer :(
        if (localClassifierPath.exists()) {
            DLSymbolAndPositionClassifier classifier = new DLSymbolAndPositionClassifier(localClassifierPath);
            File testImage = new File(localClassifierPath, "test/test1.jpg");
            BoundingBox boundingBox = new BoundingBoxXY(91,245,150,375);
            AgnosticSymbol agnosticSymbol = classifier.recognize(testImage, boundingBox);
            assertEquals("Agnostic symbol", "clef.C:L1", agnosticSymbol.getAgnosticString());


            File testImage2 = new File(localClassifierPath, "test/test2.jpg");
            boundingBox = new BoundingBoxXY(2508, 1671, 2537, 1846);
            agnosticSymbol = classifier.recognize(testImage2, boundingBox);
            assertEquals("Agnostic symbol", "verticalLine:L1", agnosticSymbol.getAgnosticString());


        }
    }
}