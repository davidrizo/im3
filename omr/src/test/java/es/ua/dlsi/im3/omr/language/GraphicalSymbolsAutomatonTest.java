package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GraphicalSymbolsAutomatonTest {
    @Test
    public void getDeterministicProbabilisticAutomaton() throws Exception {
        GraphicalSymbolsAutomaton gspa = new GraphicalSymbolsAutomaton();
        gspa.getDeterministicProbabilisticAutomaton().writeDot(TestFileUtils.createTempFile("gspa.dot"));

        List<GraphicalSymbol> sequence = Arrays.asList(
                GraphicalSymbol.clef, GraphicalSymbol.accidental, GraphicalSymbol.accidental,
                GraphicalSymbol.text, GraphicalSymbol.text,
                GraphicalSymbol.accidental, GraphicalSymbol.note, GraphicalSymbol.rest,
                GraphicalSymbol.barline
                );

        BigFraction p = gspa.probabilityOf(sequence);
        System.out.println("Probability of " + sequence + "\n\t=" + p);
        assertTrue(p.getNumeratorAsLong() > 0);

        List<GraphicalSymbol> sequence2 = Arrays.asList(
                GraphicalSymbol.accidental,
                GraphicalSymbol.text, GraphicalSymbol.note, GraphicalSymbol.rest,
                GraphicalSymbol.barline
        );

        BigFraction p2 = gspa.probabilityOf(sequence2);
        System.out.println("Probability of " + sequence2 + "\n\t=" + p2);
        assertEquals(0, p2.getNumeratorAsLong());

    }

}