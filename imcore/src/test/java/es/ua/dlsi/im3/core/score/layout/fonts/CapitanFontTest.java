package es.ua.dlsi.im3.core.score.layout.fonts;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class CapitanFontTest {
    @Test
    public void test() throws Exception {
        CapitanFont bf = new CapitanFont();
        assertNotNull(bf.getOtfTextFont());
        assertNotNull(bf.getFontMap());
        assertNotNull(bf.getFont());
        assertNotNull(bf.getSVGFont());
        assertNotNull(bf.getOtfTextFont());
        assertTrue(bf.getTextHeightInPixels() > 0.0);
    }

}