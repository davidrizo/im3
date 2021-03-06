package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.HashMap;

public class Accidental<ParentType extends NotationSymbol> extends Component<ParentType> {
    Pictogram pictogram;
    Accidentals accidental;
    Coordinate position;

    static HashMap<Accidentals, String> unicodes = new HashMap<>();
    {
        unicodes.put(Accidentals.FLAT, "accidentalFlat"); //TODO Generalizarlo a una fuente SmuflMap o algo así
        unicodes.put(Accidentals.NATURAL, "accidentalNatural");
        unicodes.put(Accidentals.SHARP, "accidentalSharp");
        unicodes.put(Accidentals.DOUBLE_SHARP, "accidentalDoubleSharp");
        unicodes.put(Accidentals.DOUBLE_FLAT, "accidentalDoubleFlat");
        unicodes.put(Accidentals.TRIPLE_FLAT, "accidentalTripleFlat");

    }

    public Accidental(LayoutFont layoutFont, ParentType parent, Accidentals accidental, Coordinate position) throws IM3Exception {
        super(accidental, parent, position);
        this.accidental = accidental;

        pictogram = new Pictogram(this, InteractionElementType.accidental, layoutFont, getUnicode(), position);
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

    public Accidentals getAccidental() {
        return accidental;
    }

    @Override
    protected void doLayout() throws IM3Exception {
        // TODO: 26/3/18 ¿Si ha cambiado el pictograma?
    }

    public void setReferenceY(CoordinateComponent y) {
        this.getPosition().setReferenceY(y);
    }
}
