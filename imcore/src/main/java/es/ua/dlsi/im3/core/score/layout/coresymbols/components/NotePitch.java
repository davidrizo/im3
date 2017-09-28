package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.ArrayList;
import java.util.HashMap;

public class NotePitch extends Component<LayoutCoreSingleFigureAtom> {
    private static final HashMap<Figures, String> UNICODES = new HashMap<>();
    private final Pictogram noteHeadPictogram;
    private final AtomPitch atomPitch;
    private final LayoutFont layoutFont;

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
        //UNICODES.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES.put(Figures.SEMIMINIM, "mensuralBlackMinima"); //TODO Ver esto - ¿igual en proporción ternaria?
        // TODO: 26/9/17  IM3 - debemos tener distintas versiones de glifos - cojo las duraciones del sXVII - https://en.wikipedia.org/wiki/Mensural_notation
        UNICODES.put(Figures.FUSA, "mensuralBlackSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
    }

    /**
     * Used by the layout engines
     */
    public static String NOTE_HEAD_WIDTH_CODEPOINT = "noteheadBlack";

    private Group root;

    private ArrayList<Dot> dots;

    private Accidental accidental;

    private PositionInStaff positionInStaff;
    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public NotePitch(LayoutFont layoutFont, LayoutCoreSingleFigureAtom parent, AtomPitch pitch, Coordinate position) throws IM3Exception {
        super(parent, position);
        this.layoutFont = layoutFont;

        atomPitch = pitch;

        root = new Group("NOTE-HEAD-G-"); //TODO IDS

        ScientificPitch scientificPitch =  pitch.getScientificPitch();
        int ndots = pitch.getAtomFigure().getDots();

        // accidentals are computed after all elements are drawn
        positionInStaff = parent.getCoreSymbol().getStaff().computePositionInStaff(pitch.getTime(), scientificPitch.getPitchClass().getNoteName(), scientificPitch.getOctave());
        Coordinate noteHeadPosition = new Coordinate(
                new CoordinateComponent(position.getX()),
                null
        );
        noteHeadPictogram = new Pictogram("NOTE-HEAD-", layoutFont, getUnicode(), noteHeadPosition); //TODO IDS
        root.getChildren().add(noteHeadPictogram);

        if (ndots > 0) {
            dots = new ArrayList<>();
            for (int d = 0; d < ndots; d++) {
                CoordinateComponent dotX = new CoordinateComponent(position.getX(), LayoutConstants.DOT_SEPARATION);
                Coordinate dotPosition = new Coordinate(dotX, null);
                Dot dot = new Dot(layoutFont, this, dotPosition);
                dots.add(dot);
                root.add(dot.getGraphics());
            }
        }
    }

    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        noteHeadPictogram.getPosition().setReferenceY(layoutStaff.computeYPosition(positionInStaff));

        if (dots != null) {
            for (int d = 0; d < dots.size(); d++) {
                CoordinateComponent dotX = new CoordinateComponent(position.getX(), LayoutConstants.DOT_SEPARATION);
                double yDisplacement = 0;
                if (positionInStaff.laysOnLine()) {
                    yDisplacement = -LayoutConstants.SPACE_HEIGHT / 2;
                }
                CoordinateComponent dotY = new CoordinateComponent(noteHeadPictogram.getPosition().getY(), yDisplacement);
                dots.get(d).getPosition().setReferenceY(dotY);
            }
        }
        if (accidental != null) {
            accidental.getPosition().setReferenceY(noteHeadPictogram.getPosition().getY());
        }
    }

    private String getUnicode() throws IM3Exception {
        String unicode = UNICODES.get(parent.getCoreSymbol().getAtomFigure().getFigure());
        if (unicode == null) {
            throw new IM3Exception("Cannot find a font unicode for " + parent.getCoreSymbol().getAtomFigure().getFigure());
        }
        return unicode;
    }

    public AtomPitch getAtomPitch() {
        return atomPitch;
    }

    @Override
    public GraphicsElement getGraphics() {
        return root;
    }

    public PositionInStaff getPositionInStaff() {
        return positionInStaff;
    }
    
    public double getNoteHeadWidth() {
        return noteHeadPictogram.getWidth();
    }

    public Coordinate getNoteHeadPosition() {
        return noteHeadPictogram.getPosition();
    }

    public ScientificPitch getScientificPitch() {
        return atomPitch.getScientificPitch();
    }

    public Accidental getAccidental() {
        return accidental;
    }

    public void removeAccidental() {
        if (accidental == null) {
            throw new IM3RuntimeException("The note pitch didn't have an accidental");
        }
        root.remove(accidental.getGraphics());
        accidental = null;
    }

    public void addAccidental(Accidentals alteration) throws IM3Exception {
        Coordinate alterationPosition = new Coordinate(new CoordinateComponent(noteHeadPictogram.getPosition().getX()), noteHeadPictogram.getPosition().getY());
        accidental = new Accidental(layoutFont, this, alteration, alterationPosition);
        alterationPosition.getX().setDisplacement(-accidental.getWidth() - LayoutConstants.ACCIDENTAL_HEAD_SEPARATION);
        root.add(0, accidental.getGraphics());
    }
}
