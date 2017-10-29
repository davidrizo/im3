package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;

public class LayoutCoreKeySignature extends LayoutCoreSymbolInStaff<KeySignature> {
    Group group;
    ArrayList<Accidental> accidentals;
    private PositionInStaff[] positionInStaffs;

    public LayoutCoreKeySignature(LayoutFont layoutFont, KeySignature coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
        createAccidentals(layoutFont);
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    private void createAccidentals(LayoutFont layoutFont) throws IM3Exception {
        group = new Group("KEYSIG-"); //TODO IDS
        accidentals = new ArrayList<>();
        /*
        int previousNoteOrder = 0;
        Accidentals accidental = coreSymbol.getAccidental();
        DiatonicPitch[] alteredNoteNames = coreSymbol.getInstrumentKey().getAlteredNoteNames();
        boolean nextUp = (accidental == Accidentals.SHARP);
        int i = 1;
        double nextRelativeXPosition = 0;
        int octave = getStartingOctave();
        for (DiatonicPitch nn : alteredNoteNames) {
            int noteOrder = nn.getHorizontalOrderInStaff() + octave * 7;
            if (i > 1) {
                if (nextUp) {
                    if (noteOrder < previousNoteOrder) {
                        octave++;
                    }
                } else { // next down
                    if (noteOrder > previousNoteOrder) {
                        octave--;
                    }
                }
            }
            previousNoteOrder = nn.getHorizontalOrderInStaff() + octave * 7;
            nextUp = !nextUp;

            CoordinateComponent x = new CoordinateComponent(position.getX(), nextRelativeXPosition);
            CoordinateComponent y = layoutStaff.computeYPositionForPitchWithoutClefOctaveChange(getTime(), nn, octave);
            Coordinate position = new Coordinate(x, y);

            Accidental p = new Accidental(layoutFont, this, accidental, nn, octave, position);
            nextRelativeXPosition += p.getWidth();
            addComponent(p);
            i++;
        }*/

        positionInStaffs = coreSymbol.computePositionsOfAccidentals();
        if (positionInStaffs != null) {
            double nextRelativeXPosition = 0;
            for (int i=0; i<positionInStaffs.length; i++) {
                CoordinateComponent x = new CoordinateComponent(position.getX(), nextRelativeXPosition);
                CoordinateComponent y = null;
                Coordinate position = new Coordinate(x, y);

                Accidental p = new Accidental(layoutFont, this, coreSymbol.getAccidental(), position);
                nextRelativeXPosition += p.getWidth();
                addAccidentalComponent(p);

            }
        }
    }

    @Override
    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        super.setLayoutStaff(layoutStaff);
        if (positionInStaffs != null) {
            double nextRelativeXPosition = 0;
            for (int i=0; i<positionInStaffs.length; i++) {
                CoordinateComponent y = layoutStaff.computeYPosition(positionInStaffs[i]);
                accidentals.get(i).getPosition().setReferenceY(y);
            }
        }
    }

    private void addAccidentalComponent(Accidental p) {
        accidentals.add(p);
        group.add(p.getGraphics());
    }

    public int getStartingOctave() {
        if (coreSymbol.getAccidental().equals(Accidentals.NATURAL)) {
            return 0;
        } else {
            Clef clef = layoutStaff.getStaff().getClefAtTime(getTime());
            return clef.getStartingOctave(coreSymbol.getAccidental());
        }
    }
}
