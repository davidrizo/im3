package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.BeamedGroup;
import es.ua.dlsi.im3.core.score.CompoundAtom;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class LayoutBeamGroup extends CompoundLayout {
    BeamedGroup beamedGroup;
    Group group;

    public LayoutBeamGroup(BeamedGroup beamedGroup) {
        this.beamedGroup = beamedGroup;
    }

    /**
     * Create the beams between the notes
     */
    public void createBeams() throws IM3Exception {
        group = new Group("BEAM-"); //TODO ID
        // TODO: 1/10/17 Importante !!! No funciona con grupos de varias duraciones
        Integer flags = null;
        for (LayoutCoreSymbol coreSymbol: this.coreSymbols) {
            if (!(coreSymbol instanceof LayoutCoreSingleFigureAtom)) {
                throw new IM3Exception("Unsupported groups of non LayoutCoreSingleFigureAtom"); // FIXME: 1/10/17 Rests, different durations
            }
            LayoutCoreSingleFigureAtom lcoreSingleFigureAtom = ((LayoutCoreSingleFigureAtom)coreSymbol);
            int thisSymbolFlags = lcoreSingleFigureAtom.getCoreSymbol().getAtomFigure().getFigure().getNumFlags();
            if (flags != null) {
                if (flags != thisSymbolFlags) {
                    throw new IM3Exception("Unsupported mixed figure beams, there are figures with number of flags: " + flags + " and " + thisSymbolFlags);
                }
            } else {
                flags = thisSymbolFlags;
            }
            lcoreSingleFigureAtom.removeFlag();
        }

        if (this.coreSymbols.size() < 2) {
            throw new IM3Exception("Cannot create beams with less than 2 elements, there are: " + this.coreSymbols.size()); // TODO: 1/10/17 Broken beam of 1 note
        }
        LayoutCoreSingleFigureAtom from = (LayoutCoreSingleFigureAtom) coreSymbols.get(0);
        LayoutCoreSingleFigureAtom to = (LayoutCoreSingleFigureAtom) coreSymbols.get(this.coreSymbols.size()-1);

        // TODO: 1/10/17 En lugar de line un polígono
        double displacementY = 0;
        for (int i=0; i<flags; i++) {
            Coordinate fromPosition = new Coordinate(
                    new CoordinateComponent(from.getStemEnd().getX()),
                    new CoordinateComponent(from.getStemEnd().getY(), displacementY));

            Coordinate toPosition = new Coordinate(
                    new CoordinateComponent(to.getStemEnd().getX()),
                    new CoordinateComponent(to.getStemEnd().getY(), displacementY));

            Line line = new Line("BEAMLINE-", fromPosition, toPosition);
            group.add(line);
            displacementY += LayoutConstants.BEAM_SEPARATION; // FIXME: 1/10/17 Dirección según plica - extender plica
        }

    }

    public Group getGraphicsElement() {
        if (group == null) {
            throw new IM3RuntimeException("createBeams not invoked");
        }
        return group;
    }
}
