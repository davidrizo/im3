package es.ua.dlsi.im3.omr.encoding.semantic;

public abstract class SemanticSymbolType implements Comparable<SemanticSymbolType> {
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
}
