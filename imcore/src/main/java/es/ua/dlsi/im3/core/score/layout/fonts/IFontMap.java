package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public interface IFontMap {
    String getUnicode(Figures figures, boolean stemUp) throws IM3Exception;

    /**
     * There are fonts that include the flag (e.g. Capitán)
     * @param figures
     * @return
     * @throws IM3Exception
     */

    String getUnicodeWihoutFlag(Figures figures) throws IM3Exception;
    String getUnicodeNoteHeadWidth();
    boolean isGlyphIncludeStemAndFlag(Figures figures);
    GraphicsElement createBeam(String ID, Coordinate from, Coordinate to);
    String getCustosCodePoint();
}
