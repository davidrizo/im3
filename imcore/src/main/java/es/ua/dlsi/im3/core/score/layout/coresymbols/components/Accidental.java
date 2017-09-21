package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutKeySignature;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.HashMap;

public class Accidental extends Component<LayoutKeySignature> {
    int order;
    DiatonicPitch noteName;
    int octave;
    Pictogram pictogram;
    Accidentals accidental;
    Coordinate position;

    static HashMap<Accidentals, String> unicodes = new HashMap<>();
    {
        unicodes.put(Accidentals.FLAT, "accidentalFlat");
        unicodes.put(Accidentals.NATURAL, "accidentalNatural");
        unicodes.put(Accidentals.SHARP, "accidentalSharp");
        unicodes.put(Accidentals.DOUBLE_SHARP, "accidentalDoubleSharp");
        unicodes.put(Accidentals.DOUBLE_FLAT, "accidentalDoubleFlat");
        unicodes.put(Accidentals.TRIPLE_FLAT, "accidentalTripleFlat");

    }

    public Accidental(LayoutFont layoutFont, LayoutKeySignature parent, Accidentals accidental, int order, DiatonicPitch noteName, int octave, Coordinate position) throws IM3Exception {
        super(parent, position);
        this.accidental = accidental;
        this.order = order;
        this.noteName = noteName;
        this.octave = octave;

        pictogram = new Pictogram(layoutFont, getUnicode(), position);
    }

    private String getUnicode() {
        String unicode = unicodes.get(accidental);
        if (unicode == null) {
            throw new IM3RuntimeException("Accidental " + accidental + " not found");
        }
        return unicode;
    }


    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }
}
