package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.fonts.CapitanFont;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;

public class FontFactory {
    private static FontFactory instance = null;
    private static BravuraFont bravuraFont;
    private static CapitanFont capitanFont;


    public static final FontFactory getInstance() {
        synchronized (FontFactory.class) {
            if (instance == null) {
                instance = new FontFactory();
            }
        }
        return instance;
    }

    public BravuraFont getBravuraFont() {
        synchronized (FontFactory.class) {
            if (bravuraFont == null) {
                try {
                    bravuraFont = new BravuraFont();
                } catch (Exception e) {
                    throw new IM3RuntimeException(e);
                }
            }
        }
        return bravuraFont;
    }

    public CapitanFont getCapitanFont() {
        synchronized (FontFactory.class) {
            if (capitanFont == null) {
                try {
                    capitanFont = new CapitanFont();
                } catch (Exception e) {
                    throw new IM3RuntimeException(e);
                }
            }
        }
        return capitanFont;
    }

    public LayoutFont getFont(LayoutFonts font) {
        switch (font) {
            case bravura:
                return getBravuraFont();
            case capitan:
                return getCapitanFont();
            default:
                throw new IM3RuntimeException("Invalid font: " + font);
        }

    }

}
