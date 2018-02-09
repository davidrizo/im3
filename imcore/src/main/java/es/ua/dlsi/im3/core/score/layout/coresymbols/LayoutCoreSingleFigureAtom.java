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
    private boolean stemUp;
    Group group;
    ArrayList<NotePitch> notePitches;
    Stem stem;
    Flag flag;

    public LayoutCoreSingleFigureAtom(LayoutFont layoutFont, SingleFigureAtom coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);

        group = new Group("SINGLE_FIG_"); //TODO IDS

        notePitches = new ArrayList<>();

        stemUp = true; // FIXME: 22/9/17 step up or down depending on beams

        double stemXDisplacement = 0;
        CoordinateComponent stemYPosition = null;
        for (AtomPitch atomPitch: coreSymbol.getAtomPitches()) {
            NotePitch notePitch = new NotePitch(layoutFont, this, atomPitch, position);
            notePitches.add(notePitch);
            addComponent(notePitch); // TODO: 28/10/17 Añadir todos los demás componentes !!!!
            group.add(notePitch.getGraphics());

            // FIXME: 22/9/17 Esto funciona cuando es una nota, en acordes?
            if (coreSymbol.getExplicitStemDirection() != null) {
                stemUp = coreSymbol.getExplicitStemDirection() == StemDirection.up;
            } else {
                stemUp = notePitch.getPositionInStaff().getLine() <= 2; // TODO actually we should check surrounding notes (Behind bars book)
            }

            if (stemUp) {
                stemXDisplacement = notePitch.getNoteHeadWidth();
            }
        }


        if (coreSymbol.getAtomFigure().getFigure().usesStem()) {
            Coordinate stemPosition = new Coordinate(
                    new CoordinateComponent(position.getX(), stemXDisplacement),
                    null
            );
            stem = new Stem(this, stemPosition, stemUp);
            group.add(stem.getGraphics());

            if (coreSymbol.getAtomFigure().getFigure().usesFlag()) {
                Coordinate flagPosition = stem.getLineEnd();
                flag = new Flag(layoutFont, this, coreSymbol.getAtomFigure().getFigure(), flagPosition, stemUp);
                group.add(flag.getGraphics());
            }
        }


        //// FIXME: 21/9/17 Move stem to the correct position given step up / down and note heads
        /*double stemXDisplacement = 0;

        if (!stemUp) {
            stemXDisplacement -
        }*/

    }

    @Override
    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        //TODO Para acordes
        CoordinateComponent stemYPosition = null;
        super.setLayoutStaff(layoutStaff);
        for (NotePitch notePitch: notePitches) {
            // TODO: 24/9/17 ¿Y si ya las tenía?
            notePitch.setLayoutStaff(layoutStaff);
            layoutStaff.addNecessaryLedgerLinesFor(notePitch.getAtomPitch().getTime(), notePitch.getPositionInStaff(), notePitch.getPosition(), notePitch.getNoteHeadWidth());
            stemYPosition = notePitch.getNoteHeadPosition().getY();
        }

        if (stem != null) {
            stem.setReferenceY(stemYPosition);
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
            throw new IM3Exception("Unsupported " + notePitches.size() + " pitches for removeFlag");
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
                ydisplacement = LayoutConstants.SEPARATION_NOTE_SLUR;
            } else {
                ydisplacement = -LayoutConstants.SEPARATION_NOTE_SLUR;
            }
            Coordinate connectionPoint = new Coordinate(
                    notePitches.get(0).getNoteHeadPosition().getX(),
                    new CoordinateComponent(notePitches.get(0).getNoteHeadPosition().getY(), ydisplacement));
            return connectionPoint;
        }
        //return position;
    }


}
