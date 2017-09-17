package es.ua.dlsi.im3.core.score.layout.svg;

import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import org.junit.Test;

import static org.junit.Assert.*;

public class SVGExporterTest {
    // just load it
    @Test
    public void initFont() throws Exception {
        SVGExporter a = new SVGExporter(LayoutFonts.bravura);
        LayoutFont bravura = a.initFont();
    }

}