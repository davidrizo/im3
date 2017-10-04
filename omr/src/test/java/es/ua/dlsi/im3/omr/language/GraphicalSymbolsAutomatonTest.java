package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.adt.dfa.Transduction;
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

        List<GraphicalToken> sequence1 = Arrays.asList(
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

        Transduction t1 = gspa.probabilityOf(sequence1);
        System.out.println("Probability of " + sequence1 + "\n\t=" + t1.getProbability());
        assertTrue(t1.getProbability().getNumeratorAsLong() > 0);

        List<GraphicalToken> sequence2 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.text, "3", PossitionsInStaff.LINE_4)
        );

        Transduction t2 = gspa.probabilityOf(sequence2);
        System.out.println("Probability of " + sequence2 + "\n\t=" + t2.getProbability());
        assertEquals(0, t2.getProbability().getNumeratorAsLong());

    }

}