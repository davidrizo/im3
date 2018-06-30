package es.ua.dlsi.im3.core.patternmatching;

import es.ua.dlsi.im3.core.IM3Exception;
import org.junit.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class NearestNeighbourClassifierTest {

    @Test
    public void classify() throws IM3Exception {
        NearestNeighbourClassifier<String, DoublePrototype> classifier = new NearestNeighbourClassifier<String, DoublePrototype>() {
            @Override
            protected void train() {
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
        TreeSet<RankingItem<DoublePrototype>> rank1 = classifier.classify(new DoublePrototype(null, 1.5));
        assertEquals(9, rank1.size());
        assertEquals("A", new RankingItem<DoublePrototype>(new DoublePrototype("A", 1.0), 0.5), rank1.first());
    }
}