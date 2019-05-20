package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.IAlphabetSymbolType;
import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;

public abstract class SemanticSymbolType<IMCoreSymbolType extends ITimedElementInStaff> implements Comparable<SemanticSymbolType>, IAlphabetSymbolType {
    protected static String SEPSYMBOL = "-";
    protected static String SEPVALUES = "_";
    protected IMCoreSymbolType coreSymbol;

    public SemanticSymbolType(IMCoreSymbolType coreSymbol) {
        this.coreSymbol = coreSymbol;
    }

    public IMCoreSymbolType getCoreSymbol() {
        return coreSymbol;
    }

    /**
     * @deprecated Use toKernSemanticString
     * Used in PRIMUS v1
     * @return
     */
    public abstract String toSemanticString() throws IM3Exception;
    /**
     * Used after MuRET. Semantic encoding based on kern / mens
     */
    public abstract String toKernSemanticString() throws IM3Exception;

    /**
     * It orders element, first given their name, then given their hashChode
     * @param other
     * @return
     */
    @Override
    public int compareTo(SemanticSymbolType other) {
        int diff = getClass().getName().compareTo(other.getClass().getName());
        if (diff == 0) {
            diff = hashCode() - other.hashCode();
        }
        return diff;
    }

    @Override
    public String getType() {
        return this.getClass().getName();
    }
}
