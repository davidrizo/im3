package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class StubImage00531SymbolRecognizerTest {

    @Test
    public void recognize() throws IM3Exception {
        StubImage00531SymbolRecognizer symbolRecognizer = new StubImage00531SymbolRecognizer();
        File file = TestFileUtils.getFile("/testdata/stubs/00531.JPG");
        List<Symbol> symbols = symbolRecognizer.recognize(file);
        assertEquals("Symbol count", 352, symbols.size());

        Symbol symbol0 = symbols.get(0);
        // it is the new version in agnostic encoding of minima
        assertEquals("Symbol agnostic string", "note.half_down:L3", symbol0.getAgnosticSymbol().getAgnosticString());
        assertEquals("From X", 1764.4030314683914, symbol0.getBoundingBox().getFromX(), 0.0001);
        assertEquals("From Y", 784.610049188137, symbol0.getBoundingBox().getFromY(), 0.0001);
        assertEquals("To X", 1818.3234423398972, symbol0.getBoundingBox().getToX(), 0.0001);
        assertEquals("To Y", 896.5861566066742, symbol0.getBoundingBox().getToY(), 0.0001);
    }
}