package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.PossitionsInStaff;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GraphicalSymbolsAutomatonTest {
    @Test
    public void getDeterministicProbabilisticAutomaton() throws Exception {
        GraphicalSymbolsAutomaton gspa = new GraphicalSymbolsAutomaton();
        gspa.getDeterministicProbabilisticAutomaton().writeDot(TestFileUtils.createTempFile("gspa.dot"));

        List<GraphicalToken> sequence = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.clef, "g", PossitionsInStaff.LINE_2),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.text, "3", PossitionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.text, "4", PossitionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_2),
                new GraphicalToken(GraphicalSymbol.note, "HALF", PossitionsInStaff.SPACE_2),
                new GraphicalToken(GraphicalSymbol.rest, "QUARTER", PossitionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1)
                );

        BigFraction p = gspa.probabilityOf(sequence);
        System.out.println("Probability of " + sequence + "\n\t=" + p);
        assertTrue(p.getNumeratorAsLong() > 0);

        List<GraphicalToken> sequence2 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.text, "3", PossitionsInStaff.LINE_4)
        );

        BigFraction p2 = gspa.probabilityOf(sequence2);
        System.out.println("Probability of " + sequence2 + "\n\t=" + p2);
        assertEquals(0, p2.getNumeratorAsLong());

    }

}