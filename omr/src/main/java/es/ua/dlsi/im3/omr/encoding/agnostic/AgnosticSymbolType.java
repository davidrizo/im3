package es.ua.dlsi.im3.omr.encoding.agnostic;

public abstract class AgnosticSymbolType implements Comparable<AgnosticSymbolType> {
    protected static String SEPSYMBOL = ".";
    public abstract String toAgnosticString();

    /**
     * It orders element given the class name
     * @param other
     * @return
     */
    @Override
    public int compareTo(AgnosticSymbolType other) {
        int diff = getClass().getName().compareTo(other.getClass().getName());
        return diff;
    }


    /**
     * Class hashCode
     * @return
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Class equals
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass();
    }
}
