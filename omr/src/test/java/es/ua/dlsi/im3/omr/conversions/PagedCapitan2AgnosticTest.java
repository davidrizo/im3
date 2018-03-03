package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticEncoding;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import org.junit.Test;

import static org.junit.Assert.*;

public class PagedCapitan2AgnosticTest {

    private void test(String input, AgnosticSymbol [] expected) throws ImportException {
        PagedCapitan2Agnostic capitan2Agnostic = new PagedCapitan2Agnostic();
        AgnosticEncoding agnosticEncoding = capitan2Agnostic.parseLine(input);
        assertEquals("Size", expected.length, agnosticEncoding.size());

        for (int i=0; i<expected.length; i++) {
            assertEquals("Token #" + i, expected[i].toString(), agnosticEncoding.getSymbols().get(i).toString());
        }
    }

    private void test(String input, String expected) throws ImportException {
        PagedCapitan2Agnostic capitan2Agnostic = new PagedCapitan2Agnostic();
        AgnosticEncoding agnosticEncoding = capitan2Agnostic.parseLine(input);
        AgnosticExporter exporter = new AgnosticExporter();
        assertEquals(expected, exporter.export(agnosticEncoding));
    }
    @Test
    public void parseLine() throws ImportException {
        String input1 = "GCLEF.5 COLOUREDMINIMA.-10 COLOUREDMINIMA.0 MINIMA.01 MINIMA.01 MINIMAREST.1 BREVISREST.01";
        AgnosticSymbol [] expected1 = new AgnosticSymbol[] {
                new AgnosticSymbol(new Clef(ClefNote.G), PositionsInStaff.LINE_2),
                new AgnosticSymbol(new Note(NoteFigures.quarter), PositionsInStaff.SPACE_5),
                new AgnosticSymbol(new Note(NoteFigures.quarter), PositionsInStaff.LINE_5),
                new AgnosticSymbol(new Note(NoteFigures.half), PositionsInStaff.SPACE_4),
                new AgnosticSymbol(new Note(NoteFigures.half), PositionsInStaff.SPACE_4),
                new AgnosticSymbol(new Rest(RestFigures.half), PositionsInStaff.LINE_4),
                new AgnosticSymbol(new Rest(RestFigures.breve), PositionsInStaff.SPACE_4)
        };
        test(input1, expected1);


        // first line of 12625-B.txt
        String input2 = "FCLEF.2 CUTTIME.2 LONGAREST.13 LONGAREST.13 SEMIBREVIS.1 DOT.01 FLAT.-10 MINIMA.01 MINIMA.1 DOT.01 COLOUREDMINIMA.12 MINIMA.2 MINIMA.3 SEMIBREVIS.1 SEMIBREVISREST.2 MINIMA.23 DOT.23 COLOUREDMINIMA.3 COLOUREDMINIMA.34 COLOUREDMINIMA.45 SEMIBREVIS.3 MINIMA.34/SHARP.45 MINIMA.34/SHARP.45 SEMIBREVIS.3 MINIMA.1 SEMIBREVIS.3 SEMIBREVIS.1 CUSTOS.3";
        String expected2 = "clef.F-L3\tmetersign.Ccut-L3\trest.longa-L2\trest.longa-L2\tnote.whole-L4\tdot-S4\taccidental.flat-S5\tnote.half-S4\tnote.half-L4\tdot-S4\tnote.quarter-S3\tnote.half-L3\tnote.half-L2\tnote.whole-L4\trest.whole-L3\tnote.half-S2\tdot-S2\tnote.quarter-L2\tnote.quarter-S1\tnote.quarter-S0\tnote.whole-L2\tnote.half-S1\taccidental.sharp-S0\tnote.half-S1\taccidental.sharp-S0\tnote.whole-L2\tnote.half-L4\tnote.whole-L2\tnote.whole-L4\tcustos-L2";
        test(input2, expected2);

        // Beginning of 5th staff in 12644-B.txt
        String input3 = "GCLEF.5 BCOLOUREDSEMIMINIMA.0 BCOLOUREDSEMIMINIMA.01 BCOLOUREDSEMIMINIMA.1 BCOLOUREDSEMIMINIMA.12 MINIMA.1";
        String expected3 = "clef.G-L2\tnote.beamedLeft1-L5\tnote.beamedBoth1-S4\tnote.beamedBoth1-L4\tnote.beamedRight1-S3\tnote.half-L4";
        test(input3, expected3);

        // Other variations
        String input4 = "BCOLOUREDFUSA.0 BCOLOUREDFUSA.01";
        String expected4 = "note.beamedLeft2-L5\tnote.beamedRight2-S4";
        test(input3, expected3);

    }
}