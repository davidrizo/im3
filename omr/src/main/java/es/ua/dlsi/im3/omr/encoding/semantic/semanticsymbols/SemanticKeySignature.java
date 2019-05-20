package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

import java.util.List;

/**
 * @autor drizo
 */
public class SemanticKeySignature extends SemanticSymbolType<KeySignature> {
    private static final String SEMANTIC = "keySignature" + SEPSYMBOL;

    /*public SemanticKeySignature(NotationType notationType, DiatonicPitch diatonicPitch, Accidentals accidentals, MajorMinor majorMinor) throws IM3Exception {
        super(new es.ua.dlsi.im3.core.score.KeySignature(notationType, toKey(diatonicPitch, accidentals, majorMinor)));
    }*/

    public SemanticKeySignature(KeySignature coreSymbol) {
        super(coreSymbol.clone());
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder(SEMANTIC);
        sb.append(coreSymbol.getInstrumentKey().getPitchClass().getNoteName().name());
        Accidentals accidentals = coreSymbol.getInstrumentKey().getPitchClass().getAccidental();
        if (accidentals != null && accidentals != Accidentals.NATURAL) {
            sb.append(accidentals.getAbbrName());
        }
        if (coreSymbol.getInstrumentKey().getMode() != null && coreSymbol.getInstrumentKey().getMode() != Mode.UNKNOWN) {
            sb.append(coreSymbol.getInstrumentKey().getMode().getNameChar());
        } else {
            sb.append(MajorMinor.major.toSemanticString());
        }
        return sb.toString();
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        return "*" + KernExporter.generateKeySignature(this.coreSymbol);
    }

    private static Key toKey(DiatonicPitch diatonicPitch, Accidentals accidentals, MajorMinor majorMinor) throws IM3Exception {
        Mode mode;
        if (majorMinor != null) {
            mode = Mode.stringToMode(majorMinor.toSemanticString());
        } else {
            mode = Mode.UNKNOWN;
        }
        Key key = new Key(new PitchClass(diatonicPitch, accidentals), mode);
        return key;
    }
}
