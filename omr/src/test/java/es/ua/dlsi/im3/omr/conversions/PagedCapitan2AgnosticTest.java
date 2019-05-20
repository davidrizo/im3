package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.*;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import org.junit.Test;

import static org.junit.Assert.*;

public class PagedCapitan2AgnosticTest {
    AgnosticVersion agnosticVersion = AgnosticVersion.v1;

    private void test(String input, AgnosticToken [] expected) throws ImportException {
        PagedCapitan2Agnostic capitan2Agnostic = new PagedCapitan2Agnostic();
        AgnosticEncoding agnosticEncoding = capitan2Agnostic.parseLine(input);
        assertEquals("Size", expected.length, agnosticEncoding.size());

        for (int i=0; i<expected.length; i++) {
            assertEquals("Token #" + i, expected[i].getAgnosticString(), agnosticEncoding.getSymbols().get(i).getAgnosticString());
        }
    }

    private void test(String input, String expected) throws IM3Exception {
        PagedCapitan2Agnostic capitan2Agnostic = new PagedCapitan2Agnostic();
        AgnosticEncoding agnosticEncoding = capitan2Agnostic.parseLine(input);
        AgnosticExporter exporter = new AgnosticExporter();
        assertEquals(expected, exporter.export(agnosticEncoding));
    }
    @Test
    public void parseLine() throws IM3Exception {
        HorizontalSeparator horizontalSeparator = new HorizontalSeparator(AgnosticVersion.v2);

        String input1 = "GCLEF.5 COLOUREDMINIMA.-10 COLOUREDMINIMA.0 MINIMA.01 MINIMA.01 MINIMAREST.1 BREVISREST.01";
        AgnosticToken[] expected1 = new AgnosticToken[] {
                new AgnosticSymbol(agnosticVersion, new Clef(ClefNote.G), PositionsInStaff.LINE_2),
                horizontalSeparator,
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.quarter), PositionsInStaff.SPACE_5),
                horizontalSeparator,
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.quarter), PositionsInStaff.LINE_5),
                horizontalSeparator,
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.half), PositionsInStaff.SPACE_4),
                horizontalSeparator,
                new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.half), PositionsInStaff.SPACE_4),
                horizontalSeparator,
                new AgnosticSymbol(agnosticVersion, new Rest(RestFigures.half), PositionsInStaff.LINE_4),
                horizontalSeparator,
                new AgnosticSymbol(agnosticVersion, new Rest(RestFigures.breve), PositionsInStaff.LINE_3)
        };
        test(input1, expected1);


        String input2 = "FCLEF.2 CUTTIME.2 LONGAREST.13 LONGAREST.13 SEMIBREVIS.1 DOT.01 FLAT.-10 MINIMA.01 MINIMA.1 DOT.01 COLOUREDMINIMA.12 MINIMA.2 MINIMA.3 SEMIBREVIS.1 SEMIBREVISREST.2 MINIMA.23 DOT.23 COLOUREDMINIMA.3 COLOUREDMINIMA.34 COLOUREDMINIMA.45 SEMIBREVIS.3 MINIMA.34/SHARP.45 MINIMA.34/SHARP.45 SEMIBREVIS.3 MINIMA.1 SEMIBREVIS.3 SEMIBREVIS.1 CUSTOS.3";
        String expected2 = "clef.F-L3, metersign.Ccut-L3, rest.longa2-L2, rest.longa2-L2, note.whole-L4, dot-S4, accidental.flat-S5, note.half-S4, note.half-L4, dot-S4, note.quarter-S3, note.half-L3, note.half-L2, note.whole-L4, rest.whole-L3, note.half-S2, dot-S2, note.quarter-L2, note.quarter-S1, note.quarter-S0, note.whole-L2, note.half-S1/accidental.sharp-S0, note.half-S1/accidental.sharp-S0, note.whole-L2, note.half-L4, note.whole-L2, note.whole-L4, custos-L2";
        test(input2, expected2);

        // Beginning of 5th staff in 12644-B.txt
        String input3 = "GCLEF.5 BCOLOUREDSEMIMINIMA.0 BCOLOUREDSEMIMINIMA.01 BCOLOUREDSEMIMINIMA.1 BCOLOUREDSEMIMINIMA.12 MINIMA.1";
        String expected3 = "clef.G-L2, note.beamedLeft1-L5, note.beamedBoth1-S4, note.beamedBoth1-L4, note.beamedRight1-S3, note.half-L4";
        test(input3, expected3);

        // Other variations
        String input4 = "BCOLOUREDFUSA.0 BCOLOUREDFUSA.01";
        String expected4 = "note.beamedLeft2-L5, note.beamedRight2-S4";
        test(input3, expected3);

    }
}
