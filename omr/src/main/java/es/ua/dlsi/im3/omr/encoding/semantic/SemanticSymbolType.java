package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.IAlphabetSymbolType;
import es.ua.dlsi.im3.core.score.ScoreLayer;

public abstract class SemanticSymbolType implements Comparable<SemanticSymbolType>, IAlphabetSymbolType {
    protected static String SEPSYMBOL = "-";
    protected static String SEPVALUES = "_";

    /**
     * Used in PRIMUS v1
     * @return
     */
    public abstract String toSemanticString();
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

    /**
     *
     * @param scoreLayer
     * @param propagatedSymbolType
     * @return Symbol to propagate
     * @throws IM3Exception
     */
    public abstract SemanticSymbolType semantic2ScoreSong(ScoreLayer scoreLayer, SemanticSymbolType propagatedSymbolType) throws IM3Exception;
}
