package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.PossitionsInStaff;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.omr.language.mensural.GraphicalMensuralSymbolsAutomaton;
import es.ua.dlsi.im3.omr.language.modern.GraphicalModernSymbolsAutomaton;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GraphicalSymbolsAutomatonTest {
    @Test
    public void modern() throws Exception {
        GraphicalModernSymbolsAutomaton gspa = new GraphicalModernSymbolsAutomaton();
        gspa.getDeterministicProbabilisticAutomaton().writeDot(TestFileUtils.createTempFile("modern.dot"));

        List<GraphicalToken> sequence1 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.clef, "g", PossitionsInStaff.LINE_2),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.text, "3", PossitionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.text, "4", PossitionsInStaff.LINE_2),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_2),
                new GraphicalToken(GraphicalSymbol.note, "HALF", PossitionsInStaff.SPACE_2),
                new GraphicalToken(GraphicalSymbol.rest, "QUARTER", PossitionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1)
                );

        OMRTransduction t1 = gspa.probabilityOf(sequence1, true);
        System.out.println("Probability of " + sequence1 + "\n\t=" + t1.getProbability());
        assertTrue(t1.getProbability().getNumeratorAsLong() > 0);

        List<GraphicalToken> sequence2 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.text, "3", PossitionsInStaff.LINE_4)
        );

        OMRTransduction t2 = gspa.probabilityOf(sequence2, true);
        System.out.println("Probability of " + sequence2 + "\n\t=" + t2.getProbability());
        assertEquals(0, t2.getProbability().getNumeratorAsLong());

        // draw first transduction
        HorizontalLayout layout = new HorizontalLayout(t1.getSong(), LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("transduction_modern.svg");
        svgExporter.exportLayout(svgFile, layout);
    }
    @Test
    public void mensuralBinary() throws Exception {
        GraphicalMensuralSymbolsAutomaton gspa = new GraphicalMensuralSymbolsAutomaton();
        gspa.getDeterministicProbabilisticAutomaton().writeDot(TestFileUtils.createTempFile("mensural.dot"));

        List<GraphicalToken> sequence1 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.clef, "g", PossitionsInStaff.LINE_2),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.text, "C", PossitionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.rest, "MINIM", PossitionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PossitionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1),
                new GraphicalToken(GraphicalSymbol.note, "SEMIMINIM", PossitionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.note, "SEMIMINIM", PossitionsInStaff.LINE_5),
                new GraphicalToken(GraphicalSymbol.note, "SEMIBREVE", PossitionsInStaff.SPACE_5),
                new GraphicalToken(GraphicalSymbol.accidental, "#", PossitionsInStaff.LINE_5),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PossitionsInStaff.LINE_5),
                new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PossitionsInStaff.SPACE_5),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PossitionsInStaff.SPACE_5),
                new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1),
                new GraphicalToken(GraphicalSymbol.note, "SEMIBREVE", PossitionsInStaff.FIRST_TOP_LEDGER_LINE), // TODO: 5/10/17 ¿De entrada tenemos el ledger line?
                new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1)
        );

        OMRTransduction t1 = gspa.probabilityOf(sequence1, true);
        System.out.println("Probability of " + sequence1 + "\n\t=" + t1.getProbability());
        assertTrue(t1.getProbability().getNumeratorAsLong() > 0);

        List<GraphicalToken> sequence2 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.accidental, "b", PossitionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.text, "3", PossitionsInStaff.LINE_4)
        );

        OMRTransduction t2 = gspa.probabilityOf(sequence2, true);
        System.out.println("Probability of " + sequence2 + "\n\t=" + t2.getProbability());
        assertEquals(0, t2.getProbability().getNumeratorAsLong());

        // draw first transduction
        HorizontalLayout layout = new HorizontalLayout(t1.getSong(), LayoutFonts.capitan,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("transduction_mensural.svg");
        svgExporter.exportLayout(svgFile, layout);
    }

}