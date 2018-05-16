package es.ua.dlsi.im3.core.adt.dfa;

public class Token<AlphabetSymbolType extends IAlphabetSymbolType> implements Comparable<Token<AlphabetSymbolType>> {
    /**
     * Symbol type, depends on the given alphabet
     */
    protected AlphabetSymbolType symbol;

    public Token(AlphabetSymbolType symbol) {
        this.symbol = symbol;
    }

    public AlphabetSymbolType getSymbol() {
        return symbol;
    }

    @Override
    public int compareTo(Token<AlphabetSymbolType> o) {
        return symbol.getType().compareTo(o.symbol.getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token<?> token = (Token<?>) o;

        return symbol.getType().equals(token.symbol.getType());
    }

    @Override
    public int hashCode() {
        return symbol.getType().hashCode();
    }
}
