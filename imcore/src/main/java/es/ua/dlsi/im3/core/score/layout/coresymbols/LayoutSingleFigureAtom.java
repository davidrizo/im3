package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.SingleFigureAtom;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Stem;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;

public class LayoutSingleFigureAtom extends LayoutSymbolWithDuration<SingleFigureAtom> {
    Group group;
    ArrayList<NotePitch> heads;
    Stem stem;

    public LayoutSingleFigureAtom(LayoutStaff layoutStaff, SingleFigureAtom coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);
        group = new Group();

        heads = new ArrayList<>();

        if (coreSymbol.getAtomFigure().getFigure().usesStem()) {
            stem = new Stem(this, position, false); // TODO: 21/9/17 stem up or down
            group.add(stem.getGraphics());
        }

        for (AtomPitch atomPitch: coreSymbol.getAtomPitches()) {
            NotePitch notePitch = new NotePitch(layoutStaff.getScoreLayout().getLayoutFont(), this, atomPitch, position);
            group.add(notePitch.getGraphics());


            layoutStaff.addNecessaryLedgerLinesFor(atomPitch.getTime(), notePitch.getPositionInStaff(), notePitch.getPosition(), notePitch.getWidth() );
        }
        
        //// FIXME: 21/9/17 Move stem to the correct position given step up / down and note heads

    }
    // TODO: 21/9/17 FLAGS


    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    @Override
    public Time getDuration() {
        return coreSymbol.getDuration();
    }

}
