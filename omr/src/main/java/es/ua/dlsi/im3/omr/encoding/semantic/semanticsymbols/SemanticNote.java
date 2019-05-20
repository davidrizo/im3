package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;

/**
 * @autor drizo
 */
public class SemanticNote extends SemanticAtom<SimpleNote> {
    private static final String SEMANTIC_NOTE = "note" + SEPSYMBOL;
    private static final String SEMANTIC_GRACENOTE = "gracenote" + SEPSYMBOL;

    private boolean trill;
    private boolean tied;
    private boolean fermata;

    /*
    protected static final String TUPLET = "tuplet";

    boolean trill;
    boolean graceNote;
*/
    /**
     * @deprecated
     */
    //boolean fermata;

    /**
     * @deprecated
     */
    // Integer tupletNumber;

    /**
     * tupletNumber If null, it is not a tuplet
     */
    /*public SemanticNote(boolean graceNote, ScientificPitch scientificPitch, Figures figures, int dots, boolean fermata, boolean trill, Integer tupletNumber) {
        super(new SimpleNote(figures, dots, scientificPitch));
        this.fermata = fermata;
        this.tupletNumber = tupletNumber;
        this.trill = trill;
        this.graceNote = graceNote;
    }*/

    public SemanticNote(SimpleNote simpleNote) {
        super(simpleNote.clone());
        this.trill = simpleNote.hasTrill();
        this.tied = simpleNote.getAtomPitch().isTiedToNext();
        this.fermata = simpleNote.getAtomFigure().getFermata() != null;
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder();
        if (coreSymbol.isGrace()) {
            sb.append(SEMANTIC_GRACENOTE);
        } else {
            sb.append(SEMANTIC_NOTE);
        }

        ScientificPitch scientificPitch = coreSymbol.getPitch();
        sb.append(scientificPitch.getPitchClass().getNoteName());
        if (scientificPitch.getPitchClass().isAltered()) {
            sb.append(scientificPitch.getPitchClass().getAccidental().getAbbrName());
        }
        sb.append(scientificPitch.getOctave());
        sb.append(SEPVALUES);

        Figures figures = coreSymbol.getAtomFigure().getFigure();
        int dots = coreSymbol.getAtomFigure().getDots();

        sb.append(figures.name().toLowerCase()); //TODO ¿Para moderno y mensural?

        for (int i=0; i<dots; i++) {
            sb.append('.');
        }

        /*TODO if (tupletNumber != null) {
            sb.append(SEPVALUES);
            sb.append(TUPLET);
            sb.append(tupletNumber);
        }*/

        if (fermata) {
            sb.append(SEPVALUES);
            sb.append(FERMATA);
        }

        if (trill) {
            sb.append(SEPVALUES);
            sb.append(TRILL);
        }

        if (tied) {
            sb.append(' '); //TODO it was other symbol - ' ' is a symbol separator
            sb.append("tie");
        }
        return sb.toString();
    }

    public boolean isTiedToNext() {
        return tied;
    }

    /*@Override
    public void semantic2IMCore(SemanticConversionContext semanticConversionContext, List<ITimedElementInStaff> conversionResult) throws IM3Exception {
        //TODO tuplets, fermata, trill, stems, beams ...
        SimpleNote note = new SimpleNote(figures, dots, scientificPitch);

        if (semanticConversionContext.hasPendingTie()) {
            semanticConversionContext.removePendingTie();
            if (semanticConversionContext.getPendingPitchesToTie().isEmpty()) {
                throw new IM3Exception("Missing pitches to tie");
            }
            //TODO cojo la primera
            AtomPitch atomPitch = semanticConversionContext.getPendingPitchesToTie().remove(0);
            note.getAtomPitch().setTiedFromPrevious(atomPitch);
            //lastNote.tieToNext(note);
        }
        conversionResult.add(note);
    }*/
}
