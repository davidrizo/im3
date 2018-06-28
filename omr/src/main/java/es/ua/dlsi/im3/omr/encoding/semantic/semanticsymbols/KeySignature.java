package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class KeySignature extends SemanticSymbolType {
    private static final String SEMANTIC = "keySignature" + SEPSYMBOL;

    private DiatonicPitch diatonicPitch;
    private Accidentals accidentals;
    private MajorMinor majorMinor;

    public KeySignature(DiatonicPitch diatonicPitch, Accidentals accidentals, MajorMinor majorMinor) {
        this.diatonicPitch = diatonicPitch;
        this.accidentals = accidentals;
        this.majorMinor = majorMinor;
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder(SEMANTIC);
        sb.append(diatonicPitch.name());
        if (accidentals != null && accidentals != Accidentals.NATURAL) {
            sb.append(accidentals.getAbbrName());
        }
        if (majorMinor != null) {
            sb.append(majorMinor.toSemanticString());
        } else {
            sb.append(MajorMinor.major.toSemanticString());
        }
        return sb.toString();
    }
}
