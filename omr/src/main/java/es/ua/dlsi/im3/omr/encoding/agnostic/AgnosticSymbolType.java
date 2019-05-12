package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.IAlphabetSymbolType;

public abstract class AgnosticSymbolType implements Comparable<AgnosticSymbolType>, IAlphabetSymbolType {
    public static String SEPSYMBOL = ".";
    public static String SEPPROPERTIES = "_";

    
    public abstract String toAgnosticString();

    /**
     * Required for the AgnosticSymbolTypeFactory
     */
    public AgnosticSymbolType() {
    }
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
    /**
     * The meaning depends on the agnostic type
     * @param string
     */
    public abstract void setSubtype(String string) throws IM3Exception;

    @Override
    public String getType() {
        return this.getClass().getName();
    }

    @Override
    public String toString() {
        return toAgnosticString();
    }
}
