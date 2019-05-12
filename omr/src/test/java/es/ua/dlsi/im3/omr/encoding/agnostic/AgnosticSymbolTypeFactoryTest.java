package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.IM3Exception;
import org.junit.Test;

import static org.junit.Assert.*;

public class AgnosticSymbolTypeFactoryTest {

    @Test
    public void parseString() throws IM3Exception {
        String [] strings = {"clef.G", "metersign.Ct", "metersign.Cdot", "metersign.Odot", "colon", "note.breve", "note.beamedBoth1_down", "defect.paperHole"}; // TODO Comprobar con todos

        for (String string: strings) {
            AgnosticSymbolType agnosticSymbolType = AgnosticSymbolTypeFactory.parseString(string);
            assertEquals(string, agnosticSymbolType.toAgnosticString(), string);
        }
    }
}
