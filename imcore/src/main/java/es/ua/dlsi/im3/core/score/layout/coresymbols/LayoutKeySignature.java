package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;

public class LayoutKeySignature extends LayoutSymbolInStaff<KeySignature> {
    Group group;
    ArrayList<Accidental> accidentals;

    public LayoutKeySignature(LayoutStaff layoutStaff, KeySignature coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);
        createAccidentals(layoutStaff.getScoreLayout().getLayoutFont());
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    private void createAccidentals(LayoutFont layoutFont) throws IM3Exception {
        group = new Group();
        accidentals = new ArrayList<>();
        int octave = 0;
        int previousNoteOrder = 0;

        Accidentals accidental = coreSymbol.getAccidental();
        DiatonicPitch[] alteredNoteNames = coreSymbol.getInstrumentKey().getAlteredNoteNames();
        boolean nextUp = (accidental == Accidentals.SHARP);
        int i = 1;
        double nextRelativeXPosition = 0;
        int oct = getStartingOctave() + octave;
        for (DiatonicPitch nn : alteredNoteNames) {
            int noteOrder = nn.getOrder() + octave * 7;
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
            previousNoteOrder = nn.getOrder() + octave * 7;
            nextUp = !nextUp;

            CoordinateComponent x = new CoordinateComponent(position.getX(), nextRelativeXPosition);
            CoordinateComponent y = layoutStaff.computeYPositionForPitchWithoutClefOctaveChange(getTime(), nn, oct);
            Coordinate position = new Coordinate(x, y);

            Accidental p = new Accidental(layoutFont, this, accidental, i, nn, octave, position);
            nextRelativeXPosition += p.getWidth();
            addComponent(p);
            i++;
        }
    }

    private void addComponent(Accidental p) {
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
