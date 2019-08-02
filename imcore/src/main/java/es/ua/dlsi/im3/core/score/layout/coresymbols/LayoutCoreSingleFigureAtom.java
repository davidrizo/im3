package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Flag;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Stem;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Not used for rests
 */
public class LayoutCoreSingleFigureAtom extends LayoutCoreSymbolWithDuration<SingleFigureAtom>  implements IConnectableWithSlurInStaff {
    private double stemXDisplacement;
    private double headWidth;
    private boolean stemUp;
    Group group;
    ArrayList<NotePitch> notePitches;
    Stem stem;
    Flag flag;

    public LayoutCoreSingleFigureAtom(LayoutFont layoutFont, SingleFigureAtom coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);

        group = new Group(this, InteractionElementType.singleFigureAtom);

        notePitches = new ArrayList<>();

        stemXDisplacement = 0;
        CoordinateComponent stemYPosition = null;

        int sumLinePosition = 0;

        for (AtomPitch atomPitch: coreSymbol.getAtomPitches()) {
            // Computed here and not inside NodePitch because the position is required to compute the stem direction
            // and the stem is required for computing some notePitch elements at constructor such as the unicode for
            // some mensural glyphs
            PositionInStaff positionInStaff = getCoreSymbol().getStaff().computePositionInStaff(getTime(), atomPitch.getScientificPitch().getPitchClass().getNoteName(),
                    atomPitch.getScientificPitch().getOctave());

            sumLinePosition += positionInStaff.getLineSpace();

            NotePitch notePitch = new NotePitch(layoutFont, this, atomPitch, position, positionInStaff);
            notePitches.add(notePitch);
            addComponent(notePitch); // TODO: 28/10/17 Añadir todos los demás componentes !!!!
            group.add(notePitch.getGraphics());

            headWidth = notePitch.getNoteHeadWidth();
        }
        if (coreSymbol.getExplicitStemDirection() != null) {
            stemUp = coreSymbol.getExplicitStemDirection() == StemDirection.up;
        } else {
            int avgSumLinePosition = sumLinePosition / coreSymbol.getAtomPitches().size();
            //stemUp = positionInStaff.getLine() <= 2; // TODO actually we should check surrounding notes (Behind bars book)
            stemUp = avgSumLinePosition < PositionsInStaff.LINE_3.getLineSpace();
        }

