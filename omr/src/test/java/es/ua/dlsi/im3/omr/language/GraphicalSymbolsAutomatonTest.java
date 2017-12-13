package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.omr.language.mensural.GraphicalMensuralSymbolsAutomaton;
import es.ua.dlsi.im3.omr.language.modern.GraphicalModernSymbolsAutomaton;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
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
                new GraphicalToken(GraphicalSymbol.clef, "g", PositionsInStaff.LINE_2),
                new GraphicalToken(GraphicalSymbol.accidental, "flat", PositionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.accidental, "flat", PositionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.digit, "3", PositionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.digit, "4", PositionsInStaff.LINE_2),
                new GraphicalToken(GraphicalSymbol.accidental, "flat", PositionsInStaff.SPACE_2),
                new GraphicalToken(GraphicalSymbol.note, "HALF", PositionsInStaff.SPACE_2),
                new GraphicalToken(GraphicalSymbol.rest, "QUARTER", PositionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1)
                );

        OMRTransduction t1 = gspa.probabilityOf(sequence1, true);
        System.out.println("Probability of " + sequence1 + "\n\t=" + t1.getProbability());
        assertTrue(t1.getProbability().getNumeratorAsLong() > 0);

        List<GraphicalToken> sequence2 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.accidental, "b", PositionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.digit, "3", PositionsInStaff.LINE_4)
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
                new GraphicalToken(GraphicalSymbol.clef, "g", PositionsInStaff.LINE_2),
                new GraphicalToken(GraphicalSymbol.accidental, "b", PositionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.metersign, "C", PositionsInStaff.LINE_3),
                new GraphicalToken(GraphicalSymbol.rest, "MINIM", PositionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PositionsInStaff.LINE_4),
                new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1),
                new GraphicalToken(GraphicalSymbol.note, "SEMIMINIM", PositionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.note, "SEMIMINIM", PositionsInStaff.LINE_5),
                new GraphicalToken(GraphicalSymbol.note, "SEMIBREVE", PositionsInStaff.SPACE_5),
                new GraphicalToken(GraphicalSymbol.accidental, "#", PositionsInStaff.LINE_5),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PositionsInStaff.LINE_5),
                new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PositionsInStaff.SPACE_5),
                new GraphicalToken(GraphicalSymbol.note, "MINIM", PositionsInStaff.SPACE_5),
                new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1),
                new GraphicalToken(GraphicalSymbol.note, "SEMIBREVE", PositionsInStaff.FIRST_TOP_LEDGER_LINE), // TODO: 5/10/17 ¿De entrada tenemos el ledger line?
                new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1)
        );

        OMRTransduction t1 = gspa.probabilityOf(sequence1, true);
        System.out.println("Probability of " + sequence1 + "\n\t=" + t1.getProbability());
        assertTrue(t1.getProbability().getNumeratorAsLong() > 0);

        List<GraphicalToken> sequence2 = Arrays.asList(
                new GraphicalToken(GraphicalSymbol.accidental, "b", PositionsInStaff.SPACE_4),
                new GraphicalToken(GraphicalSymbol.digit, "3", PositionsInStaff.LINE_4)
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