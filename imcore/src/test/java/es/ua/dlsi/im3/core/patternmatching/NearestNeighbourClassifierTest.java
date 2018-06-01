package es.ua.dlsi.im3.core.patternmatching;

import es.ua.dlsi.im3.core.IM3Exception;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NearestNeighbourClassifierTest {

    @Test
    public void classify() throws IM3Exception {
        NearestNeighbourClassifier<String, DoublePrototype> classifier = new NearestNeighbourClassifier<String, DoublePrototype>() {
            @Override
            protected void train() throws IM3Exception {
                addPrototype(new DoublePrototype("B", 10.0));
                addPrototype(new DoublePrototype("C", -40.0));
                addPrototype(new DoublePrototype("B", 11.0));
                addPrototype(new DoublePrototype("B", 12.0));
                addPrototype(new DoublePrototype("A", 1.0));
                addPrototype(new DoublePrototype("A", 2.0));
                addPrototype(new DoublePrototype("A", 3.0));
                addPrototype(new DoublePrototype("C", -30.0));
                addPrototype(new DoublePrototype("C", -50.0));
            }
        };

        classifier.train();
        List<String> rank1 = classifier.classify(new DoublePrototype(null, 1.5));
        assertEquals(3, rank1.size());
        assertEquals("A", rank1.get(0));
        assertEquals("B", rank1.get(1));
        assertEquals("C", rank1.get(2));

        List<String> rank2 = classifier.classify(new DoublePrototype(null, -100.0));
        assertEquals(3, rank2.size());
        assertEquals("C", rank2.get(0));
        assertEquals("A", rank2.get(1));
        assertEquals("B", rank2.get(2));

    }
}