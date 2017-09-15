package es.ua.dlsi.im3.core.score.layout.svg;

import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import org.junit.Test;

import static org.junit.Assert.*;

public class SVGExporterTest {
    @Test
    public void initFont() throws Exception {
        SVGExporter a = new SVGExporter();
        LayoutFont bravura = a.initFont();
        //TODO JSON y comprobar que existe glifo
    }

}