package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.clefs.ClefPercussion;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

public class LayoutClef extends CoreSymbol {
    private final LayoutStaff layoutStaff;
    private final Clef clef;
    private final Pictogram pictogram;

    public LayoutClef(Clef clef, LayoutStaff layoutStaff)  {
        this.clef = clef;
        this.layoutStaff = layoutStaff;
        pictogram = new Pictogram(getUnicode());
    }

    /**
     * Compute the y value, the x is given by the layout algorithm
     * @return
     */
    public void computeLayout() throws IM3Exception {
        double y = layoutStaff.getYAtLine(clef.getLine());
        pictogram.setY(y);
    }


    private String getUnicode() {
        switch (clef.getNote()) {
            case F:
                switch (clef.getOctaveChange()) {
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
                        throw new IM3RuntimeException("Unsupported octave change for clef " + clef);
                }
            case G:
                switch (clef.getOctaveChange()) {
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
                        throw new IM3RuntimeException("Unsupported octave change for clef " + clef);
                }
            case C:
                return "cClef";
            default:
                if (clef instanceof ClefPercussion) {
                    return "unpitchedPercussionClef1";
                }

                throw new IM3RuntimeException("Unsupported clef note: " + clef.getNote());
        }

    }

    @Override
    public GraphicsElement getGraphics() {
       return pictogram;
    }
}
