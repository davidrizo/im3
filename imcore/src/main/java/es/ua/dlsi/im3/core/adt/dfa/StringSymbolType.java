package es.ua.dlsi.im3.core.adt.dfa;

/**
 * Just in case someone just needs to work with strings
 * @autor drizo
 */
public class StringSymbolType implements IAlphabetSymbolType {
    private final String string;

    public StringSymbolType(String string) {
        this.string = string;
    }

    @Override
    public String getType() {
        return string;
    }

    public String getString() {
        return string;
    }
}
