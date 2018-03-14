package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.ArrayList;

public class NotePitch extends Component<LayoutCoreSingleFigureAtom> implements IConnectableWithSlurInStaff {

    private Pictogram noteHeadPictogram;
    private final AtomPitch atomPitch;
    private final LayoutFont layoutFont;

    private Group root;

    /**
     * Dots that accompany
     */
    private ArrayList<Dot> dots;

    /**
     * Independent dots
     */
    private ArrayList<Dot> displacedDots;

    private LayoutScoreLyric layoutScoreLyric;

    private Accidental accidental;

    private PositionInStaff positionInStaff;

    CoordinateComponent dotsYCoordinate;

    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public NotePitch(LayoutFont layoutFont, LayoutCoreSingleFigureAtom parent, AtomPitch pitch, Coordinate position, PositionInStaff positionInStaff) throws IM3Exception {
        super(pitch, parent, position);
        this.layoutFont = layoutFont;

        atomPitch = pitch;

        root = new Group(this, InteractionElementType.notePitch); //TODO IDS

        ScientificPitch scientificPitch =  pitch.getScientificPitch();
        int ndots = pitch.getAtomFigure().getDots();

        // accidentals are computed after all elements are drawn
        /*Staff staff = parent.getCoreSymbol().getStaff();
        if (staff == null) {
            throw new IM3Exception("The symbol " + parent.getCoreSymbol() + " has not staff");
        }*/
        //positionInStaff = parent.getCoreSymbol().getStaff().computePositionInStaff(pitch.getTime(), scientificPitch.getPitchClass().getNoteName(), scientificPitch.getOctave());
        this.positionInStaff = positionInStaff;
        Coordinate noteHeadPosition = new Coordinate(
                new CoordinateComponent(position.getX()),
                null
        );
        noteHeadPictogram = new Pictogram(this, InteractionElementType.noteHead, layoutFont, getUnicode(), noteHeadPosition); //TODO IDS
        root.getChildren().add(noteHeadPictogram);

        if (ndots > 0) {
            dotsYCoordinate = new CoordinateComponent();
        }
        int requiredDots = ndots;
        if (pitch.getDisplacedDots() != null) {
            ndots -= pitch.getDisplacedDots().size(); // just draw the non displaced dots (the ones not drawn as core symbols)
        }
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

        /*if (atomPitch.getDisplacedDots() != null) {
            for (DisplacedDot displacedDot: atomPitch.getDisplacedDots()) {
                Coordinate dotPosition = new Coordinate(null, null); // it must be computed by the layout algorithm given its time and order
                LayoutCoreDisplacedDot layoutDisplacedDot = new LayoutCoreDisplacedDot(layoutFont, displacedDot, this, dotPosition);
                root.add(layoutDisplacedDot.getGraphics());
            }
        }
        // 28/10/17 Resto de attachments
        */

        if (atomPitch.getLyrics() != null && !atomPitch.getLyrics().isEmpty()) {
            CoordinateComponent lx = new CoordinateComponent(position.getX());
            Coordinate lyricPos = new Coordinate(lx, null);

            layoutScoreLyric = new LayoutScoreLyric(this, layoutFont, lyricPos);
            lx.setDisplacement(-layoutScoreLyric.getWidth() / 2); // center
            root.add(layoutScoreLyric.getGraphics());
        }
    }

    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        noteHeadPictogram.getPosition().setReferenceY(layoutStaff.computeYPosition(positionInStaff));

        if (dotsYCoordinate != null) {
            double yDisplacement = 0;
            if (positionInStaff.laysOnLine()) {
                yDisplacement = -LayoutConstants.SPACE_HEIGHT / 2;
            }
            dotsYCoordinate.setDisplacement(yDisplacement);
            dotsYCoordinate.setReference(noteHeadPictogram.getPosition().getY());
                    //= new CoordinateComponent(noteHeadPictogram.getPosition().getY(), yDisplacement);
        }
        if (dots != null) {
            for (int d = 0; d < dots.size(); d++) {
                CoordinateComponent dotX = new CoordinateComponent(position.getX(), LayoutConstants.DOT_SEPARATION);
                dots.get(d).getPosition().setReferenceY(dotsYCoordinate);
            }
        }
        if (accidental != null) {
            accidental.getPosition().setReferenceY(noteHeadPictogram.getPosition().getY());
        }
        if (layoutScoreLyric != null) {
            layoutScoreLyric.getPosition().setReferenceY(layoutStaff.getBottomLine().getPosition().getY());
        }
    }

    private String getUnicode() throws IM3Exception {
        boolean stemUp = parent.isStemUp();

        String unicode = layoutFont.getFontMap().getUnicode(parent.getCoreSymbol().getAtomFigure().getFigure(), stemUp);
        if (unicode == null) {
            throw new IM3Exception("Cannot find a font unicode for " + parent.getCoreSymbol().getAtomFigure().getFigure());
        }
        return unicode;
    }

    public boolean headIncludesFlag() throws IM3Exception {
        boolean stemUp = parent.isStemUp();

        return !layoutFont.getFontMap().getUnicode(parent.getCoreSymbol().getAtomFigure().getFigure(), false).equals(
                layoutFont.getFontMap().getUnicodeWihoutFlag(parent.getCoreSymbol().getAtomFigure().getFigure(), stemUp)
        );
    }

    public void useHeadWithoutFlag() throws IM3Exception {
        boolean stemUp = parent.isStemUp();

        Pictogram newNoteHeadPictogram = new Pictogram(this, noteHeadPictogram.getInteractionElementType(), layoutFont, //TODO ID
                layoutFont.getFontMap().getUnicodeWihoutFlag(parent.getCoreSymbol().getAtomFigure().getFigure(), stemUp),
                noteHeadPictogram.getPosition());

        root.remove(noteHeadPictogram);
        root.add(newNoteHeadPictogram);
        noteHeadPictogram = newNoteHeadPictogram;
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

    @Override
    public Direction getDefaultSlurDirection() {
        if (parent.isStemUp()) {
            return Direction.down;
        } else {
            return Direction.up;
        }
    }

    // TODO: 20/11/17 Debe poder ser distinto al del padre como en core
    @Override
    public LayoutStaff getLayoutStaff() {
        return this.parent.getLayoutStaff();
    }

    // TODO: 1/10/17 El enganche en la plica se hará con el LayoutCoreSingleFigureAtom, no aquí
    @Override
    public Coordinate getConnectionPoint(Direction direction) {
        double xdiplacement = noteHeadPictogram.getWidth() / 2;

        double ydisplacement;
        if (direction == Direction.up) {
            ydisplacement = -LayoutConstants.SEPARATION_NOTE_SLUR;
        } else {
            ydisplacement = LayoutConstants.SEPARATION_NOTE_SLUR;
        }

        return new Coordinate(new CoordinateComponent(getNoteHeadPosition().getX(), xdiplacement),
                new CoordinateComponent(getNoteHeadPosition().getY(), ydisplacement));
    }

    public Pictogram getNoteHeadPictogram() {
        return noteHeadPictogram;
    }

    public CoordinateComponent getDotsYCoordinate() {
        return dotsYCoordinate;
    }

    @Override
    protected void doLayout() throws IM3Exception {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
