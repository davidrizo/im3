package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
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
     * tupletNumber If null, it is not a tuplet
     * @param scientificPitch It contains the actual accidental, the one that must be played
     * @param visualAccidental It contains the drawn one (e.g. null if B flat in F major, or sharp in a mensural F major)
     */
    public SemanticNote(boolean graceNote, ScientificPitch scientificPitch, Accidentals visualAccidental, Figures figures, int dots, boolean fermata, boolean trill, Integer tupletNumber, Boolean colored, Perfection perfection)  {
        super(new SimpleNote(figures, dots, scientificPitch));
        this.fermata = fermata;
        this.trill = trill;
        this.coreSymbol.setGrace(graceNote);
        if (visualAccidental != null) {
            this.coreSymbol.setWrittenExplicitAccidental(visualAccidental);
        }
        this.coreSymbol.getAtomFigure().setColored(colored);

        this.coreSymbol.getAtomFigure().setExplicitMensuralPerfection(perfection);
        //TODO Tuplet y fermata en el CoreSymbol
    }

    /**
     * tupletNumber If null, it is not a tuplet
     * @param scientificPitch It contains the actual accidental, the one that must be played
     * @param visualAccidental It contains the drawn one (e.g. null if B flat in F major, or sharp in a mensural F major)
     */
    public SemanticNote(boolean graceNote, ScientificPitch scientificPitch, Accidentals visualAccidental, Figures figures, int dots, boolean fermata, boolean trill, Integer tupletNumber, Boolean colored) {
        this(graceNote, scientificPitch, visualAccidental, figures, dots, fermata, trill, tupletNumber, colored, null);
    }

    public SemanticNote(SimpleNote simpleNote) {
        super(simpleNote.clone());
        this.trill = simpleNote.hasTrill();
        this.tied = simpleNote.getAtomPitch().isTiedToNext();
        this.fermata = simpleNote.getAtomFigure().getFermata() != null;
    }

    @Override
    public String toSemanticString()  {
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

        if (coreSymbol.getAtomFigure().isColored()) {
            sb.append('~');
        }

        /*//TODO código copiado en SemanticRest
        if (coreSymbol.getAtomFigure().isExplicitMensuralPerfection()) {
            Perfection perfection = coreSymbol.getAtomFigure().getMensuralPerfection();
            if (perfection != null) {
                switch (perfection) {
                    case perfectum:
                        sb.append('p');
                        break;
                    case imperfectum:
                        sb.append('i');
                        break;
                    default:
                        throw new IM3Exception("Unsupported perfection type: " + perfection);
                }
            } else {
                throw new IM3Exception("Expected a perfection");
            }
        }*/

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
