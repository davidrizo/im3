package es.ua.dlsi.im3.core.score.layout.fonts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BravuraFontTest {
    @Test
    public void test() throws Exception {
        BravuraFont bf = new BravuraFont();
        assertNotNull(bf.getOtfTextFont());
        assertNotNull(bf.getFontMap());
        assertNotNull(bf.getFont());
        assertNotNull(bf.getSVGFont());
        assertNotNull(bf.getOtfTextFont());
        assertTrue(bf.getTextHeightInPixels() > 0.0);
    }

}