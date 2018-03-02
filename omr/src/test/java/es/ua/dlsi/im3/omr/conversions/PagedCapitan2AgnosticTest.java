package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticEncoding;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import org.junit.Test;

import static org.junit.Assert.*;

public class PagedCapitan2AgnosticTest {

    @Test
    public void parseLine() throws ImportException {
        String input = "GCLEF.5 COLOUREDMINIMA.-10 COLOUREDMINIMA.0 MINIMA.01 MINIMA.01 MINIMAREST.1 BREVISREST.01";
        AgnosticSymbol [] expected = new AgnosticSymbol[] {
                new AgnosticSymbol(new Clef(ClefNote.G), PositionsInStaff.FIRST_BOTTOM_LEDGER_LINE),
                new AgnosticSymbol(new Note(NoteFigures.quarter), PositionsInStaff.SPACE_5),
                new AgnosticSymbol(new Note(NoteFigures.quarter), PositionsInStaff.LINE_5),
                new AgnosticSymbol(new Note(NoteFigures.half), PositionsInStaff.SPACE_4),
                new AgnosticSymbol(new Note(NoteFigures.half), PositionsInStaff.SPACE_4),
                new AgnosticSymbol(new Rest(RestFigures.half), PositionsInStaff.LINE_4),
                new AgnosticSymbol(new Rest(RestFigures.breve), PositionsInStaff.SPACE_4)
        };

        PagedCapitan2Agnostic capitan2Agnostic = new PagedCapitan2Agnostic();
        AgnosticEncoding agnosticEncoding = capitan2Agnostic.parseLine(input);
        assertEquals("Size", expected.length, agnosticEncoding.size());

        for (int i=0; i<expected.length; i++) {
            assertEquals("Token #" + i, expected[i].toString(), agnosticEncoding.getSymbols().get(i).toString());
        }

    }
}