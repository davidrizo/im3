package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.clefs.ClefPercussion;
import es.ua.dlsi.im3.core.score.io.paec.PaecStringExport;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

public class LayoutClef extends LayoutSymbolInStaff<Clef> {
    private final Pictogram pictogram;

    public LayoutClef(LayoutStaff layoutStaff, Clef clef) throws IM3Exception {
        super(layoutStaff, clef);
        this.layoutStaff = layoutStaff;

        position.setY(layoutStaff.getYAtLine(coreSymbol.getLine()));
        pictogram = new Pictogram(layoutStaff.getScoreLayout().getLayoutFont(), getUnicode(), position);

    }


    private String getUnicode() {
        switch (coreSymbol.getNote()) {
            case F:
                switch (coreSymbol.getOctaveChange()) {
                    case -2:
                        return "fClef15mb";
                    case -1:
                        return "fClef8vb";
                    case 0:
                        return "fClef";
                    case 1:
                        return "fClef8va";
                    case 2:
                        return "fClef15ma";
                    default:
                        throw new IM3RuntimeException("Unsupported octave change for clef " + coreSymbol);
                }
            case G:
                switch (coreSymbol.getOctaveChange()) {
                    case -2:
                        return "gClef15mb";
                    case -1:
                        return "gClef8vb";
                    case 0:
                        return "gClef";
                    case 1:
                        return "gClef8va";
                    case 2:
                        return "gClef15ma";
                    default:
                        throw new IM3RuntimeException("Unsupported octave change for clef " + coreSymbol);
                }
            case C:
                return "cClef";
            default:
                if (coreSymbol instanceof ClefPercussion) {
                    return "unpitchedPercussionClef1";
                }

                throw new IM3RuntimeException("Unsupported clef note: " + coreSymbol.getNote());
        }

    }

    @Override
    public GraphicsElement getGraphics() {
       return pictogram;
    }
}
