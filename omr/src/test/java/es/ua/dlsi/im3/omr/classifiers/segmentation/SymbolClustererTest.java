package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.StubImage00531SymbolRecognizer;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.SortedSet;

import static org.junit.Assert.*;

public class SymbolClustererTest {

    @Test
    public void cluster() throws IM3Exception {
        // see StubImage00531SymbolRecognizerTest
        StubImage00531SymbolRecognizer symbolRecognizer = new StubImage00531SymbolRecognizer();
        File file = TestFileUtils.getFile("/testdata/stubs/00531.JPG");
        List<Symbol> symbols = symbolRecognizer.recognize(file);
        assertEquals("Symbol count", 352, symbols.size());

        SymbolClusterer symbolClusterer = new SymbolClusterer();
        SortedSet<Region> regions = symbolClusterer.cluster(symbols, 6);
        assertEquals("Number of regions", 6, regions.size());
        int nsymbols=0;
        double prevY = -1;
        for (Region region: regions) {
            int nsr = region.getSymbols().size();
            assertTrue("Number of symbols", nsr > 0);
            nsymbols += nsr;

            assertTrue("Vertically ordered", region.getBoundingBox().getFromY() > prevY);
            prevY = region.getBoundingBox().getFromY();
        }

        assertEquals("Number of clustered symbols", nsymbols, symbols.size());

    }
}