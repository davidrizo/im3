package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GraphicalSymbolsPATest {
    @Test
    public void getDeterministicProbabilisticAutomaton() throws Exception {
        GraphicalSymbolsPA gspa = new GraphicalSymbolsPA();
        gspa.getDeterministicProbabilisticAutomaton().writeDot(TestFileUtils.createTempFile("gspa.dot"));

        List<GraphicalSymbol> sequence = Arrays.asList(
                GraphicalSymbol.clef, GraphicalSymbol.accidental, GraphicalSymbol.accidental,
                GraphicalSymbol.number, GraphicalSymbol.number,
                GraphicalSymbol.accidental, GraphicalSymbol.note, GraphicalSymbol.rest,
                GraphicalSymbol.barline
                );

        Fraction p = gspa.probabilityOf(sequence);
        System.out.println("Probability of " + sequence + "\n\t=" + p);
        assertTrue(p.getNumerator() > 0);

        List<GraphicalSymbol> sequence2 = Arrays.asList(
                GraphicalSymbol.accidental,
                GraphicalSymbol.number, GraphicalSymbol.note, GraphicalSymbol.rest,
                GraphicalSymbol.barline
        );

        Fraction p2 = gspa.probabilityOf(sequence2);
        System.out.println("Probability of " + sequence2 + "\n\t=" + p2);
        assertEquals(0, p2.getNumerator());

    }

}