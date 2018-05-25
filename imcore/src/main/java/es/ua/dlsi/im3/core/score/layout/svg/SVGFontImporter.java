package es.ua.dlsi.im3.core.score.layout.svg;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.XMLSAXImporter;
import es.ua.dlsi.im3.core.score.io.XMLSAXScoreSongImporter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SVGFontImporter extends XMLSAXImporter {
    private final String fontName;
    private SVGFont svgFont;

    public SVGFontImporter(String fontName) {
        this.svgFont = svgFont;
        this.fontName = fontName;
    }

    @Override
    protected void init() throws ParserConfigurationException, SAXException, IM3Exception {
        svgFont = new SVGFont();
    }

    @Override
    protected void handleElementClose(String closingElement) throws ImportException, IM3Exception {
    }

    @Override
    protected void doHandleOpenElement(String element, HashMap<String, String> attributesMap) throws ImportException {
        switch (element) {
            case "font-face":
                svgFont.setUnitsPerEM(Integer.parseInt(getAttribute(attributesMap, "units-per-em")));
                svgFont.setAscent(Integer.parseInt(getAttribute(attributesMap, "ascent")));
                svgFont.setDescent(Integer.parseInt(getAttribute(attributesMap, "descent")));
                break;
            case "glyph":
                // non unicode symbols are not imported
                String unicode = getOptionalAttribute(attributesMap, "unicode");
                if (unicode != null && unicode.trim().length() > 0) {
                    String horiz_adv_x = getOptionalAttribute(attributesMap, "horiz-adv-x");
                    String d = getOptionalAttribute(attributesMap, "d");
                    // if d is empty we don't import it
                    if (d != null) {
                        Glyph glyph = new Glyph(fontName, unicode, d);
                        if (horiz_adv_x != null) {
                            glyph.setDefaultHorizontalAdvance(Integer.parseInt(horiz_adv_x));
                        }
                        svgFont.add(glyph);
                    }
                }
                break;
        }
    }

    private void doInit() throws ParserConfigurationException, SAXException, IM3Exception {
        initSAX();
        init();
    }

    public SVGFont importStream(InputStream is) throws ImportException {
        try {
            doInit();
            saxParser.parse(is, handler);
        } catch (ParserConfigurationException | SAXException | IOException | IM3Exception e) {
            throw new ImportException(e);
        }

        return svgFont;
    }

    public SVGFont importFile(File file) throws ImportException {
        try {
            doInit();
            saxParser.parse(file, handler);
            postProcess();
        } catch (IM3Exception | ParserConfigurationException | SAXException | IOException e) {
            throw new ImportException(e);
        }

        return svgFont;
    }

    @Override
    protected void handleElementContent(String currentElement, String content) throws ImportException {

    }
}
