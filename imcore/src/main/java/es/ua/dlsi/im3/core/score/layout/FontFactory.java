package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.fonts.CapitanFont;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.fonts.PatriarcaFont;

import java.util.HashMap;

public class FontFactory {
    private static FontFactory instance = null;
    private static BravuraFont bravuraFont;
    private static CapitanFont capitanFont;
    private static PatriarcaFont patriarcaFont;


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
                    e.printStackTrace();
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

    public PatriarcaFont getPatriarcaFont() {
        synchronized (FontFactory.class) {
            if (patriarcaFont == null) {
                try {
                    patriarcaFont = new PatriarcaFont();
                } catch (Exception e) {
                    throw new IM3RuntimeException(e);
                }
            }
        }
        return patriarcaFont;
    }
    public LayoutFont getFont(LayoutFonts font) {
        switch (font) {
            case bravura:
                return getBravuraFont();
            case capitan:
                return getCapitanFont();
            case patriarca:
                return getPatriarcaFont();
            default:
                throw new IM3RuntimeException("Invalid font: " + font);
        }

    }

    public HashMap<Staff, LayoutFonts> getFontNameForScoreSong(ScoreSong scoreSong) throws IM3Exception {
        HashMap<Staff, LayoutFonts> fontHashMap = new HashMap<>();
        for (Staff staff: scoreSong.getStaves()) {
            LayoutFonts layoutFonts;
            if (staff.getNotationType() == null) {
                throw new IM3Exception("Notation type for staff '" + staff.getName() + "' not set");
            }
            switch (staff.getNotationType()) {
                case eMensural:
                    layoutFonts = LayoutFonts.patriarca;
                    break;
                case eModern:
                    layoutFonts = LayoutFonts.bravura;
                    break;
                default:
                    throw new IM3Exception("Unsupported notation type: " + staff.getNotationType());
            }
            fontHashMap.put(staff, layoutFonts);
        }
        return fontHashMap;
    }

    public HashMap<Staff, LayoutFont> getFontsForScoreSong(ScoreSong scoreSong) throws IM3Exception {
        HashMap<Staff, LayoutFont> fontHashMap = new HashMap<>();
        for (Staff staff: scoreSong.getStaves()) {
            LayoutFonts layoutFonts;
            if (staff.getNotationType() == null) {
                throw new IM3Exception("Notation type for staff '" + staff.getName() + "' not set");
            }
            switch (staff.getNotationType()) {
                case eMensural:
                    layoutFonts = LayoutFonts.patriarca;
                    break;
                case eModern:
                    layoutFonts = LayoutFonts.bravura;
                    break;
                default:
                    throw new IM3Exception("Unsupported notation type: " + staff.getNotationType());
            }
            fontHashMap.put(staff, getFont(layoutFonts));
        }
        return fontHashMap;
    }

}
