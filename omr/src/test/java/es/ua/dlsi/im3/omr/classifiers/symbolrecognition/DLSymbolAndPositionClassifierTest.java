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
        File localClassifierPath = new File("/Users/drizo/cmg/investigacion/software/github/repositorioHispamus/symbol-classification");

        // just execute test in drizo's computer :(
        if (localClassifierPath.exists()) {
            DLSymbolAndPositionClassifier classifier = new DLSymbolAndPositionClassifier(localClassifierPath);
            File testImage = new File(localClassifierPath, "test/test1.jpg");
            BoundingBox boundingBox = new BoundingBoxXY(91,245,150,375);
            AgnosticSymbol agnosticSymbol = classifier.recognize(testImage, boundingBox);
            assertEquals("Agnostic symbol", "clef.C:L1", agnosticSymbol.getAgnosticString());
        }
    }
}