        if (coreSymbol.getAtomFigure().getFigure().usesStem()) {
            Coordinate stemPosition = new Coordinate(
                    new CoordinateComponent(position.getX()),
                    null
            );

            if (coreSymbol.getAtomFigure().getFigure().usesCombinedStemAndFlag()) {
                if (stemUp) {
                    stemXDisplacement = headWidth / 2;
                    stemPosition.setDisplacementX(stemXDisplacement-1);
                } else {
                    stemPosition.setDisplacementX(1);
                }

                stem = new Stem(this, stemPosition, stemUp);
                Coordinate flagPosition = stem.getPosition(); // just used for computing the position
                flag = new Flag(layoutFont, this, coreSymbol.getAtomFigure().getFigure(), flagPosition, stemUp);
                group.add(flag.getGraphics());
            } else {
                if (stemUp) {
                    stemXDisplacement = headWidth;
                    stemPosition.setDisplacementX(stemXDisplacement-1);
                } else {
                    stemPosition.setDisplacementX(1);
                }

                stem = new Stem(this, stemPosition, stemUp);
                group.add(stem.getGraphics()); // if combined flag, the stem is just used to locate the flag position

                if (coreSymbol.getAtomFigure().getFigure().usesFlag()) {
                    Coordinate flagPosition = stem.getLineEnd();
                    flag = new Flag(layoutFont, this, coreSymbol.getAtomFigure().getFigure(), flagPosition, stemUp);
                    group.add(flag.getGraphics());
                }
            }

        }
    }


    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    @Override
    public Time getDuration() {
        return coreSymbol.getDuration();
    }

    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }

    public List<NotePitch> getNotePitches() {
        return notePitches;
    }

    public boolean isStemUp() {
        return stemUp;
    }

    public Coordinate getStemEnd() throws IM3Exception {
        if (stem != null) {
            return stem.getLineEnd();
        } else {
            // the stem is included in the glyph
            if (notePitches.size() != 1) {
                // TODO: 17/10/17  
                throw new IM3Exception("Unsupported " + notePitches.size() + " pitches for getStemEnd");
            }
            NotePitch refHead = notePitches.get(0);
            double ydisplacement;
            double xdisplacement;
            if (stemUp) {
                ydisplacement = -refHead.getNoteHeadPictogram().getHeight();
                xdisplacement = 0;
            } else {
                ydisplacement = refHead.getNoteHeadPictogram().getHeight() - 4; //TODO A piñón el -4;
                xdisplacement = refHead.getNoteHeadWidth() / 2; //TODO A piñón el /2
            }
            Coordinate result = new Coordinate(
                    new CoordinateComponent(refHead.getNoteHeadPosition().getX(), xdisplacement),
                    new CoordinateComponent(refHead.getNoteHeadPosition().getY(), ydisplacement));
            return result;
        }
    }

    public void removeFlag() throws IM3Exception {
        if (notePitches.size() != 1) {
            // TODO: 17/10/17 Por mensural
            if (this.getCoreSymbol().getStaff().getNotationType() == NotationType.eMensural) {
                throw new IM3Exception("Unsupported " + notePitches.size() + " pitches for removeFlag");
            }
        }
        if (notePitches.get(0).headIncludesFlag()) {
            notePitches.get(0).useHeadWithoutFlag();
        } else {
            if (flag != null) {
                group.remove(flag.getGraphics());
                flag = null;
            }
        }
    }

    @Override
    public Direction getDefaultSlurDirection() {
        if (stemUp) {
            return Direction.down;
        } else {
            return Direction.up;
        }
    }

    @Override
    public Coordinate getConnectionPoint(Direction direction) {
        // TODO: 31/10/17 para acordes y posición
        if (notePitches == null || notePitches.isEmpty()) {
            return position;
        } else {
            double ydisplacement;
            if (stemUp) {
                ydisplacement = LayoutConstants.VERTICAL_SEPARATION_NOTE_SLUR;
            } else {
                ydisplacement = -LayoutConstants.VERTICAL_SEPARATION_NOTE_SLUR;
            }
            Coordinate connectionPoint = new Coordinate(
                    notePitches.get(0).getNoteHeadPosition().getX(),
                    new CoordinateComponent(notePitches.get(0).getNoteHeadPosition().getY(), ydisplacement));
            return connectionPoint;
        }
        //return position;
    }
    @Override
    protected void doLayout() throws IM3Exception {
        for (NotePitch notePitch: notePitches) {
            ScientificPitch scientificPitch = notePitch.getScientificPitch();
            PositionInStaff positionInStaff = getCoreSymbol().getStaff().computePositionInStaff(getTime(),scientificPitch.getPitchClass().getNoteName(),
                    scientificPitch.getOctave());
            notePitch.setPositionInStaff(positionInStaff); //TODO Esto no está bien así - está duplicado el código con la construcción

            // TODO: 24/9/17 ¿Y si ya las tenía?
            notePitch.setLayoutStaff(layoutStaff);
            notePitch.layout();
            layoutStaff.addNecessaryLedgerLinesFor(notePitch.getAtomPitch().getTime(), notePitch.getPositionInStaff(), notePitch.getPosition(), notePitch.getNoteHeadWidth());
        }

        if (stem != null) {
            if (stemUp) {
                stem.setStartY(getBottomNote().getNoteHeadPosition().getY(), 0);
                stem.setEndY(new CoordinateComponent(getTopNote().getNoteHeadPosition().getY()), -LayoutConstants.STEM_HEIGHT);
            } else {
                stem.setStartY(getTopNote().getNoteHeadPosition().getY(), 0);
                stem.setEndY(new CoordinateComponent(getBottomNote().getNoteHeadPosition().getY()), LayoutConstants.STEM_HEIGHT);
            }
        }
    }

    @Override
    public int getNumBeams() {
        return this.coreSymbol.getAtomFigure().getFigure().getNumFlags();
    }

    @Override
    public double getBottomPitchAbsoluteY() throws IM3Exception {
        double y = Double.MIN_VALUE;
        for (NotePitch notePitch: notePitches) {
            y = Math.max(y, notePitch.getNoteHeadPosition().getAbsoluteY());
        }

        return y;
    }

    @Override
    public double getTopPitchAbsoluteY() throws IM3Exception {
        double y = Double.MAX_VALUE;
        for (NotePitch notePitch: notePitches) {
            y = Math.min(y, notePitch.getNoteHeadPosition().getAbsoluteY());
        }

        return y;
    }

    // TODO: 4/5/18 Cuando es acorde y tenemos en los dos lados del stem
    public void setStemUp(boolean stemUp) {
        if (stem != null) {
            if (this.stemUp != stemUp) {
                this.stemUp = stemUp;

                stem.changeStemDirection();
            }

            if (this.stemUp) {
                stemXDisplacement = headWidth;
            } else {
                stemXDisplacement = 0;
            }
            stem.setXDisplacement(stemXDisplacement);
        }
    }

    public NotePitch getBottomNote() throws IM3Exception {
        double maxY = -Double.MAX_VALUE;
        NotePitch result = null;
        for (NotePitch notePitch: notePitches) {
            double absY = notePitch.getNoteHeadPosition().getAbsoluteY();
            if (absY > maxY) {
                maxY = absY;
                result = notePitch;
            }
        }
        return result;
    }

    public NotePitch getTopNote() throws IM3Exception {
        double minY = Double.MAX_VALUE;
        NotePitch result = null;
        for (NotePitch notePitch: notePitches) {
            double absY = notePitch.getNoteHeadPosition().getAbsoluteY();
            if (absY < minY) {
                minY = absY;
                result = notePitch;
            }
        }
        return result;
    }

    public void setStemEndY(CoordinateComponent referenceY, double ydisplacement) {
        if (this.stem != null) {
            this.stem.setEndY(referenceY, ydisplacement);
        }
    }

    public void setStemEndY(double stemFromYAbsolute) throws IM3Exception {
        if (this.stem != null) {
            this.stem.setEndAbsoluteY(stemFromYAbsolute);
        }
    }
}
