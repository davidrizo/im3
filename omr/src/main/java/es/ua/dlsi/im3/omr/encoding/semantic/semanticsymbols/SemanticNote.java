package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;

/**
 * @autor drizo
 */
public class SemanticNote extends SemanticAtom<SimpleNote> {
    private static final String SEMANTIC_NOTE = "note" + SEPSYMBOL;
    private static final String SEMANTIC_GRACENOTE = "gracenote" + SEPSYMBOL;
    private Integer tupletNumber;

    private boolean trill;
    private boolean tiedToNext;
    private boolean fermata;
    private SemanticBeamType semanticBeamType;

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
    public SemanticNote(boolean graceNote, ScientificPitch scientificPitch, Accidentals visualAccidental, Figures figures, int dots, boolean fermata, boolean trill, Integer tupletNumber, Boolean colored, Perfection perfection, SemanticBeamType semanticBeamType) throws IM3Exception {
        super(new SimpleNote(figures, dots, scientificPitch));
        setTuplet(tupletNumber);
        this.setFermata(fermata);
        this.semanticBeamType = semanticBeamType;
        this.trill = trill;
        this.coreSymbol.setGrace(graceNote);
        if (visualAccidental != null) {
            this.coreSymbol.setWrittenExplicitAccidental(visualAccidental);
        }
        this.coreSymbol.getAtomFigure().setColored(colored);

        if (perfection != null) {
            try {
                this.coreSymbol.getAtomFigure().setExplicitMensuralPerfection(perfection);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e); // this should never happen
            }
        }
        this.tupletNumber = tupletNumber;
    }

    public void setTiedToNext(boolean tiedToNext) {
        this.tiedToNext = tiedToNext;
    }

    private void setTuplet(Integer tupletNumber) throws IM3Exception {
        this.tupletNumber = tupletNumber;
        if (tupletNumber != null) {
            ((SimpleNote)coreSymbol).setInTuplet(tupletNumber);
        }
    }

    /**
     * tupletNumber If null, it is not a tuplet
     * @param scientificPitch It contains the actual accidental, the one that must be played
     * @param visualAccidental It contains the drawn one (e.g. null if B flat in F major, or sharp in a mensural F major)
     */
    public SemanticNote(boolean graceNote, ScientificPitch scientificPitch, Accidentals visualAccidental, Figures figures, int dots, boolean fermata, boolean trill, Integer tupletNumber, Boolean colored, SemanticBeamType semanticBeamType) throws IM3Exception {
        this(graceNote, scientificPitch, visualAccidental, figures, dots, fermata, trill, tupletNumber, colored, null, semanticBeamType);
        if (tupletNumber != null) {
            setTuplet(tupletNumber);
        }
    }


    public SemanticNote(SimpleNote simpleNote) throws IM3Exception {
        super(simpleNote.clone());
        this.trill = simpleNote.hasTrill();
        this.tiedToNext = simpleNote.getAtomPitch().isTiedToNext();
        this.fermata = simpleNote.getAtomFigure().getFermata() != null;
        BeamGroup beamGroup = simpleNote.getBelongsToBeam();
        if (beamGroup != null) {
            if (beamGroup.getFirstFigure() == simpleNote) {
                this.semanticBeamType = SemanticBeamType.start;
            } else if (beamGroup.getLastFigure() == simpleNote) {
                this.semanticBeamType = SemanticBeamType.end;
            } else {
                this.semanticBeamType = SemanticBeamType.inner;
            }
        }
        if (simpleNote.getParentAtom() != null && simpleNote.getParentAtom() instanceof SimpleTuplet) {
            SimpleTuplet parent = (SimpleTuplet) simpleNote.getParentAtom();
            setTuplet(parent.getCardinality());
        }
    }

    public SemanticBeamType getSemanticBeamType() {
        return semanticBeamType;
    }

    public void setSemanticBeamType(SemanticBeamType semanticBeamType) {
        this.semanticBeamType = semanticBeamType;
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

        for (int i=0; i<dots; i++) {
            sb.append('.');
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

        if (tiedToNext) {
            sb.append(' '); //TODO it was other symbol - ' ' is a symbol separator
            sb.append("tie");
        }
        return sb.toString();
    }

    public boolean isTiedToNext() {
        return tiedToNext;
    }

    public boolean isFermata() {
        return fermata;
    }

    public void setFermata(boolean fermata) {
        this.fermata = fermata;
        if (fermata) {
            this.coreSymbol.getAtomFigure().setFermata(new Fermata());
        } else {
            this.coreSymbol.getAtomFigure().setFermata(null);
        }
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
