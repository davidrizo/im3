package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;

public class FontFactory {
    private static FontFactory instance = null;
    BravuraFont bravuraFont;

    private FontFactory() {
        try {
            bravuraFont = new BravuraFont();
        } catch (IM3Exception e) {
            throw new IM3RuntimeException(e);
        }
    }

    public static final synchronized FontFactory getInstance() {
        if (instance == null) {
            instance = new FontFactory();
        }
        return instance;
    }

    public LayoutFont getBravuraFont() {
        return bravuraFont;
    }
}
