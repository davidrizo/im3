package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.adt.dfa.IAlphabetSymbolType;

public abstract class SemanticSymbolType implements Comparable<SemanticSymbolType>, IAlphabetSymbolType {
    protected static String SEPSYMBOL = "-";
    protected static String SEPVALUES = "_";
    public abstract String toSemanticString();

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
