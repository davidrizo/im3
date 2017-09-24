package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.SingleFigureAtom;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Flag;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Stem;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;
import java.util.List;

public class LayoutSingleFigureAtom extends LayoutSymbolWithDuration<SingleFigureAtom> {
    Group group;
    ArrayList<NotePitch> notePitches;
    Stem stem;
    Flag flag;

    public LayoutSingleFigureAtom(LayoutStaff layoutStaff, SingleFigureAtom coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);

        LayoutFont layoutFont = layoutStaff.getScoreLayout().getLayoutFont();
        group = new Group("SINGLE_FIG_"); //TODO IDS

        notePitches = new ArrayList<>();

        boolean stemUp = true; // FIXME: 22/9/17 step up or down depending on beams

        double stemXDisplacement = 0;
        CoordinateComponent stemYPosition = null;
        for (AtomPitch atomPitch: coreSymbol.getAtomPitches()) {
            NotePitch notePitch = new NotePitch(layoutFont, this, atomPitch, position);
            notePitches.add(notePitch);
            group.add(notePitch.getGraphics());

            // FIXME: 22/9/17 Esto funciona cuando es una nota, en acordes?
            stemUp = notePitch.getPositionInStaff().getLine() <= 2;
            if (stemUp) {
                stemXDisplacement = notePitch.getNoteHeadWidth();
            }
            stemYPosition = notePitch.getNoteHeadPosition().getY();

            layoutStaff.addNecessaryLedgerLinesFor(atomPitch.getTime(), notePitch.getPositionInStaff(), notePitch.getPosition(), notePitch.getWidth() );
        }



        if (coreSymbol.getAtomFigure().getFigure().usesStem()) {
            Coordinate stemPosition = new Coordinate(
                    new CoordinateComponent(position.getX(), stemXDisplacement),
                    stemYPosition
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
}
