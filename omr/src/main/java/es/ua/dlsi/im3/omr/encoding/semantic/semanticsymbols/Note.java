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

    public Note(boolean graceNote, ScientificPitch scientificPitch, Figures figures, int dots, boolean fermata, boolean trill) {
        super(figures, dots, fermata);
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
