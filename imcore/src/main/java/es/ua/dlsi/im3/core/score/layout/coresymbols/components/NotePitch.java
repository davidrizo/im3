package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.ArrayList;
import java.util.HashMap;

public class NotePitch extends Component<LayoutSingleFigureAtom> {
    private static final HashMap<Figures, String> UNICODES = new HashMap<>();
    {
        UNICODES.put(Figures.DOUBLE_WHOLE, "noteheadDoubleWhole");
        UNICODES.put(Figures.WHOLE, "noteheadWhole");
        UNICODES.put(Figures.HALF, "noteheadHalf");
        UNICODES.put(Figures.QUARTER, "noteheadBlack");
        UNICODES.put(Figures.EIGHTH, "noteheadBlack");
        UNICODES.put(Figures.SIXTEENTH, "noteheadBlack");
        UNICODES.put(Figures.THIRTY_SECOND, "noteheadBlack");
        UNICODES.put(Figures.SIXTY_FOURTH, "noteheadBlack");
        UNICODES.put(Figures.HUNDRED_TWENTY_EIGHTH, "noteheadBlack");
        UNICODES.put(Figures.TWO_HUNDRED_FIFTY_SIX, "noteheadBlack");
        // TODO Existen hasta la 1024th

        //TODO Mensural
        // TODO: 21/9/17 Para Mensural se debe saber si es blanca o ennegrecida
        UNICODES.put(Figures.SEMIBREVE, "mensuralWhiteSemibrevis");
        UNICODES.put(Figures.MINIM, "mensuralWhiteMinima");
        UNICODES.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima");
    }

    /**
     * Used by the layout engines
     */
    public static String NOTE_HEAD_WIDTH_CODEPOINT = "noteheadBlack";

    GraphicsElement root;

    ArrayList<Dot> dots;

    Accidental accidental;

    PositionInStaff positionInStaff;
    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public NotePitch(LayoutFont layoutFont, LayoutSingleFigureAtom parent, AtomPitch pitch, Coordinate position) throws IM3Exception {
        super(parent, position);

        ScientificPitch scientificPitch =  pitch.getScientificPitch();
        int ndots = pitch.getAtomFigure().getDots();
        Accidentals alteration = pitch.getScientificPitch().getPitchClass().getAccidental();
        // TODO: 21/9/17 Cálculo de si debe pintar la alteración, si no hay que poner accidental a null

        positionInStaff = parent.getLayoutStaff().computePositionInStaff(pitch.getTime(), scientificPitch.getPitchClass().getNoteName(), scientificPitch.getOctave());
        CoordinateComponent noteHeadY = parent.getLayoutStaff().computeYPosition(positionInStaff);

        Coordinate noteHeadPosition = new Coordinate(
                new CoordinateComponent(position.getX()),
                noteHeadY
        );
        Pictogram noteHeadPictogram = new Pictogram(layoutFont, getUnicode(), noteHeadPosition);
        //TODO coordinate.getX().setDisplacement(-pictogram.getWidth());


        if (ndots > 0 || alteration != null) {
            Group group = new Group();

            if (alteration != null) {
                Coordinate alterationPosition = new Coordinate(new CoordinateComponent(noteHeadPosition.getX()), noteHeadPosition.getY());
                accidental = new Accidental(parent.getLayoutStaff().getScoreLayout().getLayoutFont(), this, alteration, scientificPitch.getPitchClass().getNoteName(), scientificPitch.getOctave(), alterationPosition);
                alterationPosition.getX().setDisplacement(-accidental.getWidth() - LayoutConstants.ACCIDENTAL_HEAD_SEPARATION);
                group.add(accidental.getGraphics());
            }

            group.add(noteHeadPictogram);

            if (ndots > 0) {
                dots = new ArrayList<>();
                for (int d = 0; d < ndots; d++) {
                    CoordinateComponent dotX = new CoordinateComponent(position.getX(), LayoutConstants.DOT_SEPARATION);
                    // TODO: 21/9/17 Cuando el puntillo caiga en una línea se suba al espacio, ponerlo en yDisplacement
                    double yDisplacement = 0;
                    CoordinateComponent dotY = new CoordinateComponent(noteHeadPosition.getY(), yDisplacement);

                    Coordinate dotPosition = new Coordinate(dotX, dotY);
                    Dot dot = new Dot(parent.getLayoutStaff().getScoreLayout().getLayoutFont(), this, new Coordinate(dotX, dotY));
                    dots.add(dot);
                    group.add(dot.getGraphics());
                }
            }

            root = group;
        } else {
            root = noteHeadPictogram;
        }

    }

    private String getUnicode() throws IM3Exception {
        String unicode = UNICODES.get(parent.getCoreSymbol().getAtomFigure().getFigure());
        if (unicode == null) {
            throw new IM3Exception("Cannot find a font unicode for " + parent.getCoreSymbol().getAtomFigure().getFigure());
        }
        return unicode;
    }

    @Override
    public GraphicsElement getGraphics() {
        return root;
    }

    public PositionInStaff getPositionInStaff() {
        return positionInStaff;
    }
}
