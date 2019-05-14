package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

import java.util.List;

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

    @Override
    public String toKernSemanticString() throws IM3Exception {
        Key key = toKey();
        es.ua.dlsi.im3.core.score.KeySignature ks = new es.ua.dlsi.im3.core.score.KeySignature(null, key);
        return "*" + KernExporter.generateKeySignature(ks);
    }

    @Override
    public void semantic2ScoreSong(SemanticConversionContext semanticConversionContext, List<ITimedElementInStaff> conversionResult) throws IM3Exception {
        Key key = toKey();
        es.ua.dlsi.im3.core.score.KeySignature ks = new es.ua.dlsi.im3.core.score.KeySignature(semanticConversionContext.getNotationType(), key);
        conversionResult.add(ks);
        semanticConversionContext.setCurrentKeySignature(ks);
    }

    private Key toKey() throws IM3Exception {
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
