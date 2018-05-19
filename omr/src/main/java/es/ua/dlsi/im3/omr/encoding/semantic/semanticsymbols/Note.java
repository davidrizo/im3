package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class Note extends DurationalSymbol {
    private static final String SEMANTIC_NOTE = "note" + SEPSYMBOL;
    private static final String SEMANTIC_GRACENOTE = "gracenote" + SEPSYMBOL;
    private static final String TRILL = "trill";

    boolean trill;
    boolean graceNote;
    ScientificPitch scientificPitch;

    /**
     * @param graceNote
     * @param scientificPitch
     * @param figures
     * @param dots
     * @param fermata
     * @param trill
     * @param tupletNumber If null, it is not a tuplet
     */
    public Note(boolean graceNote, ScientificPitch scientificPitch, Figures figures, int dots, boolean fermata, boolean trill, Integer tupletNumber) {
        super(figures, dots, fermata, tupletNumber);
        this.trill = trill;
        this.graceNote = graceNote;
        this.scientificPitch = scientificPitch;
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder();
        if (graceNote) {
            sb.append(SEMANTIC_GRACENOTE);
        } else {
            sb.append(SEMANTIC_NOTE);
        }

        sb.append(scientificPitch.getPitchClass().getNoteName());
        if (scientificPitch.getPitchClass().isAltered()) {
            sb.append(scientificPitch.getPitchClass().getAccidental().getAbbrName());
        }
        sb.append(scientificPitch.getOctave());
        sb.append(SEPVALUES);
        sb.append(figures.name().toLowerCase()); //TODO ¿Para moderno y mensural?

        for (int i=0; i<dots; i++) {
            sb.append('.');
        }

        if (tupletNumber != null) {
            sb.append(SEPVALUES);
            sb.append(TUPLET);
            sb.append(tupletNumber);
        }

        if (fermata) {
            sb.append(SEPVALUES);
            sb.append(FERMATA);
        }
        if (trill) {
            sb.append(SEPVALUES);
            sb.append(TRILL);
        }

        return sb.toString();
    }
}
