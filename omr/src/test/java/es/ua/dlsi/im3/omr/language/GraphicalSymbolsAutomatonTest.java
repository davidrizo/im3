package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;
import es.ua.dlsi.im3.omr.language.mensural.GraphicalMensuralSymbolsAutomaton;
import es.ua.dlsi.im3.omr.language.modern.GraphicalModernSymbolsAutomaton;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GraphicalSymbolsAutomatonTest {
    AgnosticVersion agnosticVersion = AgnosticVersion.v1;

    @Test
    public void modern() throws Exception {
        GraphicalModernSymbolsAutomaton gspa = new GraphicalModernSymbolsAutomaton();
        gspa.getDeterministicProbabilisticAutomaton().writeDot(TestFileUtils.createTempFile("modern.dot"));

        List<AgnosticToken> sequence1 = Arrays.asList(
                new AgnosticSymbol(agnosticVersion, new Clef(ClefNote.G), PositionsInStaff.LINE_2),
                new AgnosticSymbol(agnosticVersion, new Accidental(Accidentals.flat), PositionsInStaff.LINE_3),
                new AgnosticSymbol(agnosticVersion, new Accidental(Accidentals.flat), PositionsInStaff.SPACE_4),
                new AgnosticSymbol(agnosticVersion, new Digit(3), PositionsInStaff.LINE_4),
                new AgnosticSymbol(agnosticVersion, new Digit(4), PositionsInStaff.LINE_2),
                new AgnosticSymbol(agnosticVersion, new Accidental(Accidentals.flat), PositionsInStaff.SPACE_2),
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.half), PositionsInStaff.SPACE_2),
                new AgnosticSymbol(agnosticVersion, new Rest(RestFigures.quarter), PositionsInStaff.LINE_3),
                new AgnosticSymbol(agnosticVersion, new VerticalLine(), PositionsInStaff.LINE_1)
                );

        OMRTransduction t1 = gspa.probabilityOf(sequence1, true);
        assertEquals("Key signature", -2, t1.getSong().getUniqueKeyWithOnset(Time.TIME_ZERO).getFifths());

        System.out.println("Probability of " + sequence1 + "\n\t=" + t1.getProbability());

        assertTrue(t1.getProbability().getNumeratorAsLong() > 0);

        List<AgnosticToken> sequence2 = Arrays.asList(
                new AgnosticSymbol(agnosticVersion, new Accidental(Accidentals.flat), PositionsInStaff.SPACE_4),
                new AgnosticSymbol(agnosticVersion, new Digit(3), PositionsInStaff.LINE_4)
        );

        OMRTransduction t2 = gspa.probabilityOf(sequence2, true);
        System.out.println("Probability of " + sequence2 + "\n\t=" + t2.getProbability());
        assertEquals(0, t2.getProbability().getNumeratorAsLong());

        // draw first transduction
        HorizontalLayout layout = new HorizontalLayout(t1.getSong(), LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout(true);

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("transduction_modern.svg");
        svgExporter.exportLayout(svgFile, layout);
    }
    @Test
    public void mensuralBinary() throws Exception {
        GraphicalMensuralSymbolsAutomaton gspa = new GraphicalMensuralSymbolsAutomaton();
        gspa.getDeterministicProbabilisticAutomaton().writeDot(TestFileUtils.createTempFile("mensural.dot"));

        List<AgnosticToken> sequence1 = Arrays.asList(
                new AgnosticSymbol(agnosticVersion, new Clef(ClefNote.G), PositionsInStaff.LINE_2),
                new AgnosticSymbol(agnosticVersion, new Accidental(Accidentals.flat), PositionsInStaff.LINE_3),
                new AgnosticSymbol(agnosticVersion, new MeterSign(MeterSigns.C), PositionsInStaff.LINE_3),
                new AgnosticSymbol(agnosticVersion, new Rest(RestFigures.half), PositionsInStaff.LINE_4), // minim
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.half), PositionsInStaff.LINE_4), // minim
                new AgnosticSymbol(agnosticVersion, new VerticalLine(), PositionsInStaff.LINE_1),
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.quarter), PositionsInStaff.SPACE_4), // seminim
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.quarter), PositionsInStaff.LINE_5), // seminim
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.whole), PositionsInStaff.SPACE_5), // SEMIBREVE
                new AgnosticSymbol(agnosticVersion, new Accidental(Accidentals.sharp), PositionsInStaff.LINE_5),
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.half), PositionsInStaff.LINE_5), // minim
                new AgnosticSymbol(agnosticVersion, new VerticalLine(), PositionsInStaff.LINE_1),
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.half), PositionsInStaff.SPACE_5), // minim
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.half), PositionsInStaff.SPACE_5), // minim
                new AgnosticSymbol(agnosticVersion, new VerticalLine(), PositionsInStaff.LINE_1),
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.whole), PositionsInStaff.FIRST_TOP_LEDGER_LINE), // SEMIBREVE - // TODO: 5/10/17 ¿De entrada tenemos el ledger line?
                new AgnosticSymbol(agnosticVersion, new VerticalLine(), PositionsInStaff.LINE_1)
        );

        OMRTransduction t1 = gspa.probabilityOf(sequence1, true);
        System.out.println("Probability of " + sequence1 + "\n\t=" + t1.getProbability());
        assertTrue(t1.getProbability().getNumeratorAsLong() > 0);

        List<AgnosticToken> sequence2 = Arrays.asList(
                new AgnosticSymbol(agnosticVersion, new Accidental(Accidentals.flat), PositionsInStaff.SPACE_4),
                new AgnosticSymbol(agnosticVersion, new Digit(3), PositionsInStaff.LINE_4)
        );

        OMRTransduction t2 = gspa.probabilityOf(sequence2, true);
        System.out.println("Probability of " + sequence2 + "\n\t=" + t2.getProbability());
        assertEquals(0, t2.getProbability().getNumeratorAsLong());

        // draw first transduction
        HorizontalLayout layout = new HorizontalLayout(t1.getSong(), LayoutFonts.patriarca,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout(true);

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("transduction_mensural.svg");
        svgExporter.exportLayout(svgFile, layout);
    }

}